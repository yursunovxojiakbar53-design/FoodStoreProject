package org.example.project.extra;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {
    private String message;
    private boolean status;
    private Object data;

    public ApiResponse(String invalidCredentials, boolean b) {
        this.message = invalidCredentials;
        this.status = b;
    }
}
