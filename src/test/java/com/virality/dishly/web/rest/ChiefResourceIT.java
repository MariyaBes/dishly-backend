package com.virality.dishly.web.rest;

import static com.virality.dishly.domain.ChiefAsserts.*;
import static com.virality.dishly.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virality.dishly.IntegrationTest;
import com.virality.dishly.domain.Chief;
import com.virality.dishly.domain.enumeration.ChiefStatus;
import com.virality.dishly.repository.ChiefRepository;
import com.virality.dishly.repository.EntityManager;
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
 * Integration tests for the {@link ChiefResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ChiefResourceIT {

    private static final Float DEFAULT_RATING = 1F;
    private static final Float UPDATED_RATING = 2F;

    private static final ChiefStatus DEFAULT_CHIEF_STATUS = ChiefStatus.FREE;
    private static final ChiefStatus UPDATED_CHIEF_STATUS = ChiefStatus.BUSY;

    private static final String DEFAULT_ABOUT = "AAAAAAAAAA";
    private static final String UPDATED_ABOUT = "BBBBBBBBBB";

    private static final String DEFAULT_ADDITIONAL_LINKS = "AAAAAAAAAA";
    private static final String UPDATED_ADDITIONAL_LINKS = "BBBBBBBBBB";

    private static final String DEFAULT_EDUCATION_DOCUMENT = "AAAAAAAAAA";
    private static final String UPDATED_EDUCATION_DOCUMENT = "BBBBBBBBBB";

    private static final String DEFAULT_MEDICAL_BOOK = "AAAAAAAAAA";
    private static final String UPDATED_MEDICAL_BOOK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/chiefs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChiefRepository chiefRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Chief chief;

    private Chief insertedChief;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chief createEntity() {
        return new Chief()
            .rating(DEFAULT_RATING)
            .chiefStatus(DEFAULT_CHIEF_STATUS)
            .about(DEFAULT_ABOUT)
            .additionalLinks(DEFAULT_ADDITIONAL_LINKS)
            .educationDocument(DEFAULT_EDUCATION_DOCUMENT)
            .medicalBook(DEFAULT_MEDICAL_BOOK);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chief createUpdatedEntity() {
        return new Chief()
            .rating(UPDATED_RATING)
            .chiefStatus(UPDATED_CHIEF_STATUS)
            .about(UPDATED_ABOUT)
            .additionalLinks(UPDATED_ADDITIONAL_LINKS)
            .educationDocument(UPDATED_EDUCATION_DOCUMENT)
            .medicalBook(UPDATED_MEDICAL_BOOK);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Chief.class).block();
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
        chief = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedChief != null) {
            chiefRepository.delete(insertedChief).block();
            insertedChief = null;
        }
        deleteEntities(em);
    }

    @Test
    void createChief() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Chief
        var returnedChief = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(chief))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Chief.class)
            .returnResult()
            .getResponseBody();

        // Validate the Chief in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertChiefUpdatableFieldsEquals(returnedChief, getPersistedChief(returnedChief));

        insertedChief = returnedChief;
    }

    @Test
    void createChiefWithExistingId() throws Exception {
        // Create the Chief with an existing ID
        chief.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(chief))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chief in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllChiefsAsStream() {
        // Initialize the database
        chiefRepository.save(chief).block();

        List<Chief> chiefList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Chief.class)
            .getResponseBody()
            .filter(chief::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(chiefList).isNotNull();
        assertThat(chiefList).hasSize(1);
        Chief testChief = chiefList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertChiefAllPropertiesEquals(chief, testChief);
        assertChiefUpdatableFieldsEquals(chief, testChief);
    }

    @Test
    void getAllChiefs() {
        // Initialize the database
        insertedChief = chiefRepository.save(chief).block();

        // Get all the chiefList
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
            .value(hasItem(chief.getId().intValue()))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING.doubleValue()))
            .jsonPath("$.[*].chiefStatus")
            .value(hasItem(DEFAULT_CHIEF_STATUS.toString()))
            .jsonPath("$.[*].about")
            .value(hasItem(DEFAULT_ABOUT.toString()))
            .jsonPath("$.[*].additionalLinks")
            .value(hasItem(DEFAULT_ADDITIONAL_LINKS.toString()))
            .jsonPath("$.[*].educationDocument")
            .value(hasItem(DEFAULT_EDUCATION_DOCUMENT))
            .jsonPath("$.[*].medicalBook")
            .value(hasItem(DEFAULT_MEDICAL_BOOK));
    }

    @Test
    void getChief() {
        // Initialize the database
        insertedChief = chiefRepository.save(chief).block();

        // Get the chief
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, chief.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(chief.getId().intValue()))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING.doubleValue()))
            .jsonPath("$.chiefStatus")
            .value(is(DEFAULT_CHIEF_STATUS.toString()))
            .jsonPath("$.about")
            .value(is(DEFAULT_ABOUT.toString()))
            .jsonPath("$.additionalLinks")
            .value(is(DEFAULT_ADDITIONAL_LINKS.toString()))
            .jsonPath("$.educationDocument")
            .value(is(DEFAULT_EDUCATION_DOCUMENT))
            .jsonPath("$.medicalBook")
            .value(is(DEFAULT_MEDICAL_BOOK));
    }

    @Test
    void getNonExistingChief() {
        // Get the chief
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingChief() throws Exception {
        // Initialize the database
        insertedChief = chiefRepository.save(chief).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chief
        Chief updatedChief = chiefRepository.findById(chief.getId()).block();
        updatedChief
            .rating(UPDATED_RATING)
            .chiefStatus(UPDATED_CHIEF_STATUS)
            .about(UPDATED_ABOUT)
            .additionalLinks(UPDATED_ADDITIONAL_LINKS)
            .educationDocument(UPDATED_EDUCATION_DOCUMENT)
            .medicalBook(UPDATED_MEDICAL_BOOK);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedChief.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedChief))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Chief in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChiefToMatchAllProperties(updatedChief);
    }

    @Test
    void putNonExistingChief() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chief.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, chief.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(chief))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chief in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchChief() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chief.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(chief))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chief in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamChief() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chief.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(chief))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Chief in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateChiefWithPatch() throws Exception {
        // Initialize the database
        insertedChief = chiefRepository.save(chief).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chief using partial update
        Chief partialUpdatedChief = new Chief();
        partialUpdatedChief.setId(chief.getId());

        partialUpdatedChief
            .chiefStatus(UPDATED_CHIEF_STATUS)
            .additionalLinks(UPDATED_ADDITIONAL_LINKS)
            .educationDocument(UPDATED_EDUCATION_DOCUMENT)
            .medicalBook(UPDATED_MEDICAL_BOOK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChief.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedChief))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Chief in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChiefUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedChief, chief), getPersistedChief(chief));
    }

    @Test
    void fullUpdateChiefWithPatch() throws Exception {
        // Initialize the database
        insertedChief = chiefRepository.save(chief).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chief using partial update
        Chief partialUpdatedChief = new Chief();
        partialUpdatedChief.setId(chief.getId());

        partialUpdatedChief
            .rating(UPDATED_RATING)
            .chiefStatus(UPDATED_CHIEF_STATUS)
            .about(UPDATED_ABOUT)
            .additionalLinks(UPDATED_ADDITIONAL_LINKS)
            .educationDocument(UPDATED_EDUCATION_DOCUMENT)
            .medicalBook(UPDATED_MEDICAL_BOOK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChief.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedChief))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Chief in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChiefUpdatableFieldsEquals(partialUpdatedChief, getPersistedChief(partialUpdatedChief));
    }

    @Test
    void patchNonExistingChief() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chief.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, chief.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(chief))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chief in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchChief() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chief.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(chief))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chief in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamChief() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chief.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(chief))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Chief in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteChief() {
        // Initialize the database
        insertedChief = chiefRepository.save(chief).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the chief
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, chief.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chiefRepository.count().block();
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

    protected Chief getPersistedChief(Chief chief) {
        return chiefRepository.findById(chief.getId()).block();
    }

    protected void assertPersistedChiefToMatchAllProperties(Chief expectedChief) {
        // Test fails because reactive api returns an empty object instead of null
        // assertChiefAllPropertiesEquals(expectedChief, getPersistedChief(expectedChief));
        assertChiefUpdatableFieldsEquals(expectedChief, getPersistedChief(expectedChief));
    }

    protected void assertPersistedChiefToMatchUpdatableProperties(Chief expectedChief) {
        // Test fails because reactive api returns an empty object instead of null
        // assertChiefAllUpdatablePropertiesEquals(expectedChief, getPersistedChief(expectedChief));
        assertChiefUpdatableFieldsEquals(expectedChief, getPersistedChief(expectedChief));
    }
}
