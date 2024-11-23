package com.virality.dishly.web.rest;

import com.virality.dishly.domain.Chief;
import com.virality.dishly.repository.ChiefRepository;
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
 * REST controller for managing {@link com.virality.dishly.domain.Chief}.
 */
@RestController
@RequestMapping("/api/chiefs")
@Transactional
public class ChiefResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChiefResource.class);

    private static final String ENTITY_NAME = "chief";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChiefRepository chiefRepository;

    public ChiefResource(ChiefRepository chiefRepository) {
        this.chiefRepository = chiefRepository;
    }

    /**
     * {@code POST  /chiefs} : Create a new chief.
     *
     * @param chief the chief to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chief, or with status {@code 400 (Bad Request)} if the chief has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Chief>> createChief(@RequestBody Chief chief) throws URISyntaxException {
        LOG.debug("REST request to save Chief : {}", chief);
        if (chief.getId() != null) {
            throw new BadRequestAlertException("A new chief cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return chiefRepository
            .save(chief)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/chiefs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /chiefs/:id} : Updates an existing chief.
     *
     * @param id the id of the chief to save.
     * @param chief the chief to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chief,
     * or with status {@code 400 (Bad Request)} if the chief is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chief couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Chief>> updateChief(@PathVariable(value = "id", required = false) final Long id, @RequestBody Chief chief)
        throws URISyntaxException {
        LOG.debug("REST request to update Chief : {}, {}", id, chief);
        if (chief.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chief.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return chiefRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return chiefRepository
                    .save(chief)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /chiefs/:id} : Partial updates given fields of an existing chief, field will ignore if it is null
     *
     * @param id the id of the chief to save.
     * @param chief the chief to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chief,
     * or with status {@code 400 (Bad Request)} if the chief is not valid,
     * or with status {@code 404 (Not Found)} if the chief is not found,
     * or with status {@code 500 (Internal Server Error)} if the chief couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Chief>> partialUpdateChief(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Chief chief
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Chief partially : {}, {}", id, chief);
        if (chief.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chief.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return chiefRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Chief> result = chiefRepository
                    .findById(chief.getId())
                    .map(existingChief -> {
                        if (chief.getRating() != null) {
                            existingChief.setRating(chief.getRating());
                        }
                        if (chief.getChiefStatus() != null) {
                            existingChief.setChiefStatus(chief.getChiefStatus());
                        }
                        if (chief.getAbout() != null) {
                            existingChief.setAbout(chief.getAbout());
                        }
                        if (chief.getAdditionalLinks() != null) {
                            existingChief.setAdditionalLinks(chief.getAdditionalLinks());
                        }
                        if (chief.getEducationDocument() != null) {
                            existingChief.setEducationDocument(chief.getEducationDocument());
                        }
                        if (chief.getMedicalBook() != null) {
                            existingChief.setMedicalBook(chief.getMedicalBook());
                        }

                        return existingChief;
                    })
                    .flatMap(chiefRepository::save);

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
     * {@code GET  /chiefs} : get all the chiefs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chiefs in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Chief>> getAllChiefs() {
        LOG.debug("REST request to get all Chiefs");
        return chiefRepository.findAll().collectList();
    }

    /**
     * {@code GET  /chiefs} : get all the chiefs as a stream.
     * @return the {@link Flux} of chiefs.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Chief> getAllChiefsAsStream() {
        LOG.debug("REST request to get all Chiefs as a stream");
        return chiefRepository.findAll();
    }

    /**
     * {@code GET  /chiefs/:id} : get the "id" chief.
     *
     * @param id the id of the chief to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chief, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Chief>> getChief(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Chief : {}", id);
        Mono<Chief> chief = chiefRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(chief);
    }

    /**
     * {@code DELETE  /chiefs/:id} : delete the "id" chief.
     *
     * @param id the id of the chief to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteChief(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Chief : {}", id);
        return chiefRepository
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
