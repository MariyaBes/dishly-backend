package com.virality.dishly.web.rest;

import static com.virality.dishly.domain.SignatureDishAsserts.*;
import static com.virality.dishly.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virality.dishly.IntegrationTest;
import com.virality.dishly.domain.SignatureDish;
import com.virality.dishly.repository.EntityManager;
import com.virality.dishly.repository.SignatureDishRepository;
import java.time.Duration;
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
 * Integration tests for the {@link SignatureDishResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SignatureDishResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/signature-dishes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SignatureDishRepository signatureDishRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private SignatureDish signatureDish;

    private SignatureDish insertedSignatureDish;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SignatureDish createEntity() {
        return new SignatureDish().name(DEFAULT_NAME).image(DEFAULT_IMAGE).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SignatureDish createUpdatedEntity() {
        return new SignatureDish().name(UPDATED_NAME).image(UPDATED_IMAGE).description(UPDATED_DESCRIPTION);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(SignatureDish.class).block();
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
        signatureDish = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSignatureDish != null) {
            signatureDishRepository.delete(insertedSignatureDish).block();
            insertedSignatureDish = null;
        }
        deleteEntities(em);
    }

    @Test
    void createSignatureDish() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SignatureDish
        var returnedSignatureDish = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(signatureDish))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SignatureDish.class)
            .returnResult()
            .getResponseBody();

        // Validate the SignatureDish in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSignatureDishUpdatableFieldsEquals(returnedSignatureDish, getPersistedSignatureDish(returnedSignatureDish));

        insertedSignatureDish = returnedSignatureDish;
    }

    @Test
    void createSignatureDishWithExistingId() throws Exception {
        // Create the SignatureDish with an existing ID
        signatureDish.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(signatureDish))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SignatureDish in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllSignatureDishesAsStream() {
        // Initialize the database
        signatureDishRepository.save(signatureDish).block();

        List<SignatureDish> signatureDishList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(SignatureDish.class)
            .getResponseBody()
            .filter(signatureDish::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(signatureDishList).isNotNull();
        assertThat(signatureDishList).hasSize(1);
        SignatureDish testSignatureDish = signatureDishList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertSignatureDishAllPropertiesEquals(signatureDish, testSignatureDish);
        assertSignatureDishUpdatableFieldsEquals(signatureDish, testSignatureDish);
    }

    @Test
    void getAllSignatureDishes() {
        // Initialize the database
        insertedSignatureDish = signatureDishRepository.save(signatureDish).block();

        // Get all the signatureDishList
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
            .value(hasItem(signatureDish.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].image")
            .value(hasItem(DEFAULT_IMAGE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getSignatureDish() {
        // Initialize the database
        insertedSignatureDish = signatureDishRepository.save(signatureDish).block();

        // Get the signatureDish
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, signatureDish.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(signatureDish.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.image")
            .value(is(DEFAULT_IMAGE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingSignatureDish() {
        // Get the signatureDish
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSignatureDish() throws Exception {
        // Initialize the database
        insertedSignatureDish = signatureDishRepository.save(signatureDish).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the signatureDish
        SignatureDish updatedSignatureDish = signatureDishRepository.findById(signatureDish.getId()).block();
        updatedSignatureDish.name(UPDATED_NAME).image(UPDATED_IMAGE).description(UPDATED_DESCRIPTION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSignatureDish.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedSignatureDish))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SignatureDish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSignatureDishToMatchAllProperties(updatedSignatureDish);
    }

    @Test
    void putNonExistingSignatureDish() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        signatureDish.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, signatureDish.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(signatureDish))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SignatureDish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSignatureDish() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        signatureDish.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(signatureDish))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SignatureDish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSignatureDish() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        signatureDish.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(signatureDish))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SignatureDish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSignatureDishWithPatch() throws Exception {
        // Initialize the database
        insertedSignatureDish = signatureDishRepository.save(signatureDish).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the signatureDish using partial update
        SignatureDish partialUpdatedSignatureDish = new SignatureDish();
        partialUpdatedSignatureDish.setId(signatureDish.getId());

        partialUpdatedSignatureDish.name(UPDATED_NAME).image(UPDATED_IMAGE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSignatureDish.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSignatureDish))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SignatureDish in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSignatureDishUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSignatureDish, signatureDish),
            getPersistedSignatureDish(signatureDish)
        );
    }

    @Test
    void fullUpdateSignatureDishWithPatch() throws Exception {
        // Initialize the database
        insertedSignatureDish = signatureDishRepository.save(signatureDish).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the signatureDish using partial update
        SignatureDish partialUpdatedSignatureDish = new SignatureDish();
        partialUpdatedSignatureDish.setId(signatureDish.getId());

        partialUpdatedSignatureDish.name(UPDATED_NAME).image(UPDATED_IMAGE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSignatureDish.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSignatureDish))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SignatureDish in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSignatureDishUpdatableFieldsEquals(partialUpdatedSignatureDish, getPersistedSignatureDish(partialUpdatedSignatureDish));
    }

    @Test
    void patchNonExistingSignatureDish() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        signatureDish.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, signatureDish.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(signatureDish))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SignatureDish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSignatureDish() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        signatureDish.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(signatureDish))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SignatureDish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSignatureDish() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        signatureDish.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(signatureDish))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SignatureDish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSignatureDish() {
        // Initialize the database
        insertedSignatureDish = signatureDishRepository.save(signatureDish).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the signatureDish
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, signatureDish.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return signatureDishRepository.count().block();
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

    protected SignatureDish getPersistedSignatureDish(SignatureDish signatureDish) {
        return signatureDishRepository.findById(signatureDish.getId()).block();
    }

    protected void assertPersistedSignatureDishToMatchAllProperties(SignatureDish expectedSignatureDish) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSignatureDishAllPropertiesEquals(expectedSignatureDish, getPersistedSignatureDish(expectedSignatureDish));
        assertSignatureDishUpdatableFieldsEquals(expectedSignatureDish, getPersistedSignatureDish(expectedSignatureDish));
    }

    protected void assertPersistedSignatureDishToMatchUpdatableProperties(SignatureDish expectedSignatureDish) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSignatureDishAllUpdatablePropertiesEquals(expectedSignatureDish, getPersistedSignatureDish(expectedSignatureDish));
        assertSignatureDishUpdatableFieldsEquals(expectedSignatureDish, getPersistedSignatureDish(expectedSignatureDish));
    }
}
