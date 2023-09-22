package com.kocobiet.project.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Data // to string
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    @JsonProperty("first_name")
    @NotEmpty(message = "first name is required")
    private String firstName;

    @JsonProperty("last_name")
    @NotEmpty(message = "last name is required")
    private String lastName;

    @NotBlank(message = "email cannot be blank")
    private String email;

    @NotBlank(message = "password cannot be blank")
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("role_id")
    @NotNull(message = "Role id is required")
    private Long roleId;
}
