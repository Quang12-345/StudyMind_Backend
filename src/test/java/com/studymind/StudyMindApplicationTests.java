package com.studymind;

import com.studymind.repository.AiJobRepository;
import com.studymind.repository.ChatMessageRepository;
import com.studymind.repository.ChatSessionRepository;
import com.studymind.repository.DeckRepository;
import com.studymind.repository.DocumentChunkRepository;
import com.studymind.repository.DocumentTextRepository;
import com.studymind.repository.FlashcardRepository;
import com.studymind.repository.QuizAttemptRepository;
import com.studymind.repository.QuizQuestionRepository;
import com.studymind.repository.QuizRepository;
import com.studymind.repository.StudyDocumentRepository;
import com.studymind.repository.SummaryRepository;
import com.studymind.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class StudyMindApplicationTests {

    @MockBean private UserRepository userRepository;
    @MockBean private StudyDocumentRepository studyDocumentRepository;
    @MockBean private DocumentTextRepository documentTextRepository;
    @MockBean private DocumentChunkRepository documentChunkRepository;
    @MockBean private SummaryRepository summaryRepository;
    @MockBean private DeckRepository deckRepository;
    @MockBean private FlashcardRepository flashcardRepository;
    @MockBean private QuizRepository quizRepository;
    @MockBean private QuizQuestionRepository quizQuestionRepository;
    @MockBean private QuizAttemptRepository quizAttemptRepository;
    @MockBean private ChatSessionRepository chatSessionRepository;
    @MockBean private ChatMessageRepository chatMessageRepository;
    @MockBean private AiJobRepository aiJobRepository;

    @Test
    void contextLoads() {
    }
}
