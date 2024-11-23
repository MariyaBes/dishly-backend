package com.virality.dishly.web.rest;

import com.virality.dishly.domain.Kitchen;
import com.virality.dishly.repository.KitchenRepository;
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
 * REST controller for managing {@link com.virality.dishly.domain.Kitchen}.
 */
@RestController
@RequestMapping("/api/kitchens")
@Transactional
public class KitchenResource {

    private static final Logger LOG = LoggerFactory.getLogger(KitchenResource.class);

    private static final String ENTITY_NAME = "kitchen";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KitchenRepository kitchenRepository;

    public KitchenResource(KitchenRepository kitchenRepository) {
        this.kitchenRepository = kitchenRepository;
    }

    /**
     * {@code POST  /kitchens} : Create a new kitchen.
     *
     * @param kitchen the kitchen to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new kitchen, or with status {@code 400 (Bad Request)} if the kitchen has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Kitchen>> createKitchen(@RequestBody Kitchen kitchen) throws URISyntaxException {
        LOG.debug("REST request to save Kitchen : {}", kitchen);
        if (kitchen.getId() != null) {
            throw new BadRequestAlertException("A new kitchen cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return kitchenRepository
            .save(kitchen)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/kitchens/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /kitchens/:id} : Updates an existing kitchen.
     *
     * @param id the id of the kitchen to save.
     * @param kitchen the kitchen to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated kitchen,
     * or with status {@code 400 (Bad Request)} if the kitchen is not valid,
     * or with status {@code 500 (Internal Server Error)} if the kitchen couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Kitchen>> updateKitchen(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Kitchen kitchen
    ) throws URISyntaxException {
        LOG.debug("REST request to update Kitchen : {}, {}", id, kitchen);
        if (kitchen.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, kitchen.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return kitchenRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return kitchenRepository
                    .save(kitchen)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /kitchens/:id} : Partial updates given fields of an existing kitchen, field will ignore if it is null
     *
     * @param id the id of the kitchen to save.
     * @param kitchen the kitchen to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated kitchen,
     * or with status {@code 400 (Bad Request)} if the kitchen is not valid,
     * or with status {@code 404 (Not Found)} if the kitchen is not found,
     * or with status {@code 500 (Internal Server Error)} if the kitchen couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Kitchen>> partialUpdateKitchen(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Kitchen kitchen
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Kitchen partially : {}, {}", id, kitchen);
        if (kitchen.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, kitchen.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return kitchenRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Kitchen> result = kitchenRepository
                    .findById(kitchen.getId())
                    .map(existingKitchen -> {
                        if (kitchen.getName() != null) {
                            existingKitchen.setName(kitchen.getName());
                        }
                        if (kitchen.getDescription() != null) {
                            existingKitchen.setDescription(kitchen.getDescription());
                        }
                        if (kitchen.getImage() != null) {
                            existingKitchen.setImage(kitchen.getImage());
                        }
                        if (kitchen.getCreatedAt() != null) {
                            existingKitchen.setCreatedAt(kitchen.getCreatedAt());
                        }
                        if (kitchen.getUpdatedAt() != null) {
                            existingKitchen.setUpdatedAt(kitchen.getUpdatedAt());
                        }

                        return existingKitchen;
                    })
                    .flatMap(kitchenRepository::save);

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
     * {@code GET  /kitchens} : get all the kitchens.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of kitchens in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Kitchen>> getAllKitchens() {
        LOG.debug("REST request to get all Kitchens");
        return kitchenRepository.findAll().collectList();
    }

    /**
     * {@code GET  /kitchens} : get all the kitchens as a stream.
     * @return the {@link Flux} of kitchens.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Kitchen> getAllKitchensAsStream() {
        LOG.debug("REST request to get all Kitchens as a stream");
        return kitchenRepository.findAll();
    }

    /**
     * {@code GET  /kitchens/:id} : get the "id" kitchen.
     *
     * @param id the id of the kitchen to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the kitchen, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Kitchen>> getKitchen(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Kitchen : {}", id);
        Mono<Kitchen> kitchen = kitchenRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(kitchen);
    }

    /**
     * {@code DELETE  /kitchens/:id} : delete the "id" kitchen.
     *
     * @param id the id of the kitchen to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteKitchen(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Kitchen : {}", id);
        return kitchenRepository
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
