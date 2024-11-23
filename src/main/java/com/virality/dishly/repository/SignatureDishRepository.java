package com.virality.dishly.repository;

import com.virality.dishly.domain.SignatureDish;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the SignatureDish entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SignatureDishRepository extends ReactiveCrudRepository<SignatureDish, Long>, SignatureDishRepositoryInternal {
    @Query("SELECT * FROM signature_dish entity WHERE entity.chief_id = :id")
    Flux<SignatureDish> findByChief(Long id);

    @Query("SELECT * FROM signature_dish entity WHERE entity.chief_id IS NULL")
    Flux<SignatureDish> findAllWhereChiefIsNull();

    @Override
    <S extends SignatureDish> Mono<S> save(S entity);

    @Override
    Flux<SignatureDish> findAll();

    @Override
    Mono<SignatureDish> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SignatureDishRepositoryInternal {
    <S extends SignatureDish> Mono<S> save(S entity);

    Flux<SignatureDish> findAllBy(Pageable pageable);

    Flux<SignatureDish> findAll();

    Mono<SignatureDish> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<SignatureDish> findAllBy(Pageable pageable, Criteria criteria);
}
