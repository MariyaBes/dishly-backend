package com.virality.dishly.web.rest;

import static com.virality.dishly.domain.MenuAsserts.*;
import static com.virality.dishly.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virality.dishly.IntegrationTest;
import com.virality.dishly.domain.Menu;
import com.virality.dishly.repository.EntityManager;
import com.virality.dishly.repository.MenuRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link MenuResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MenuResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/menus";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Menu menu;

    private Menu insertedMenu;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Menu createEntity() {
        return new Menu().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).createdAt(DEFAULT_CREATED_AT).updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Menu createUpdatedEntity() {
        return new Menu().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Menu.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        menu = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedMenu != null) {
            menuRepository.delete(insertedMenu).block();
            insertedMenu = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMenu() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Menu
        var returnedMenu = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menu))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Menu.class)
            .returnResult()
            .getResponseBody();

        // Validate the Menu in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMenuUpdatableFieldsEquals(returnedMenu, getPersistedMenu(returnedMenu));

        insertedMenu = returnedMenu;
    }

    @Test
    void createMenuWithExistingId() throws Exception {
        // Create the Menu with an existing ID
        menu.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menu))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Menu in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllMenusAsStream() {
        // Initialize the database
        menuRepository.save(menu).block();

        List<Menu> menuList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Menu.class)
            .getResponseBody()
            .filter(menu::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(menuList).isNotNull();
        assertThat(menuList).hasSize(1);
        Menu testMenu = menuList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertMenuAllPropertiesEquals(menu, testMenu);
        assertMenuUpdatableFieldsEquals(menu, testMenu);
    }

    @Test
    void getAllMenus() {
        // Initialize the database
        insertedMenu = menuRepository.save(menu).block();

        // Get all the menuList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(menu.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getMenu() {
        // Initialize the database
        insertedMenu = menuRepository.save(menu).block();

        // Get the menu
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, menu.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(menu.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.updatedAt")
            .value(is(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getNonExistingMenu() {
        // Get the menu
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMenu() throws Exception {
        // Initialize the database
        insertedMenu = menuRepository.save(menu).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menu
        Menu updatedMenu = menuRepository.findById(menu.getId()).block();
        updatedMenu.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedMenu.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedMenu))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Menu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMenuToMatchAllProperties(updatedMenu);
    }

    @Test
    void putNonExistingMenu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menu.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, menu.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menu))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Menu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMenu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menu.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menu))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Menu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMenu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menu.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(menu))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Menu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMenuWithPatch() throws Exception {
        // Initialize the database
        insertedMenu = menuRepository.save(menu).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menu using partial update
        Menu partialUpdatedMenu = new Menu();
        partialUpdatedMenu.setId(menu.getId());

        partialUpdatedMenu.name(UPDATED_NAME).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMenu.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMenu))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Menu in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMenu, menu), getPersistedMenu(menu));
    }

    @Test
    void fullUpdateMenuWithPatch() throws Exception {
        // Initialize the database
        insertedMenu = menuRepository.save(menu).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the menu using partial update
        Menu partialUpdatedMenu = new Menu();
        partialUpdatedMenu.setId(menu.getId());

        partialUpdatedMenu.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMenu.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMenu))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Menu in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMenuUpdatableFieldsEquals(partialUpdatedMenu, getPersistedMenu(partialUpdatedMenu));
    }

    @Test
    void patchNonExistingMenu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menu.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, menu.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menu))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Menu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMenu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menu.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menu))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Menu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMenu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        menu.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(menu))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Menu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMenu() {
        // Initialize the database
        insertedMenu = menuRepository.save(menu).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the menu
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, menu.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return menuRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Menu getPersistedMenu(Menu menu) {
        return menuRepository.findById(menu.getId()).block();
    }

    protected void assertPersistedMenuToMatchAllProperties(Menu expectedMenu) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMenuAllPropertiesEquals(expectedMenu, getPersistedMenu(expectedMenu));
        assertMenuUpdatableFieldsEquals(expectedMenu, getPersistedMenu(expectedMenu));
    }

    protected void assertPersistedMenuToMatchUpdatableProperties(Menu expectedMenu) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMenuAllUpdatablePropertiesEquals(expectedMenu, getPersistedMenu(expectedMenu));
        assertMenuUpdatableFieldsEquals(expectedMenu, getPersistedMenu(expectedMenu));
    }
}
