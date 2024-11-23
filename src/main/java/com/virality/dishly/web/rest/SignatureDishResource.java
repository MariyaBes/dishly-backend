package com.virality.dishly.web.rest;

import com.virality.dishly.domain.SignatureDish;
import com.virality.dishly.repository.SignatureDishRepository;
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
 * REST controller for managing {@link com.virality.dishly.domain.SignatureDish}.
 */
@RestController
@RequestMapping("/api/signature-dishes")
@Transactional
public class SignatureDishResource {

    private static final Logger LOG = LoggerFactory.getLogger(SignatureDishResource.class);

    private static final String ENTITY_NAME = "signatureDish";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SignatureDishRepository signatureDishRepository;

    public SignatureDishResource(SignatureDishRepository signatureDishRepository) {
        this.signatureDishRepository = signatureDishRepository;
    }

    /**
     * {@code POST  /signature-dishes} : Create a new signatureDish.
     *
     * @param signatureDish the signatureDish to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new signatureDish, or with status {@code 400 (Bad Request)} if the signatureDish has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SignatureDish>> createSignatureDish(@RequestBody SignatureDish signatureDish) throws URISyntaxException {
        LOG.debug("REST request to save SignatureDish : {}", signatureDish);
        if (signatureDish.getId() != null) {
            throw new BadRequestAlertException("A new signatureDish cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return signatureDishRepository
            .save(signatureDish)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/signature-dishes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /signature-dishes/:id} : Updates an existing signatureDish.
     *
     * @param id the id of the signatureDish to save.
     * @param signatureDish the signatureDish to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated signatureDish,
     * or with status {@code 400 (Bad Request)} if the signatureDish is not valid,
     * or with status {@code 500 (Internal Server Error)} if the signatureDish couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SignatureDish>> updateSignatureDish(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SignatureDish signatureDish
    ) throws URISyntaxException {
        LOG.debug("REST request to update SignatureDish : {}, {}", id, signatureDish);
        if (signatureDish.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, signatureDish.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return signatureDishRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return signatureDishRepository
                    .save(signatureDish)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /signature-dishes/:id} : Partial updates given fields of an existing signatureDish, field will ignore if it is null
     *
     * @param id the id of the signatureDish to save.
     * @param signatureDish the signatureDish to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated signatureDish,
     * or with status {@code 400 (Bad Request)} if the signatureDish is not valid,
     * or with status {@code 404 (Not Found)} if the signatureDish is not found,
     * or with status {@code 500 (Internal Server Error)} if the signatureDish couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SignatureDish>> partialUpdateSignatureDish(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SignatureDish signatureDish
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SignatureDish partially : {}, {}", id, signatureDish);
        if (signatureDish.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, signatureDish.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return signatureDishRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<SignatureDish> result = signatureDishRepository
                    .findById(signatureDish.getId())
                    .map(existingSignatureDish -> {
                        if (signatureDish.getName() != null) {
                            existingSignatureDish.setName(signatureDish.getName());
                        }
                        if (signatureDish.getImage() != null) {
                            existingSignatureDish.setImage(signatureDish.getImage());
                        }
                        if (signatureDish.getDescription() != null) {
                            existingSignatureDish.setDescription(signatureDish.getDescription());
                        }

                        return existingSignatureDish;
                    })
                    .flatMap(signatureDishRepository::save);

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
     * {@code GET  /signature-dishes} : get all the signatureDishes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of signatureDishes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<SignatureDish>> getAllSignatureDishes() {
        LOG.debug("REST request to get all SignatureDishes");
        return signatureDishRepository.findAll().collectList();
    }

    /**
     * {@code GET  /signature-dishes} : get all the signatureDishes as a stream.
     * @return the {@link Flux} of signatureDishes.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<SignatureDish> getAllSignatureDishesAsStream() {
        LOG.debug("REST request to get all SignatureDishes as a stream");
        return signatureDishRepository.findAll();
    }

    /**
     * {@code GET  /signature-dishes/:id} : get the "id" signatureDish.
     *
     * @param id the id of the signatureDish to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the signatureDish, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SignatureDish>> getSignatureDish(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SignatureDish : {}", id);
        Mono<SignatureDish> signatureDish = signatureDishRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(signatureDish);
    }

    /**
     * {@code DELETE  /signature-dishes/:id} : delete the "id" signatureDish.
     *
     * @param id the id of the signatureDish to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSignatureDish(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SignatureDish : {}", id);
        return signatureDishRepository
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
