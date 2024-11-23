package com.virality.dishly.web.rest;

import static com.virality.dishly.domain.CityAsserts.*;
import static com.virality.dishly.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virality.dishly.IntegrationTest;
import com.virality.dishly.domain.City;
import com.virality.dishly.repository.CityRepository;
import com.virality.dishly.repository.EntityManager;
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
 * Integration tests for the {@link CityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CityResourceIT {

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_HAS_OBJECT = false;
    private static final Boolean UPDATED_HAS_OBJECT = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/cities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private City city;

    private City insertedCity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static City createEntity() {
        return new City().city(DEFAULT_CITY).hasObject(DEFAULT_HAS_OBJECT).createdAt(DEFAULT_CREATED_AT).updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static City createUpdatedEntity() {
        return new City().city(UPDATED_CITY).hasObject(UPDATED_HAS_OBJECT).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(City.class).block();
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
        city = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCity != null) {
            cityRepository.delete(insertedCity).block();
            insertedCity = null;
        }
        deleteEntities(em);
    }

    @Test
    void createCity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the City
        var returnedCity = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(city))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(City.class)
            .returnResult()
            .getResponseBody();

        // Validate the City in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCityUpdatableFieldsEquals(returnedCity, getPersistedCity(returnedCity));

        insertedCity = returnedCity;
    }

    @Test
    void createCityWithExistingId() throws Exception {
        // Create the City with an existing ID
        city.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(city))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCitiesAsStream() {
        // Initialize the database
        cityRepository.save(city).block();

        List<City> cityList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(City.class)
            .getResponseBody()
            .filter(city::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(cityList).isNotNull();
        assertThat(cityList).hasSize(1);
        City testCity = cityList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertCityAllPropertiesEquals(city, testCity);
        assertCityUpdatableFieldsEquals(city, testCity);
    }

    @Test
    void getAllCities() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get all the cityList
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
            .value(hasItem(city.getId().intValue()))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].hasObject")
            .value(hasItem(DEFAULT_HAS_OBJECT.booleanValue()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getCity() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        // Get the city
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, city.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(city.getId().intValue()))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.hasObject")
            .value(is(DEFAULT_HAS_OBJECT.booleanValue()))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.updatedAt")
            .value(is(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getNonExistingCity() {
        // Get the city
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCity() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the city
        City updatedCity = cityRepository.findById(city.getId()).block();
        updatedCity.city(UPDATED_CITY).hasObject(UPDATED_HAS_OBJECT).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCity.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedCity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCityToMatchAllProperties(updatedCity);
    }

    @Test
    void putNonExistingCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        city.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, city.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(city))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        city.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(city))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        city.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(city))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCityWithPatch() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the city using partial update
        City partialUpdatedCity = new City();
        partialUpdatedCity.setId(city.getId());

        partialUpdatedCity.hasObject(UPDATED_HAS_OBJECT).updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the City in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCity, city), getPersistedCity(city));
    }

    @Test
    void fullUpdateCityWithPatch() throws Exception {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the city using partial update
        City partialUpdatedCity = new City();
        partialUpdatedCity.setId(city.getId());

        partialUpdatedCity.city(UPDATED_CITY).hasObject(UPDATED_HAS_OBJECT).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the City in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCityUpdatableFieldsEquals(partialUpdatedCity, getPersistedCity(partialUpdatedCity));
    }

    @Test
    void patchNonExistingCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        city.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, city.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(city))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        city.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(city))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        city.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(city))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the City in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCity() {
        // Initialize the database
        insertedCity = cityRepository.save(city).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the city
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, city.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cityRepository.count().block();
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

    protected City getPersistedCity(City city) {
        return cityRepository.findById(city.getId()).block();
    }

    protected void assertPersistedCityToMatchAllProperties(City expectedCity) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCityAllPropertiesEquals(expectedCity, getPersistedCity(expectedCity));
        assertCityUpdatableFieldsEquals(expectedCity, getPersistedCity(expectedCity));
    }

    protected void assertPersistedCityToMatchUpdatableProperties(City expectedCity) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCityAllUpdatablePropertiesEquals(expectedCity, getPersistedCity(expectedCity));
        assertCityUpdatableFieldsEquals(expectedCity, getPersistedCity(expectedCity));
    }
}
