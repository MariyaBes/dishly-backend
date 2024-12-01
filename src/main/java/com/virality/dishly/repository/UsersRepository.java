package com.virality.dishly.repository;

import com.virality.dishly.domain.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@link Users} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UsersRepository extends ReactiveCrudRepository<Users, Long>, UsersRepositoryInternal {
    @Query("SELECT * FROM users entity WHERE entity.email = :email")
    Mono<Users> findByEmail(String email);

    @Query("SELECT * FROM users entity WHERE entity.city_id = :id")
    Flux<Users> findByCity(Long id);

    @Query("SELECT * FROM users entity WHERE entity.city_id IS NULL")
    Flux<Users> findAllWhereCityIsNull();

    @Override
    <S extends Users> Mono<S> save(S entity);

    @Override
    Flux<Users> findAll();

    @Override
    Mono<Users> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface UsersRepositoryInternal {
    <S extends Users> Mono<S> save(S entity);

    Flux<Users> findAllBy(Pageable pageable);

    Flux<Users> findAll();

    Mono<Users> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Users> findAllBy(Pageable pageable, Criteria criteria);
}
