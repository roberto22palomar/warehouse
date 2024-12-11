package com.example.warehouse.auth.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RegisterRequest {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;


}
