package com.virality.dishly.service;

import com.virality.dishly.domain.Users;
import com.virality.dishly.domain.enumeration.UserStatus;
import com.virality.dishly.domain.enumeration.VerificationStatus;
import com.virality.dishly.repository.AuthorityRepository;
import com.virality.dishly.repository.UsersRepository;
import com.virality.dishly.service.dto.UsersDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service class for managing users.
 */
@Service
public class UsersService {

    private static final Logger LOG = LoggerFactory.getLogger(UsersService.class);

    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @Transactional
    public Mono<Users> registerUsers(UsersDTO usersDTO, String passwordHash) {
        return usersRepository
            .findByEmail(usersDTO.getEmail())
            .flatMap(existingUser -> {
                // Если юзер такой уже есть - выкидываем ошибку
                return Mono.error(new UserAlreadyUsedException());
            })
            .switchIfEmpty(
                // Если пользователя нет, создаем нового
                Mono.defer(() -> {
                    Users newUser = new Users();
                    newUser.setUsername(usersDTO.getUsername());
                    newUser.setFirstName(usersDTO.getFirstName());
                    newUser.setLastName(usersDTO.getLastName());
                    newUser.setEmail(usersDTO.getEmail());
                    newUser.setPhone(usersDTO.getPhone());

                    // Хэшируем пароль перед сохранением
                    String encodedPassword = passwordEncoder.encode(passwordHash);
                    newUser.setPasswordHash(encodedPassword);

                    // Устанавливаем статусы по умолчанию
                    newUser.setVerificationStatus(VerificationStatus.NOT_VERIFIED);
                    newUser.setUserStatus(UserStatus.ACTIVE);

                    // Устанавливаем роль USER
                    authorityRepository
                        .findByName("ROLE_USER")
                        .flatMap(role -> {
                            newUser.setRole(role);
                            return usersRepository.save(newUser); // Сохраняем нового пользователя
                        })
                        .subscribe()
                        .doOnNext(user -> LOG.debug("Создан пользователь: {}", user)); // Отправляем пользователя в базу данных
                    return newUser;
                })
            );
    }
}
