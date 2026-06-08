package org.example.project.telegram.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
@Getter
@Setter
public class TelegramBotProperties {
    private String token;
    private String username;
    private boolean enabled = true;
    private String adminChatIds = "";
    private int pageSize = 5;
    private String backendBaseUrl = "http://localhost:8081";

    public List<Long> getAdminChatIdList() {
        if (adminChatIds == null || adminChatIds.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(adminChatIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
