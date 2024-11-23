package com.virality.dishly.web.rest;

import static com.virality.dishly.domain.KitchenAsserts.*;
import static com.virality.dishly.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virality.dishly.IntegrationTest;
import com.virality.dishly.domain.Kitchen;
import com.virality.dishly.repository.EntityManager;
import com.virality.dishly.repository.KitchenRepository;
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
 * Integration tests for the {@link KitchenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class KitchenResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/kitchens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private KitchenRepository kitchenRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Kitchen kitchen;

    private Kitchen insertedKitchen;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Kitchen createEntity() {
        return new Kitchen()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .image(DEFAULT_IMAGE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Kitchen createUpdatedEntity() {
        return new Kitchen()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Kitchen.class).block();
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
        kitchen = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedKitchen != null) {
            kitchenRepository.delete(insertedKitchen).block();
            insertedKitchen = null;
        }
        deleteEntities(em);
    }

    @Test
    void createKitchen() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Kitchen
        var returnedKitchen = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Kitchen.class)
            .returnResult()
            .getResponseBody();

        // Validate the Kitchen in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertKitchenUpdatableFieldsEquals(returnedKitchen, getPersistedKitchen(returnedKitchen));

        insertedKitchen = returnedKitchen;
    }

    @Test
    void createKitchenWithExistingId() throws Exception {
        // Create the Kitchen with an existing ID
        kitchen.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllKitchensAsStream() {
        // Initialize the database
        kitchenRepository.save(kitchen).block();

        List<Kitchen> kitchenList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Kitchen.class)
            .getResponseBody()
            .filter(kitchen::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(kitchenList).isNotNull();
        assertThat(kitchenList).hasSize(1);
        Kitchen testKitchen = kitchenList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertKitchenAllPropertiesEquals(kitchen, testKitchen);
        assertKitchenUpdatableFieldsEquals(kitchen, testKitchen);
    }

    @Test
    void getAllKitchens() {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();

        // Get all the kitchenList
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
            .value(hasItem(kitchen.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].image")
            .value(hasItem(DEFAULT_IMAGE))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getKitchen() {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();

        // Get the kitchen
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, kitchen.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(kitchen.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.image")
            .value(is(DEFAULT_IMAGE))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.updatedAt")
            .value(is(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getNonExistingKitchen() {
        // Get the kitchen
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingKitchen() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the kitchen
        Kitchen updatedKitchen = kitchenRepository.findById(kitchen.getId()).block();
        updatedKitchen
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedKitchen.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedKitchen))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedKitchenToMatchAllProperties(updatedKitchen);
    }

    @Test
    void putNonExistingKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchen.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, kitchen.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchen.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchen.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateKitchenWithPatch() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the kitchen using partial update
        Kitchen partialUpdatedKitchen = new Kitchen();
        partialUpdatedKitchen.setId(kitchen.getId());

        partialUpdatedKitchen.name(UPDATED_NAME).updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedKitchen.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedKitchen))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Kitchen in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKitchenUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedKitchen, kitchen), getPersistedKitchen(kitchen));
    }

    @Test
    void fullUpdateKitchenWithPatch() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the kitchen using partial update
        Kitchen partialUpdatedKitchen = new Kitchen();
        partialUpdatedKitchen.setId(kitchen.getId());

        partialUpdatedKitchen
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedKitchen.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedKitchen))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Kitchen in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKitchenUpdatableFieldsEquals(partialUpdatedKitchen, getPersistedKitchen(partialUpdatedKitchen));
    }

    @Test
    void patchNonExistingKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchen.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, kitchen.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchen.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchen.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteKitchen() {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the kitchen
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, kitchen.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return kitchenRepository.count().block();
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

    protected Kitchen getPersistedKitchen(Kitchen kitchen) {
        return kitchenRepository.findById(kitchen.getId()).block();
    }

    protected void assertPersistedKitchenToMatchAllProperties(Kitchen expectedKitchen) {
        // Test fails because reactive api returns an empty object instead of null
        // assertKitchenAllPropertiesEquals(expectedKitchen, getPersistedKitchen(expectedKitchen));
        assertKitchenUpdatableFieldsEquals(expectedKitchen, getPersistedKitchen(expectedKitchen));
    }

    protected void assertPersistedKitchenToMatchUpdatableProperties(Kitchen expectedKitchen) {
        // Test fails because reactive api returns an empty object instead of null
        // assertKitchenAllUpdatablePropertiesEquals(expectedKitchen, getPersistedKitchen(expectedKitchen));
        assertKitchenUpdatableFieldsEquals(expectedKitchen, getPersistedKitchen(expectedKitchen));
    }
}
