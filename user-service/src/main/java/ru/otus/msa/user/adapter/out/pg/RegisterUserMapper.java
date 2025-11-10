package ru.otus.msa.user.adapter.out.pg;

import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.otus.msa.user.adapter.out.pg.entity.PickupPoint;
import ru.otus.msa.user.adapter.out.pg.entity.User;
import ru.otus.msa.user.api.common.PickupPointDto;
import ru.otus.msa.user.api.http.dto.RegisterUserDto;

@Mapper(componentModel = "spring")
public interface RegisterUserMapper {

    @Mapping(target = "id", source = "keycloakClientUserId")
    User map(UUID keycloakClientUserId, RegisterUserDto registerUserDto);

    @Mapping(target = "oneLineAddress", source = "pickupPoint.address.oneLineAddress")
    PickupPoint map(PickupPointDto pickupPoint);
}
