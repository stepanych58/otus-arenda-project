package ru.otus.msa.user.adapter.out.pg;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.msa.user.adapter.out.pg.repository.entity.User;
import ru.otus.msa.user.api.http.dto.RegisterUserDto;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RegisterUserMapper {

    @Mapping(target = "id", source = "keycloakClientUserId")
    User map(UUID keycloakClientUserId, RegisterUserDto registerUserDto);
}
