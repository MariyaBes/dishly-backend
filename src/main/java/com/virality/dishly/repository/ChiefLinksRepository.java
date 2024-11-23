package com.virality.dishly.repository;

import com.virality.dishly.domain.ChiefLinks;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ChiefLinks entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChiefLinksRepository extends ReactiveCrudRepository<ChiefLinks, Long>, ChiefLinksRepositoryInternal {
    @Query("SELECT * FROM chief_links entity WHERE entity.chief_id = :id")
    Flux<ChiefLinks> findByChief(Long id);

    @Query("SELECT * FROM chief_links entity WHERE entity.chief_id IS NULL")
    Flux<ChiefLinks> findAllWhereChiefIsNull();

    @Override
    <S extends ChiefLinks> Mono<S> save(S entity);

    @Override
    Flux<ChiefLinks> findAll();

    @Override
    Mono<ChiefLinks> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ChiefLinksRepositoryInternal {
    <S extends ChiefLinks> Mono<S> save(S entity);

    Flux<ChiefLinks> findAllBy(Pageable pageable);

    Flux<ChiefLinks> findAll();

    Mono<ChiefLinks> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ChiefLinks> findAllBy(Pageable pageable, Criteria criteria);
}
