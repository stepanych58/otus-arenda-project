package ru.otus.msa.user.application;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.otus.msa.user.adapter.out.pg.entity.User;
import ru.otus.msa.user.adapter.out.pg.repository.UserFilter;
import ru.otus.msa.user.api.http.dto.RegisterUserDto;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {
    Page<User> getAll(UserFilter filter, Pageable pageable);

    User getOne(UUID id);

    List<User> getMany(List<UUID> ids);

    User create(UUID keycloakClientUserId, RegisterUserDto user);

    User patch(UUID id, JsonNode patchNode) throws IOException;

    List<User> patchMany(List<UUID> ids, JsonNode patchNode) throws IOException;

    User delete(UUID id);

    boolean isUserExistByEmail(String email);
}
