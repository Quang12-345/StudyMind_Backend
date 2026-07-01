package com.studymind.service;

import com.studymind.exception.BadRequestException;
import com.studymind.exception.BadRequestException;
import com.studymind.model.AiJob;
import com.studymind.model.Deck;
import com.studymind.model.DocumentChunk;
import com.studymind.model.DocumentText;
import com.studymind.model.Flashcard;
import com.studymind.model.Quiz;
import com.studymind.model.QuizQuestion;
import com.studymind.model.StudyDocument;
import com.studymind.model.Summary;
import com.studymind.model.embedded.SummarySection;
import com.studymind.model.enums.AiJobStatus;
import com.studymind.model.enums.AiJobType;
import com.studymind.model.enums.DocumentStatus;
import com.studymind.model.enums.ProcessingStepStatus;
import com.studymind.model.enums.QuestionDifficulty;
import com.studymind.model.enums.QuizQuestionType;
import com.studymind.repository.AiJobRepository;
import com.studymind.repository.DeckRepository;
import com.studymind.repository.DocumentChunkRepository;
import com.studymind.repository.DocumentTextRepository;
import com.studymind.repository.FlashcardRepository;
import com.studymind.repository.QuizQuestionRepository;
import com.studymind.repository.QuizRepository;
import com.studymind.repository.StudyDocumentRepository;
import com.studymind.repository.SummaryRepository;
import com.studymind.service.storage.FileStorageService;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

@Service
public class AiPipelineService {

    private static final int CHUNK_SIZE = 800;
    private static final String STUB_MODEL = "studymind-stub-v1";

    private final StudyDocumentRepository documentRepository;
    private final DocumentTextRepository documentTextRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final SummaryRepository summaryRepository;
    private final DeckRepository deckRepository;
    private final FlashcardRepository flashcardRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final AiJobRepository aiJobRepository;
    private final FileStorageService fileStorageService;

    public AiPipelineService(
            StudyDocumentRepository documentRepository,
            DocumentTextRepository documentTextRepository,
            DocumentChunkRepository documentChunkRepository,
            SummaryRepository summaryRepository,
            DeckRepository deckRepository,
            FlashcardRepository flashcardRepository,
            QuizRepository quizRepository,
            QuizQuestionRepository quizQuestionRepository,
            AiJobRepository aiJobRepository,
            FileStorageService fileStorageService
    ) {
        this.documentRepository = documentRepository;
        this.documentTextRepository = documentTextRepository;
        this.documentChunkRepository = documentChunkRepository;
        this.summaryRepository = summaryRepository;
        this.deckRepository = deckRepository;
        this.flashcardRepository = flashcardRepository;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.aiJobRepository = aiJobRepository;
        this.fileStorageService = fileStorageService;
    }

    public Summary regenerateSummary(StudyDocument document) {
        if (!Boolean.TRUE.equals(document.getTextExtracted())) {
            throw new BadRequestException("Document text must be extracted before regenerating summary");
        }
        String text = documentTextRepository.findByDocumentId(document.getId())
                .map(DocumentText::getContent)
                .orElseThrow(() -> new BadRequestException("Document text not found"));
        runSummarize(document, text);
        return summaryRepository.findById(document.getSummaryId())
                .orElseThrow(() -> new BadRequestException("Summary regeneration failed"));
    }

    public StudyDocument processDocument(StudyDocument document) {
        if (document.getStatus() == DocumentStatus.PROCESSING) {
            throw new BadRequestException("Document is already being processed");
        }

        document.setStatus(DocumentStatus.PROCESSING);
        document.setErrorMessage(null);
        documentRepository.save(document);

        try {
            String text = extractText(document);
            saveDocumentText(document, text);
            document.setTextExtracted(true);
            document.setCharacterCount(text.length());
            document.getProcessingSteps().setExtractText(ProcessingStepStatus.DONE);

            runSummarize(document, text);
            runGenerateFlashcards(document, text);
            runGenerateQuiz(document, text);
            runIndexChunks(document, text);

            document.setStatus(DocumentStatus.READY);
            return documentRepository.save(document);
        } catch (Exception ex) {
            document.setStatus(DocumentStatus.FAILED);
            document.setErrorMessage(ex.getMessage());
            documentRepository.save(document);
            throw new BadRequestException("Document processing failed: " + ex.getMessage());
        }
    }

    private String extractText(StudyDocument document) throws IOException {
        AiJob job = createJob(document, AiJobType.EXTRACT_TEXT);
        try {
            Path filePath = fileStorageService.load(document.getStorageKey());
            try (PDDocument pdf = Loader.loadPDF(filePath.toFile())) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(pdf).trim();
                document.setPageCount(pdf.getNumberOfPages());
                completeJob(job, null);
                return text.isBlank() ? "No text could be extracted from this PDF." : text;
            }
        } catch (Exception ex) {
            failJob(job, ex.getMessage());
            throw ex;
        }
    }

    private void saveDocumentText(StudyDocument document, String text) {
        DocumentText documentText = documentTextRepository.findByDocumentId(document.getId())
                .orElseGet(DocumentText::new);
        documentText.setDocumentId(document.getId());
        documentText.setContent(text);
        documentText.setCharacterCount(text.length());
        documentText.setPageCount(document.getPageCount());
        documentTextRepository.save(documentText);
    }

    private void runSummarize(StudyDocument document, String text) {
        AiJob job = createJob(document, AiJobType.SUMMARIZE);
        try {
            markPreviousSummariesNotLatest(document.getId());

            Summary summary = new Summary();
            summary.setDocumentId(document.getId());
            summary.setUserId(document.getUserId());
            summary.setShortSummary(buildShortSummary(text));
            summary.setDetailedSummary(buildDetailedSummary(text, document.getTitle()));
            summary.setKeyPoints(buildKeyPoints(text));
            summary.setSections(buildSummarySections(text));
            summary.setModel(STUB_MODEL);
            summaryRepository.save(summary);

            document.setSummaryId(summary.getId());
            document.getProcessingSteps().setSummary(ProcessingStepStatus.DONE);
            documentRepository.save(document);
            completeJob(job, summary.getId());
        } catch (Exception ex) {
            document.getProcessingSteps().setSummary(ProcessingStepStatus.FAILED);
            failJob(job, ex.getMessage());
            throw ex;
        }
    }

    private void runGenerateFlashcards(StudyDocument document, String text) {
        AiJob job = createJob(document, AiJobType.GENERATE_FLASHCARDS);
        try {
            markPreviousDecksNotLatest(document.getId());

            Deck deck = new Deck();
            deck.setDocumentId(document.getId());
            deck.setUserId(document.getUserId());
            deck.setTitle("Flashcards — " + document.getTitle());
            deckRepository.save(deck);

            List<Flashcard> cards = buildFlashcards(document, deck.getId(), text);
            flashcardRepository.saveAll(cards);

            deck.setCardCount(cards.size());
            deckRepository.save(deck);

            document.setDeckId(deck.getId());
            document.getProcessingSteps().setFlashcards(ProcessingStepStatus.DONE);
            documentRepository.save(document);
            completeJob(job, deck.getId());
        } catch (Exception ex) {
            document.getProcessingSteps().setFlashcards(ProcessingStepStatus.FAILED);
            failJob(job, ex.getMessage());
            throw ex;
        }
    }

    private void runGenerateQuiz(StudyDocument document, String text) {
        AiJob job = createJob(document, AiJobType.GENERATE_QUIZ);
        try {
            markPreviousQuizzesNotLatest(document.getId());

            Quiz quiz = new Quiz();
            quiz.setDocumentId(document.getId());
            quiz.setUserId(document.getUserId());
            quiz.setTitle("Quiz — " + document.getTitle());
            quiz.setTimeLimitMinutes(15);
            quizRepository.save(quiz);

            List<QuizQuestion> questions = buildQuizQuestions(document, quiz.getId(), text);
            quizQuestionRepository.saveAll(questions);

            quiz.setQuestionCount(questions.size());
            quizRepository.save(quiz);

            document.setQuizId(quiz.getId());
            document.getProcessingSteps().setQuiz(ProcessingStepStatus.DONE);
            documentRepository.save(document);
            completeJob(job, quiz.getId());
        } catch (Exception ex) {
            document.getProcessingSteps().setQuiz(ProcessingStepStatus.FAILED);
            failJob(job, ex.getMessage());
            throw ex;
        }
    }

    private void runIndexChunks(StudyDocument document, String text) {
        AiJob job = createJob(document, AiJobType.INDEX_CHUNKS);
        try {
            documentChunkRepository.deleteByDocumentId(document.getId());

            List<DocumentChunk> chunks = splitIntoChunks(document.getId(), text);
            documentChunkRepository.saveAll(chunks);

            document.getProcessingSteps().setIndexing(ProcessingStepStatus.DONE);
            documentRepository.save(document);
            completeJob(job, null);
        } catch (Exception ex) {
            document.getProcessingSteps().setIndexing(ProcessingStepStatus.FAILED);
            failJob(job, ex.getMessage());
            throw ex;
        }
    }

    private List<DocumentChunk> splitIntoChunks(String documentId, String text) {
        List<DocumentChunk> chunks = new ArrayList<>();
        int index = 0;
        for (int start = 0; start < text.length(); start += CHUNK_SIZE) {
            int end = Math.min(start + CHUNK_SIZE, text.length());
            String content = text.substring(start, end).trim();
            if (content.isBlank()) {
                continue;
            }
            DocumentChunk chunk = new DocumentChunk();
            chunk.setDocumentId(documentId);
            chunk.setChunkIndex(index++);
            chunk.setContent(content);
            chunk.setTokenCount(content.split("\\s+").length);
            chunks.add(chunk);
        }
        if (chunks.isEmpty()) {
            DocumentChunk chunk = new DocumentChunk();
            chunk.setDocumentId(documentId);
            chunk.setChunkIndex(0);
            chunk.setContent("Empty document content.");
            chunk.setTokenCount(3);
            chunks.add(chunk);
        }
        return chunks;
    }

    private List<Flashcard> buildFlashcards(StudyDocument document, String deckId, String text) {
        List<String> sentences = pickSentences(text, 5);
        List<Flashcard> cards = new ArrayList<>();
        for (int i = 0; i < sentences.size(); i++) {
            String sentence = sentences.get(i);
            Flashcard card = new Flashcard();
            card.setDeckId(deckId);
            card.setDocumentId(document.getId());
            card.setUserId(document.getUserId());
            card.setFront("What is the key idea in: \"" + truncate(sentence, 80) + "\"?");
            card.setBack(sentence);
            card.setOrder(i + 1);
            cards.add(card);
        }
        return cards;
    }

    private List<QuizQuestion> buildQuizQuestions(StudyDocument document, String quizId, String text) {
        List<String> sentences = pickSentences(text, 3);
        List<QuizQuestion> questions = new ArrayList<>();

        for (int i = 0; i < sentences.size(); i++) {
            String sentence = sentences.get(i);
            QuizQuestion question = new QuizQuestion();
            question.setQuizId(quizId);
            question.setDocumentId(document.getId());
            question.setOrder(i + 1);
            question.setDifficulty(QuestionDifficulty.MEDIUM);
            question.setType(QuizQuestionType.MULTIPLE_CHOICE);
            question.setQuestion("Which statement best matches this content?");
            question.setOptions(List.of(
                    sentence,
                    "This topic is unrelated to the document.",
                    "The document does not mention this subject.",
                    "None of the above"
            ));
            question.setCorrectIndex(0);
            question.setExplanation("The correct answer is taken directly from the document text.");
            questions.add(question);
        }

        if (!sentences.isEmpty()) {
            QuizQuestion trueFalse = new QuizQuestion();
            trueFalse.setQuizId(quizId);
            trueFalse.setDocumentId(document.getId());
            trueFalse.setOrder(sentences.size() + 1);
            trueFalse.setDifficulty(QuestionDifficulty.EASY);
            trueFalse.setType(QuizQuestionType.TRUE_FALSE);
            trueFalse.setQuestion("This document contains extractable text content.");
            trueFalse.setOptions(List.of("True", "False"));
            trueFalse.setCorrectIndex(0);
            trueFalse.setExplanation("Text was successfully extracted from the uploaded PDF.");
            questions.add(trueFalse);
        }

        return questions;
    }

    private String buildShortSummary(String text) {
        return truncate(text.replaceAll("\\s+", " ").trim(), 300);
    }

    private String buildDetailedSummary(String text, String title) {
        return "Summary of \"" + title + "\":\n\n" + truncate(text.replaceAll("\\s+", " ").trim(), 1500);
    }

    private List<String> buildKeyPoints(String text) {
        return pickSentences(text, 5).stream()
                .map(s -> truncate(s, 120))
                .toList();
    }

    private List<SummarySection> buildSummarySections(String text) {
        List<String> sentences = pickSentences(text, 3);
        List<SummarySection> sections = new ArrayList<>();
        for (int i = 0; i < sentences.size(); i++) {
            SummarySection section = new SummarySection();
            section.setTitle("Section " + (i + 1));
            section.setContent(sentences.get(i));
            sections.add(section);
        }
        return sections;
    }

    private List<String> pickSentences(String text, int max) {
        String normalized = text.replaceAll("\\s+", " ").trim();
        if (normalized.isBlank()) {
            return List.of("No meaningful content was found in this document.");
        }
        String[] parts = normalized.split("(?<=[.!?])\\s+");
        List<String> sentences = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.length() >= 20) {
                sentences.add(trimmed);
            }
            if (sentences.size() >= max) {
                break;
            }
        }
        if (sentences.isEmpty()) {
            sentences.add(truncate(normalized, 200));
        }
        return sentences;
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }

    private AiJob createJob(StudyDocument document, AiJobType type) {
        AiJob job = new AiJob();
        job.setDocumentId(document.getId());
        job.setUserId(document.getUserId());
        job.setType(type);
        job.setStatus(AiJobStatus.RUNNING);
        job.setStartedAt(Instant.now());
        return aiJobRepository.save(job);
    }

    private void completeJob(AiJob job, String resultId) {
        job.setStatus(AiJobStatus.COMPLETED);
        job.setResultId(resultId);
        job.setCompletedAt(Instant.now());
        aiJobRepository.save(job);
    }

    private void failJob(AiJob job, String errorMessage) {
        job.setStatus(AiJobStatus.FAILED);
        job.setErrorMessage(errorMessage);
        job.setCompletedAt(Instant.now());
        aiJobRepository.save(job);
    }

    private void markPreviousSummariesNotLatest(String documentId) {
        summaryRepository.findByDocumentIdAndIsLatestTrue(documentId).ifPresent(summary -> {
            summary.setIsLatest(false);
            summaryRepository.save(summary);
        });
    }

    private void markPreviousDecksNotLatest(String documentId) {
        deckRepository.findByDocumentIdAndIsLatestTrue(documentId).ifPresent(deck -> {
            deck.setIsLatest(false);
            deckRepository.save(deck);
        });
    }

    private void markPreviousQuizzesNotLatest(String documentId) {
        quizRepository.findByDocumentIdAndIsLatestTrue(documentId).ifPresent(quiz -> {
            quiz.setIsLatest(false);
            quizRepository.save(quiz);
        });
    }
}
