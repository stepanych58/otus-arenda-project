package ru.otus.msa.user.adapter.in.http;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import ru.otus.msa.user.adapter.out.pg.repository.UserFilter;
import ru.otus.msa.user.application.UserService;
import ru.otus.msa.user.application.exception.InvalidXUserIdException;
import ru.otus.msa.user.adapter.out.pg.repository.entity.User;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Tag(name = "user-service CRUD", description = "API to work with user data")
@RestController
@RequestMapping("/user-service/api/users")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Slf4j
public class UserResource {

    private final UserService userService;

    @GetMapping
    public PagedModel<User> getAll(
            @Parameter(description = "ID вызывающего пользователя", required = true)
            @RequestHeader("Authorization") String authorization,
            @ModelAttribute UserFilter filter, Pageable pageable) {
        Page<User> users = userService.getAll(filter, pageable);
        return new PagedModel<>(users);
    }

    @GetMapping("/{id}")
    public User getOne(
            @Parameter(description = "ID вызывающего пользователя", required = true)
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id) {
        return userService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<User> getMany(
            @Parameter(description = "ID вызывающего пользователя", required = true)
            @RequestHeader("Authorization") String authorization,
            @RequestParam List<UUID> ids) {
        return userService.getMany(ids);
    }

    @Operation(
            summary = "Обновление пользователя",
            description = "Позволяет обновить отдельные поля пользователя по его ID. "
                    + "Требует указания заголовка `x-user-id` для идентификации вызывающего пользователя. "
                    + "Если вызывающий пользователь совпадает с ID в path, будет выброшено исключение."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлён",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "Редактирование запрещено"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PatchMapping("/{id}")
    public User patch(
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "ID пользователя, которого нужно обновить", required = true)
            @PathVariable UUID id,

            @Parameter(description = "JSON-объект с изменяемыми полями", required = true,
                    schema = @Schema(type = "object"))
            @RequestBody JsonNode patchNode
    ) throws IOException {
        UUID xUserId = JwtUtils.extractSubFromAuthorizationHeader(authorization);
        if (!Objects.equals(xUserId, id)) {
            throw new InvalidXUserIdException("Пользователю запрещено редактирование другого пользователя");
        }
        return userService.patch(id, patchNode);
    }

//    @PatchMapping
//    public List<User> patchMany(@RequestParam List<UUID> ids, @RequestBody JsonNode patchNode) throws IOException {
//        return userService.patchMany(ids, patchNode);
//    }

    //todo soft deletion + delete from keycloak
    @DeleteMapping("/{id}")
    public User delete(
            @Parameter(description = "ID вызывающего пользователя", required = true)
            @RequestHeader("x-user-id") UUID xUserId,
            @PathVariable UUID id) {
        return userService.delete(id);
    }

//    @DeleteMapping
//    public void deleteMany(@RequestParam List<UUID> ids) {
//        userService.deleteMany(ids);
//    }
}
