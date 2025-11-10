package ru.otus.msa.user.adapter.out.kafka;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.otus.msa.user.adapter.out.pg.entity.User;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

@Mapper(componentModel = "spring")
public interface UserEventMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "pickupPoint.address.oneLineAddress", source = "pickupPoint.oneLineAddress")
    UserEvent map(User user);
}
