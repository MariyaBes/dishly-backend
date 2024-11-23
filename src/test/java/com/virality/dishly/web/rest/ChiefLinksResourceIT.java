package com.virality.dishly.web.rest;

import static com.virality.dishly.domain.ChiefLinksAsserts.*;
import static com.virality.dishly.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virality.dishly.IntegrationTest;
import com.virality.dishly.domain.ChiefLinks;
import com.virality.dishly.repository.ChiefLinksRepository;
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
 * Integration tests for the {@link ChiefLinksResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ChiefLinksResourceIT {

    private static final String DEFAULT_TELEGRAM_LINK = "AAAAAAAAAA";
    private static final String UPDATED_TELEGRAM_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_VK_LINK = "AAAAAAAAAA";
    private static final String UPDATED_VK_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_ODNOKLASSNIKI_LINK = "AAAAAAAAAA";
    private static final String UPDATED_ODNOKLASSNIKI_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_YOUTUBE_LINK = "AAAAAAAAAA";
    private static final String UPDATED_YOUTUBE_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_RUTUBE_LINK = "AAAAAAAAAA";
    private static final String UPDATED_RUTUBE_LINK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/chief-links";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChiefLinksRepository chiefLinksRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ChiefLinks chiefLinks;

    private ChiefLinks insertedChiefLinks;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChiefLinks createEntity() {
        return new ChiefLinks()
            .telegramLink(DEFAULT_TELEGRAM_LINK)
            .vkLink(DEFAULT_VK_LINK)
            .odnoklassnikiLink(DEFAULT_ODNOKLASSNIKI_LINK)
            .youtubeLink(DEFAULT_YOUTUBE_LINK)
            .rutubeLink(DEFAULT_RUTUBE_LINK);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChiefLinks createUpdatedEntity() {
        return new ChiefLinks()
            .telegramLink(UPDATED_TELEGRAM_LINK)
            .vkLink(UPDATED_VK_LINK)
            .odnoklassnikiLink(UPDATED_ODNOKLASSNIKI_LINK)
            .youtubeLink(UPDATED_YOUTUBE_LINK)
            .rutubeLink(UPDATED_RUTUBE_LINK);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ChiefLinks.class).block();
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
        chiefLinks = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedChiefLinks != null) {
            chiefLinksRepository.delete(insertedChiefLinks).block();
            insertedChiefLinks = null;
        }
        deleteEntities(em);
    }

    @Test
    void createChiefLinks() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ChiefLinks
        var returnedChiefLinks = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(chiefLinks))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ChiefLinks.class)
            .returnResult()
            .getResponseBody();

        // Validate the ChiefLinks in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertChiefLinksUpdatableFieldsEquals(returnedChiefLinks, getPersistedChiefLinks(returnedChiefLinks));

        insertedChiefLinks = returnedChiefLinks;
    }

    @Test
    void createChiefLinksWithExistingId() throws Exception {
        // Create the ChiefLinks with an existing ID
        chiefLinks.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(chiefLinks))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChiefLinks in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllChiefLinksAsStream() {
        // Initialize the database
        chiefLinksRepository.save(chiefLinks).block();

        List<ChiefLinks> chiefLinksList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ChiefLinks.class)
            .getResponseBody()
            .filter(chiefLinks::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(chiefLinksList).isNotNull();
        assertThat(chiefLinksList).hasSize(1);
        ChiefLinks testChiefLinks = chiefLinksList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertChiefLinksAllPropertiesEquals(chiefLinks, testChiefLinks);
        assertChiefLinksUpdatableFieldsEquals(chiefLinks, testChiefLinks);
    }

    @Test
    void getAllChiefLinks() {
        // Initialize the database
        insertedChiefLinks = chiefLinksRepository.save(chiefLinks).block();

        // Get all the chiefLinksList
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
            .value(hasItem(chiefLinks.getId().intValue()))
            .jsonPath("$.[*].telegramLink")
            .value(hasItem(DEFAULT_TELEGRAM_LINK))
            .jsonPath("$.[*].vkLink")
            .value(hasItem(DEFAULT_VK_LINK))
            .jsonPath("$.[*].odnoklassnikiLink")
            .value(hasItem(DEFAULT_ODNOKLASSNIKI_LINK))
            .jsonPath("$.[*].youtubeLink")
            .value(hasItem(DEFAULT_YOUTUBE_LINK))
            .jsonPath("$.[*].rutubeLink")
            .value(hasItem(DEFAULT_RUTUBE_LINK));
    }

    @Test
    void getChiefLinks() {
        // Initialize the database
        insertedChiefLinks = chiefLinksRepository.save(chiefLinks).block();

        // Get the chiefLinks
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, chiefLinks.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(chiefLinks.getId().intValue()))
            .jsonPath("$.telegramLink")
            .value(is(DEFAULT_TELEGRAM_LINK))
            .jsonPath("$.vkLink")
            .value(is(DEFAULT_VK_LINK))
            .jsonPath("$.odnoklassnikiLink")
            .value(is(DEFAULT_ODNOKLASSNIKI_LINK))
            .jsonPath("$.youtubeLink")
            .value(is(DEFAULT_YOUTUBE_LINK))
            .jsonPath("$.rutubeLink")
            .value(is(DEFAULT_RUTUBE_LINK));
    }

    @Test
    void getNonExistingChiefLinks() {
        // Get the chiefLinks
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingChiefLinks() throws Exception {
        // Initialize the database
        insertedChiefLinks = chiefLinksRepository.save(chiefLinks).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chiefLinks
        ChiefLinks updatedChiefLinks = chiefLinksRepository.findById(chiefLinks.getId()).block();
        updatedChiefLinks
            .telegramLink(UPDATED_TELEGRAM_LINK)
            .vkLink(UPDATED_VK_LINK)
            .odnoklassnikiLink(UPDATED_ODNOKLASSNIKI_LINK)
            .youtubeLink(UPDATED_YOUTUBE_LINK)
            .rutubeLink(UPDATED_RUTUBE_LINK);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedChiefLinks.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedChiefLinks))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ChiefLinks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChiefLinksToMatchAllProperties(updatedChiefLinks);
    }

    @Test
    void putNonExistingChiefLinks() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chiefLinks.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, chiefLinks.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(chiefLinks))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChiefLinks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchChiefLinks() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chiefLinks.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(chiefLinks))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChiefLinks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamChiefLinks() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chiefLinks.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(chiefLinks))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ChiefLinks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateChiefLinksWithPatch() throws Exception {
        // Initialize the database
        insertedChiefLinks = chiefLinksRepository.save(chiefLinks).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chiefLinks using partial update
        ChiefLinks partialUpdatedChiefLinks = new ChiefLinks();
        partialUpdatedChiefLinks.setId(chiefLinks.getId());

        partialUpdatedChiefLinks.rutubeLink(UPDATED_RUTUBE_LINK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChiefLinks.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedChiefLinks))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ChiefLinks in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChiefLinksUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedChiefLinks, chiefLinks),
            getPersistedChiefLinks(chiefLinks)
        );
    }

    @Test
    void fullUpdateChiefLinksWithPatch() throws Exception {
        // Initialize the database
        insertedChiefLinks = chiefLinksRepository.save(chiefLinks).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chiefLinks using partial update
        ChiefLinks partialUpdatedChiefLinks = new ChiefLinks();
        partialUpdatedChiefLinks.setId(chiefLinks.getId());

        partialUpdatedChiefLinks
            .telegramLink(UPDATED_TELEGRAM_LINK)
            .vkLink(UPDATED_VK_LINK)
            .odnoklassnikiLink(UPDATED_ODNOKLASSNIKI_LINK)
            .youtubeLink(UPDATED_YOUTUBE_LINK)
            .rutubeLink(UPDATED_RUTUBE_LINK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChiefLinks.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedChiefLinks))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ChiefLinks in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChiefLinksUpdatableFieldsEquals(partialUpdatedChiefLinks, getPersistedChiefLinks(partialUpdatedChiefLinks));
    }

    @Test
    void patchNonExistingChiefLinks() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chiefLinks.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, chiefLinks.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(chiefLinks))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChiefLinks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchChiefLinks() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chiefLinks.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(chiefLinks))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ChiefLinks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamChiefLinks() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chiefLinks.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(chiefLinks))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ChiefLinks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteChiefLinks() {
        // Initialize the database
        insertedChiefLinks = chiefLinksRepository.save(chiefLinks).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the chiefLinks
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, chiefLinks.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chiefLinksRepository.count().block();
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

    protected ChiefLinks getPersistedChiefLinks(ChiefLinks chiefLinks) {
        return chiefLinksRepository.findById(chiefLinks.getId()).block();
    }

    protected void assertPersistedChiefLinksToMatchAllProperties(ChiefLinks expectedChiefLinks) {
        // Test fails because reactive api returns an empty object instead of null
        // assertChiefLinksAllPropertiesEquals(expectedChiefLinks, getPersistedChiefLinks(expectedChiefLinks));
        assertChiefLinksUpdatableFieldsEquals(expectedChiefLinks, getPersistedChiefLinks(expectedChiefLinks));
    }

    protected void assertPersistedChiefLinksToMatchUpdatableProperties(ChiefLinks expectedChiefLinks) {
        // Test fails because reactive api returns an empty object instead of null
        // assertChiefLinksAllUpdatablePropertiesEquals(expectedChiefLinks, getPersistedChiefLinks(expectedChiefLinks));
        assertChiefLinksUpdatableFieldsEquals(expectedChiefLinks, getPersistedChiefLinks(expectedChiefLinks));
    }
}
