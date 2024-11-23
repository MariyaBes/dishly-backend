package com.virality.dishly.web.rest;

import com.virality.dishly.domain.Users;
import com.virality.dishly.repository.UsersRepository;
import com.virality.dishly.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.virality.dishly.domain.Users}.
 */
@RestController
@RequestMapping("/api/users")
@Transactional
public class UsersResource {

    private static final Logger LOG = LoggerFactory.getLogger(UsersResource.class);

    private static final String ENTITY_NAME = "users";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UsersRepository usersRepository;

    public UsersResource(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * {@code POST  /users} : Create a new users.
     *
     * @param users the users to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new users, or with status {@code 400 (Bad Request)} if the users has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Users>> createUsers(@Valid @RequestBody Users users) throws URISyntaxException {
        LOG.debug("REST request to save Users : {}", users);
        if (users.getId() != null) {
            throw new BadRequestAlertException("A new users cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return usersRepository
            .save(users)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/users/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /users/:id} : Updates an existing users.
     *
     * @param id the id of the users to save.
     * @param users the users to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated users,
     * or with status {@code 400 (Bad Request)} if the users is not valid,
     * or with status {@code 500 (Internal Server Error)} if the users couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Users>> updateUsers(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Users users
    ) throws URISyntaxException {
        LOG.debug("REST request to update Users : {}, {}", id, users);
        if (users.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, users.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return usersRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return usersRepository
                    .save(users)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /users/:id} : Partial updates given fields of an existing users, field will ignore if it is null
     *
     * @param id the id of the users to save.
     * @param users the users to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated users,
     * or with status {@code 400 (Bad Request)} if the users is not valid,
     * or with status {@code 404 (Not Found)} if the users is not found,
     * or with status {@code 500 (Internal Server Error)} if the users couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Users>> partialUpdateUsers(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Users users
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Users partially : {}, {}", id, users);
        if (users.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, users.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return usersRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Users> result = usersRepository
                    .findById(users.getId())
                    .map(existingUsers -> {
                        if (users.getUsername() != null) {
                            existingUsers.setUsername(users.getUsername());
                        }
                        if (users.getFirstName() != null) {
                            existingUsers.setFirstName(users.getFirstName());
                        }
                        if (users.getLastName() != null) {
                            existingUsers.setLastName(users.getLastName());
                        }
                        if (users.getEmail() != null) {
                            existingUsers.setEmail(users.getEmail());
                        }
                        if (users.getPhone() != null) {
                            existingUsers.setPhone(users.getPhone());
                        }
                        if (users.getPasswordHash() != null) {
                            existingUsers.setPasswordHash(users.getPasswordHash());
                        }
                        if (users.getImage() != null) {
                            existingUsers.setImage(users.getImage());
                        }
                        if (users.getStatus() != null) {
                            existingUsers.setStatus(users.getStatus());
                        }
                        if (users.getGender() != null) {
                            existingUsers.setGender(users.getGender());
                        }
                        if (users.getRole() != null) {
                            existingUsers.setRole(users.getRole());
                        }
                        if (users.getVerificationStatus() != null) {
                            existingUsers.setVerificationStatus(users.getVerificationStatus());
                        }
                        if (users.getCreatedAt() != null) {
                            existingUsers.setCreatedAt(users.getCreatedAt());
                        }
                        if (users.getUpdatedAt() != null) {
                            existingUsers.setUpdatedAt(users.getUpdatedAt());
                        }
                        if (users.getUserStatus() != null) {
                            existingUsers.setUserStatus(users.getUserStatus());
                        }

                        return existingUsers;
                    })
                    .flatMap(usersRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /users} : get all the users.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of users in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Users>> getAllUsers() {
        LOG.debug("REST request to get all Users");
        return usersRepository.findAll().collectList();
    }

    /**
     * {@code GET  /users} : get all the users as a stream.
     * @return the {@link Flux} of users.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Users> getAllUsersAsStream() {
        LOG.debug("REST request to get all Users as a stream");
        return usersRepository.findAll();
    }

    /**
     * {@code GET  /users/:id} : get the "id" users.
     *
     * @param id the id of the users to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the users, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Users>> getUsers(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Users : {}", id);
        Mono<Users> users = usersRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(users);
    }

    /**
     * {@code DELETE  /users/:id} : delete the "id" users.
     *
     * @param id the id of the users to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUsers(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Users : {}", id);
        return usersRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
