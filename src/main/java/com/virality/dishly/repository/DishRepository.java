package com.virality.dishly.repository;

import com.virality.dishly.domain.Dish;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Dish entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DishRepository extends ReactiveCrudRepository<Dish, Long>, DishRepositoryInternal {
    @Query("SELECT * FROM dish entity WHERE entity.kitchen_id = :id")
    Flux<Dish> findByKitchen(Long id);

    @Query("SELECT * FROM dish entity WHERE entity.kitchen_id IS NULL")
    Flux<Dish> findAllWhereKitchenIsNull();

    @Query("SELECT * FROM dish entity WHERE entity.menu_id = :id")
    Flux<Dish> findByMenu(Long id);

    @Query("SELECT * FROM dish entity WHERE entity.menu_id IS NULL")
    Flux<Dish> findAllWhereMenuIsNull();

    @Override
    <S extends Dish> Mono<S> save(S entity);

    @Override
    Flux<Dish> findAll();

    @Override
    Mono<Dish> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DishRepositoryInternal {
    <S extends Dish> Mono<S> save(S entity);

    Flux<Dish> findAllBy(Pageable pageable);

    Flux<Dish> findAll();

    Mono<Dish> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Dish> findAllBy(Pageable pageable, Criteria criteria);
}
