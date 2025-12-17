package ru.otus.msa.order.adapter.out.pg;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.msa.order.adapter.out.pg.entity.MsaUserEntity;
import ru.otus.msa.order.adapter.out.pg.repository.MsaUserRepository;
import ru.otus.msa.order.application.UserService;
import ru.otus.msa.user.api.kafka.dto.UserEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final MsaUserRepository users;

    private final MsaUserMapper userMapper;

    @Override
    public void createUser(UserEvent userEvent) {
        MsaUserEntity toSaveUser = userMapper.toEntity(userEvent);
        MsaUserEntity savedUser = users.save(toSaveUser);
        log.info("Создан пользователь {}", savedUser);
    }
}
