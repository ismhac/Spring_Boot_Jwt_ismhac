package com.kocobiet.project.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data // to string
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDto {
    @NotBlank(message = "email cannot be blank")
    private String email;

    @NotBlank(message = "password cannot be blank")
    private String password;

    @JsonProperty("role_id")
    @Min(value = 1, message = "You must enter role's Id")
    private Long roleId;
}
