package ru.otus.msa.user.adapter.in.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import ru.otus.msa.user.adapter.out.kafka.UserEventMapper;
import ru.otus.msa.user.adapter.out.kk.KeycloakClient;
import ru.otus.msa.user.adapter.out.pg.repository.entity.User;
import ru.otus.msa.user.api.http.dto.LoginRequestDto;
import ru.otus.msa.user.api.http.dto.RegisterUserDto;
import ru.otus.msa.user.api.http.dto.UserInfoDto;
import ru.otus.msa.user.api.http.dto.UserTokenDto;
import ru.otus.msa.user.api.kafka.dto.UserEvent;
import ru.otus.msa.user.application.UserService;

import java.util.UUID;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final KeycloakClient keycloakClient;
    private final UserService userService;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final UserEventMapper userEventMapper;

    @PostMapping("/register")
    public User registration(@RequestBody RegisterUserDto request) {
        UUID keycloakClientUserId = keycloakClient.createUser(request);//->rest
        User user = userService.create(keycloakClientUserId, request);//->db
        kafkaTemplate.send(kafkaTemplate.getDefaultTopic(), keycloakClientUserId.toString(), userEventMapper.map(user));//->kafka
        return user;
    }

    @PostMapping("/login")
    public UserTokenDto login(@RequestBody LoginRequestDto loginRequest) {
        return keycloakClient.login(loginRequest);
    }

    @GetMapping("/user-info")
    public UserInfoDto login(@RequestHeader("Authorization") String token) {
        return keycloakClient.getUserInfo(token);
    }
}
