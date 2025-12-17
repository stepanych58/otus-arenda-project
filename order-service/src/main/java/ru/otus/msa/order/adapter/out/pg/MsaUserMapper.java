package ru.otus.msa.order.adapter.out.pg;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.otus.msa.order.adapter.out.pg.entity.MsaUserEntity;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

@Mapper(componentModel = "spring")
public interface MsaUserMapper {

    @Mappings({
            @Mapping(target = "id", source = "userId"),
            @Mapping(target = "pickupAddress", source = "pickupPoint.address.oneLineAddress"),
            @Mapping(target = "pickupStartTime", source = "pickupPoint.startTime"),
            @Mapping(target = "pickupEndTime", source = "pickupPoint.endTime")
    })
    MsaUserEntity toEntity(UserEvent userEvent);
}
