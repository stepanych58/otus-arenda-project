package ru.otus.msa.user.adapter.out.pg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.msa.user.adapter.out.pg.entity.PickupPoint;
import ru.otus.msa.user.adapter.out.pg.entity.User;
import ru.otus.msa.user.adapter.out.pg.repository.PickupPointRepository;
import ru.otus.msa.user.adapter.out.pg.repository.UserFilter;
import ru.otus.msa.user.adapter.out.pg.repository.UserRepository;
import ru.otus.msa.user.api.http.dto.RegisterUserDto;
import ru.otus.msa.user.application.UserService;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PickupPointRepository pickupPointRepository;

    private final RegisterUserMapper registerUserMapper;

    private final ObjectMapper objectMapper;

    @Override
    public Page<User> getAll(UserFilter filter, Pageable pageable) {
        Specification<User> spec = filter.toSpecification();
        return userRepository.findAll(spec, pageable);
    }

    @Override
    public User getOne(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Entity with id `%s` not found".formatted(id)));
    }

    @Override
    public List<User> getMany(List<UUID> ids) {
        return userRepository.findAllById(ids);
    }

    @Override
    @Transactional
    public User create(UUID keycloakClientUserId, RegisterUserDto createUserRequest) {
        User userEntity = registerUserMapper.map(keycloakClientUserId, createUserRequest);
        if (Objects.nonNull(createUserRequest.getPickupPoint())) {
            PickupPoint pickupPointEntity = registerUserMapper.map(createUserRequest.getPickupPoint());
            pickupPointEntity.setUser(userEntity);
            userEntity.setPickupPoint(pickupPointEntity);
        }
//        pickupPointRepository.save(pickupPointEntity);
        return userRepository.save(userEntity);
    }

    @Override
    public User patch(UUID id, JsonNode patchNode) throws IOException {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(user).readValue(patchNode);

        return userRepository.save(user);
    }

    @Override
    public List<User> patchMany(List<UUID> ids, JsonNode patchNode) throws IOException {
        Collection<User> users = userRepository.findAllById(ids);

        for (User user : users) {
            objectMapper.readerForUpdating(user).readValue(patchNode);
        }

        return userRepository.saveAll(users);
    }

    @Override
    public User delete(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userRepository.delete(user);
        }
        return user;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        return !userRepository.findAllByEmail(email).isEmpty();
    }
}
