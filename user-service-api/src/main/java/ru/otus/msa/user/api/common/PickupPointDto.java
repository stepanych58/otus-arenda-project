package ru.otus.msa.user.api.common;

import ru.otus.msa.user.api.http.dto.AddressDto;

public record PickupPointDto(AddressDto address, String startTime, String endTime) {
}
