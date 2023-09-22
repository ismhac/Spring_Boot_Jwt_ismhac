package com.kocobiet.project.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kocobiet.project.models.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("token")
    private String token;
}