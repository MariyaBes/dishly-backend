package com.virality.dishly.web.rest;

import com.virality.dishly.domain.ChiefLinks;
import com.virality.dishly.repository.ChiefLinksRepository;
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
 * REST controller for managing {@link com.virality.dishly.domain.ChiefLinks}.
 */
@RestController
@RequestMapping("/api/chief-links")
@Transactional
public class ChiefLinksResource {

    private static final Logger LOG = LoggerFactory.getLogger(ChiefLinksResource.class);

    private static final String ENTITY_NAME = "chiefLinks";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChiefLinksRepository chiefLinksRepository;

    public ChiefLinksResource(ChiefLinksRepository chiefLinksRepository) {
        this.chiefLinksRepository = chiefLinksRepository;
    }

    /**
     * {@code POST  /chief-links} : Create a new chiefLinks.
     *
     * @param chiefLinks the chiefLinks to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chiefLinks, or with status {@code 400 (Bad Request)} if the chiefLinks has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ChiefLinks>> createChiefLinks(@RequestBody ChiefLinks chiefLinks) throws URISyntaxException {
        LOG.debug("REST request to save ChiefLinks : {}", chiefLinks);
        if (chiefLinks.getId() != null) {
            throw new BadRequestAlertException("A new chiefLinks cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return chiefLinksRepository
            .save(chiefLinks)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/chief-links/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /chief-links/:id} : Updates an existing chiefLinks.
     *
     * @param id the id of the chiefLinks to save.
     * @param chiefLinks the chiefLinks to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chiefLinks,
     * or with status {@code 400 (Bad Request)} if the chiefLinks is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chiefLinks couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ChiefLinks>> updateChiefLinks(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ChiefLinks chiefLinks
    ) throws URISyntaxException {
        LOG.debug("REST request to update ChiefLinks : {}, {}", id, chiefLinks);
        if (chiefLinks.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chiefLinks.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return chiefLinksRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return chiefLinksRepository
                    .save(chiefLinks)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /chief-links/:id} : Partial updates given fields of an existing chiefLinks, field will ignore if it is null
     *
     * @param id the id of the chiefLinks to save.
     * @param chiefLinks the chiefLinks to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chiefLinks,
     * or with status {@code 400 (Bad Request)} if the chiefLinks is not valid,
     * or with status {@code 404 (Not Found)} if the chiefLinks is not found,
     * or with status {@code 500 (Internal Server Error)} if the chiefLinks couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ChiefLinks>> partialUpdateChiefLinks(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ChiefLinks chiefLinks
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ChiefLinks partially : {}, {}", id, chiefLinks);
        if (chiefLinks.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chiefLinks.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return chiefLinksRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ChiefLinks> result = chiefLinksRepository
                    .findById(chiefLinks.getId())
                    .map(existingChiefLinks -> {
                        if (chiefLinks.getTelegramLink() != null) {
                            existingChiefLinks.setTelegramLink(chiefLinks.getTelegramLink());
                        }
                        if (chiefLinks.getVkLink() != null) {
                            existingChiefLinks.setVkLink(chiefLinks.getVkLink());
                        }
                        if (chiefLinks.getOdnoklassnikiLink() != null) {
                            existingChiefLinks.setOdnoklassnikiLink(chiefLinks.getOdnoklassnikiLink());
                        }
                        if (chiefLinks.getYoutubeLink() != null) {
                            existingChiefLinks.setYoutubeLink(chiefLinks.getYoutubeLink());
                        }
                        if (chiefLinks.getRutubeLink() != null) {
                            existingChiefLinks.setRutubeLink(chiefLinks.getRutubeLink());
                        }

                        return existingChiefLinks;
                    })
                    .flatMap(chiefLinksRepository::save);

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
     * {@code GET  /chief-links} : get all the chiefLinks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chiefLinks in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<ChiefLinks>> getAllChiefLinks() {
        LOG.debug("REST request to get all ChiefLinks");
        return chiefLinksRepository.findAll().collectList();
    }

    /**
     * {@code GET  /chief-links} : get all the chiefLinks as a stream.
     * @return the {@link Flux} of chiefLinks.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChiefLinks> getAllChiefLinksAsStream() {
        LOG.debug("REST request to get all ChiefLinks as a stream");
        return chiefLinksRepository.findAll();
    }

    /**
     * {@code GET  /chief-links/:id} : get the "id" chiefLinks.
     *
     * @param id the id of the chiefLinks to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chiefLinks, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ChiefLinks>> getChiefLinks(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ChiefLinks : {}", id);
        Mono<ChiefLinks> chiefLinks = chiefLinksRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(chiefLinks);
    }

    /**
     * {@code DELETE  /chief-links/:id} : delete the "id" chiefLinks.
     *
     * @param id the id of the chiefLinks to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteChiefLinks(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ChiefLinks : {}", id);
        return chiefLinksRepository
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
