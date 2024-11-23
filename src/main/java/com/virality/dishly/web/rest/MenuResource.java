package com.virality.dishly.web.rest;

import com.virality.dishly.domain.Menu;
import com.virality.dishly.repository.MenuRepository;
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
 * REST controller for managing {@link com.virality.dishly.domain.Menu}.
 */
@RestController
@RequestMapping("/api/menus")
@Transactional
public class MenuResource {

    private static final Logger LOG = LoggerFactory.getLogger(MenuResource.class);

    private static final String ENTITY_NAME = "menu";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MenuRepository menuRepository;

    public MenuResource(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     * {@code POST  /menus} : Create a new menu.
     *
     * @param menu the menu to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new menu, or with status {@code 400 (Bad Request)} if the menu has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Menu>> createMenu(@RequestBody Menu menu) throws URISyntaxException {
        LOG.debug("REST request to save Menu : {}", menu);
        if (menu.getId() != null) {
            throw new BadRequestAlertException("A new menu cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return menuRepository
            .save(menu)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/menus/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /menus/:id} : Updates an existing menu.
     *
     * @param id the id of the menu to save.
     * @param menu the menu to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menu,
     * or with status {@code 400 (Bad Request)} if the menu is not valid,
     * or with status {@code 500 (Internal Server Error)} if the menu couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Menu>> updateMenu(@PathVariable(value = "id", required = false) final Long id, @RequestBody Menu menu)
        throws URISyntaxException {
        LOG.debug("REST request to update Menu : {}, {}", id, menu);
        if (menu.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menu.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return menuRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return menuRepository
                    .save(menu)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /menus/:id} : Partial updates given fields of an existing menu, field will ignore if it is null
     *
     * @param id the id of the menu to save.
     * @param menu the menu to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated menu,
     * or with status {@code 400 (Bad Request)} if the menu is not valid,
     * or with status {@code 404 (Not Found)} if the menu is not found,
     * or with status {@code 500 (Internal Server Error)} if the menu couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Menu>> partialUpdateMenu(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Menu menu
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Menu partially : {}, {}", id, menu);
        if (menu.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, menu.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return menuRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Menu> result = menuRepository
                    .findById(menu.getId())
                    .map(existingMenu -> {
                        if (menu.getName() != null) {
                            existingMenu.setName(menu.getName());
                        }
                        if (menu.getDescription() != null) {
                            existingMenu.setDescription(menu.getDescription());
                        }
                        if (menu.getCreatedAt() != null) {
                            existingMenu.setCreatedAt(menu.getCreatedAt());
                        }
                        if (menu.getUpdatedAt() != null) {
                            existingMenu.setUpdatedAt(menu.getUpdatedAt());
                        }

                        return existingMenu;
                    })
                    .flatMap(menuRepository::save);

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
     * {@code GET  /menus} : get all the menus.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of menus in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Menu>> getAllMenus() {
        LOG.debug("REST request to get all Menus");
        return menuRepository.findAll().collectList();
    }

    /**
     * {@code GET  /menus} : get all the menus as a stream.
     * @return the {@link Flux} of menus.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Menu> getAllMenusAsStream() {
        LOG.debug("REST request to get all Menus as a stream");
        return menuRepository.findAll();
    }

    /**
     * {@code GET  /menus/:id} : get the "id" menu.
     *
     * @param id the id of the menu to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the menu, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Menu>> getMenu(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Menu : {}", id);
        Mono<Menu> menu = menuRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(menu);
    }

    /**
     * {@code DELETE  /menus/:id} : delete the "id" menu.
     *
     * @param id the id of the menu to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMenu(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Menu : {}", id);
        return menuRepository
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
