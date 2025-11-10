package ru.otus.msa.user.api.http.dto;

import lombok.Data;

@Data
public class RegisterUserDto {
    private String email;
    private String firstName;
    private String lastName;
    private Boolean gender;
    private String password;
}
