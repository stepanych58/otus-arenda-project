package ru.otus.msa.user.api.http.dto;

import lombok.Data;
import ru.otus.msa.user.api.common.PickupPointDto;

@Data
public class RegisterUserDto {
    private String email;

    private String firstName;

    private String lastName;

    private Boolean gender;

    private String password;
    private PickupPointDto pickupPoint;
}
