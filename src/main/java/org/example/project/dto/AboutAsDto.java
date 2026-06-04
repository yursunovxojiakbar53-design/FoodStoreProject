package org.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AboutAsDto {
    private String title;
    private String description;
    private String phoneNumber;
    private Integer attachmentId;
    private String telegramBotUrl;
}

