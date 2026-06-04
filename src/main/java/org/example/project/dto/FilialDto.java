package org.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilialDto {
    private String title;
    private String description;
    private String workHours;
    private String phoneNumber;
    private Double latitude;
    private Double longitude;
}
