package com.virality.dishly.web.rest;

import com.virality.dishly.domain.Dish;
import com.virality.dishly.repository.DishRepository;
import com.virality.dishly.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.virality.dishly.domain.Dish}.
 */
@RestController
@RequestMapping("/api/dishes")
@Transactional
public class DishResource {

    private static final Logger LOG = LoggerFactory.getLogger(DishResource.class);

    private static final String ENTITY_NAME = "dish";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DishRepository dishRepository;

    public DishResource(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    /**
     * {@code POST  /dishes} : Create a new dish.
     *
     * @param dish the dish to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dish, or with status {@code 400 (Bad Request)} if the dish has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Dish>> createDish(@RequestBody Dish dish) throws URISyntaxException {
        LOG.debug("REST request to save Dish : {}", dish);
        if (dish.getId() != null) {
            throw new BadRequestAlertException("A new dish cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return dishRepository
            .save(dish)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/dishes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /dishes/:id} : Updates an existing dish.
     *
     * @param id the id of the dish to save.
     * @param dish the dish to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dish,
     * or with status {@code 400 (Bad Request)} if the dish is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dish couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Dish>> updateDish(@PathVariable(value = "id", required = false) final Long id, @RequestBody Dish dish)
        throws URISyntaxException {
        LOG.debug("REST request to update Dish : {}, {}", id, dish);
        if (dish.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dish.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return dishRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return dishRepository
                    .save(dish)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /dishes/:id} : Partial updates given fields of an existing dish, field will ignore if it is null
     *
     * @param id the id of the dish to save.
     * @param dish the dish to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dish,
     * or with status {@code 400 (Bad Request)} if the dish is not valid,
     * or with status {@code 404 (Not Found)} if the dish is not found,
     * or with status {@code 500 (Internal Server Error)} if the dish couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Dish>> partialUpdateDish(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Dish dish
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Dish partially : {}, {}", id, dish);
        if (dish.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dish.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return dishRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Dish> result = dishRepository
                    .findById(dish.getId())
                    .map(existingDish -> {
                        if (dish.getName() != null) {
                            existingDish.setName(dish.getName());
                        }
                        if (dish.getPrice() != null) {
                            existingDish.setPrice(dish.getPrice());
                        }
                        if (dish.getDescription() != null) {
                            existingDish.setDescription(dish.getDescription());
                        }
                        if (dish.getPreparationTime() != null) {
                            existingDish.setPreparationTime(dish.getPreparationTime());
                        }
                        if (dish.getImage() != null) {
                            existingDish.setImage(dish.getImage());
                        }
                        if (dish.getStatus() != null) {
                            existingDish.setStatus(dish.getStatus());
                        }
                        if (dish.getCreatedAt() != null) {
                            existingDish.setCreatedAt(dish.getCreatedAt());
                        }
                        if (dish.getUpdatedAt() != null) {
                            existingDish.setUpdatedAt(dish.getUpdatedAt());
                        }
                        if (dish.getComposition() != null) {
                            existingDish.setComposition(dish.getComposition());
                        }
                        if (dish.getWeight() != null) {
                            existingDish.setWeight(dish.getWeight());
                        }
                        if (dish.getDishStatus() != null) {
                            existingDish.setDishStatus(dish.getDishStatus());
                        }

                        return existingDish;
                    })
                    .flatMap(dishRepository::save);

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
     * {@code GET  /dishes} : get all the dishes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dishes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Dish>> getAllDishes() {
        LOG.debug("REST request to get all Dishes");
        return dishRepository.findAll().collectList();
    }

    /**
     * {@code GET  /dishes} : get all the dishes as a stream.
     * @return the {@link Flux} of dishes.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Dish> getAllDishesAsStream() {
        LOG.debug("REST request to get all Dishes as a stream");
        return dishRepository.findAll();
    }

    /**
     * {@code GET  /dishes/:id} : get the "id" dish.
     *
     * @param id the id of the dish to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dish, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Dish>> getDish(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Dish : {}", id);
        Mono<Dish> dish = dishRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(dish);
    }

    /**
     * {@code DELETE  /dishes/:id} : delete the "id" dish.
     *
     * @param id the id of the dish to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDish(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Dish : {}", id);
        return dishRepository
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
