package com.studymind.config;

import java.awt.Desktop;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
public class SwaggerBrowserLauncher {

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${app.auto-open-swagger:true}")
    private boolean autoOpenSwagger;

    @EventListener(ApplicationReadyEvent.class)
    public void openSwaggerUi() {
        String swaggerUrl = "http://localhost:" + serverPort + "/swagger-ui/index.html";
        String healthUrl = "http://localhost:" + serverPort + "/api/v1/health";

        log.info("=================================================");
        log.info(" StudyMind Backend is running!");
        log.info(" Swagger UI : {}", swaggerUrl);
        log.info(" Health API : {}", healthUrl);
        log.info("=================================================");

        if (!autoOpenSwagger) {
            return;
        }

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(swaggerUrl));
                log.info("Opened Swagger UI in default browser.");
                return;
            }

            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", swaggerUrl});
                log.info("Opened Swagger UI in default browser.");
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", swaggerUrl});
                log.info("Opened Swagger UI in default browser.");
            } else {
                Runtime.getRuntime().exec(new String[]{"xdg-open", swaggerUrl});
                log.info("Opened Swagger UI in default browser.");
            }
        } catch (Exception ex) {
            log.warn("Could not auto-open browser. Open manually: {}", swaggerUrl);
        }
    }
}
