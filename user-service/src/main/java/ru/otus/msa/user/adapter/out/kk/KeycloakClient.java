package ru.otus.msa.user.adapter.out.kk;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import ru.otus.msa.user.api.http.dto.LoginRequestDto;
import ru.otus.msa.user.api.http.dto.RegisterUserDto;
import ru.otus.msa.user.api.http.dto.UserInfoDto;
import ru.otus.msa.user.api.http.dto.UserTokenDto;
import ru.otus.msa.user.adapter.out.kk.security.KeycloakConfig;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakClient {
    private final KeycloakConfig config;
    private final Keycloak keycloakAdmin;
    private final RestClient keyclockRestClient;

    public UUID createUser(RegisterUserDto request) {
        try {
            UsersResource usersResource = keycloakAdmin.realm(config.getRealm()).users();
            UserRepresentation user = new UserRepresentation();
            String mail = request.getEmail();
            user.setUsername(mail.substring(0, mail.indexOf('@')));
            user.setEmail(mail);
            user.setEmailVerified(true);
            user.setEnabled(true);
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getFirstName());

            Response response = usersResource.create(user);
            if (response.getStatus() != HttpStatus.CREATED.value()) {
                log.error("create state not 201");
                throw new RuntimeException("Не удалось создать пользователя");
            }
            String userId = CreatedResponseUtil.getCreatedId(response);
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());

            usersResource.get(userId).resetPassword(credential);
            return UUID.fromString(userId);
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException("Не удалось создать пользователя");
        }
    }

    public UserTokenDto login(LoginRequestDto request) {
        String url = config.getServerUrl() + "/realms/" + config.getRealm() + "/protocol/openid-connect/token";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", config.getClientId());
        form.add("client_secret", config.getClientSecret());
        form.add("scope", "openid");
        form.add("username", request.username());
        form.add("password", request.password());

        try {
            return keyclockRestClient
                    .post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(UserTokenDto.class);
        } catch (RestClientResponseException e) {
            log.error("Not possible to login", e);
            throw e;
        }
    }

    public UserInfoDto getUserInfo(String token) {
        String url = config.getServerUrl() + "/realms/" + config.getRealm() + "/protocol/openid-connect/userinfo";

        try {
            return keyclockRestClient
                    .get()
                    .uri(url)
                    .header("Authorization", token)
                    .retrieve()
                    .body(UserInfoDto.class);
        } catch (RestClientResponseException e) {
            log.error("Not possible to get user info", e);
            throw e;
        }
    }
}
