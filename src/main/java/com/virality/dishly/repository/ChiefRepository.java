package com.virality.dishly.repository;

import com.virality.dishly.domain.Chief;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Chief entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChiefRepository extends ReactiveCrudRepository<Chief, Long>, ChiefRepositoryInternal {
    @Override
    <S extends Chief> Mono<S> save(S entity);

    @Override
    Flux<Chief> findAll();

    @Override
    Mono<Chief> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ChiefRepositoryInternal {
    <S extends Chief> Mono<S> save(S entity);

    Flux<Chief> findAllBy(Pageable pageable);

    Flux<Chief> findAll();

    Mono<Chief> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Chief> findAllBy(Pageable pageable, Criteria criteria);
}
