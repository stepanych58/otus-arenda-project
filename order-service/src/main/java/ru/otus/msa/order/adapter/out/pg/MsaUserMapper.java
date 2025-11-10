package ru.otus.msa.order.adapter.out.pg;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.msa.order.adapter.out.pg.repository.entity.MsaUserEntity;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

@Mapper(componentModel = "spring")
public interface MsaUserMapper {
    @Mapping(target = "id", source = "userId")
    MsaUserEntity toEntity(UserEvent userEvent);
}
