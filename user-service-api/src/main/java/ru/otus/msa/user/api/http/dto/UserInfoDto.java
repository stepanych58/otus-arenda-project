package ru.otus.msa.user.api.http.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserInfoDto(@JsonProperty("sub") UUID sub,
                          @JsonProperty("email_verified") boolean emailVerified,
                          @JsonProperty("name") String name,
                          @JsonProperty("preferred_username") String preferredUsername,
                          @JsonProperty("given_name") String givenName,
                          @JsonProperty("family_name") String familyName,
                          @JsonProperty("email") String email
) {
}