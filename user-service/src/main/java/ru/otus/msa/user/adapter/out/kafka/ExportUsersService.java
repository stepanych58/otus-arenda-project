package ru.otus.msa.user.adapter.out.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.otus.msa.user.adapter.out.pg.repository.UserRepository;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExportUsersService {
    private final UserRepository users;
    private final UserEventMapper mapper;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void export() {
        log.info("ExportUsersService start");
        users.findAll().forEach(user -> {
            UserEvent userEvent = mapper.map(user);
            kafkaTemplate.send(kafkaTemplate.getDefaultTopic(), userEvent);
            log.info("exported userId {}", userEvent.userId());
        });
        log.info("ExportUsersService finish");
    }

}
