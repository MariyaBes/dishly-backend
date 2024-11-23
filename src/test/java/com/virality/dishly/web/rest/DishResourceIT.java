package com.virality.dishly.web.rest;

import static com.virality.dishly.domain.DishAsserts.*;
import static com.virality.dishly.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virality.dishly.IntegrationTest;
import com.virality.dishly.domain.Dish;
import com.virality.dishly.domain.enumeration.DishStatus;
import com.virality.dishly.repository.DishRepository;
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
 * Integration tests for the {@link DishResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DishResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRICE = 1;
    private static final Integer UPDATED_PRICE = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_PREPARATION_TIME = 1;
    private static final Integer UPDATED_PREPARATION_TIME = 2;

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_COMPOSITION = "AAAAAAAAAA";
    private static final String UPDATED_COMPOSITION = "BBBBBBBBBB";

    private static final Integer DEFAULT_WEIGHT = 1;
    private static final Integer UPDATED_WEIGHT = 2;

    private static final DishStatus DEFAULT_DISH_STATUS = DishStatus.AVAILABLE;
    private static final DishStatus UPDATED_DISH_STATUS = DishStatus.NOT_AVAILABLE;

    private static final String ENTITY_API_URL = "/api/dishes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Dish dish;

    private Dish insertedDish;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dish createEntity() {
        return new Dish()
            .name(DEFAULT_NAME)
            .price(DEFAULT_PRICE)
            .description(DEFAULT_DESCRIPTION)
            .preparationTime(DEFAULT_PREPARATION_TIME)
            .image(DEFAULT_IMAGE)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .composition(DEFAULT_COMPOSITION)
            .weight(DEFAULT_WEIGHT)
            .dishStatus(DEFAULT_DISH_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dish createUpdatedEntity() {
        return new Dish()
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .description(UPDATED_DESCRIPTION)
            .preparationTime(UPDATED_PREPARATION_TIME)
            .image(UPDATED_IMAGE)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .composition(UPDATED_COMPOSITION)
            .weight(UPDATED_WEIGHT)
            .dishStatus(UPDATED_DISH_STATUS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Dish.class).block();
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
        dish = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDish != null) {
            dishRepository.delete(insertedDish).block();
            insertedDish = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDish() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Dish
        var returnedDish = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dish))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Dish.class)
            .returnResult()
            .getResponseBody();

        // Validate the Dish in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDishUpdatableFieldsEquals(returnedDish, getPersistedDish(returnedDish));

        insertedDish = returnedDish;
    }

    @Test
    void createDishWithExistingId() throws Exception {
        // Create the Dish with an existing ID
        dish.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dish))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dish in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllDishesAsStream() {
        // Initialize the database
        dishRepository.save(dish).block();

        List<Dish> dishList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Dish.class)
            .getResponseBody()
            .filter(dish::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(dishList).isNotNull();
        assertThat(dishList).hasSize(1);
        Dish testDish = dishList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertDishAllPropertiesEquals(dish, testDish);
        assertDishUpdatableFieldsEquals(dish, testDish);
    }

    @Test
    void getAllDishes() {
        // Initialize the database
        insertedDish = dishRepository.save(dish).block();

        // Get all the dishList
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
            .value(hasItem(dish.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].price")
            .value(hasItem(DEFAULT_PRICE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].preparationTime")
            .value(hasItem(DEFAULT_PREPARATION_TIME))
            .jsonPath("$.[*].image")
            .value(hasItem(DEFAULT_IMAGE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.[*].composition")
            .value(hasItem(DEFAULT_COMPOSITION.toString()))
            .jsonPath("$.[*].weight")
            .value(hasItem(DEFAULT_WEIGHT))
            .jsonPath("$.[*].dishStatus")
            .value(hasItem(DEFAULT_DISH_STATUS.toString()));
    }

    @Test
    void getDish() {
        // Initialize the database
        insertedDish = dishRepository.save(dish).block();

        // Get the dish
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, dish.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(dish.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.price")
            .value(is(DEFAULT_PRICE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.preparationTime")
            .value(is(DEFAULT_PREPARATION_TIME))
            .jsonPath("$.image")
            .value(is(DEFAULT_IMAGE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.updatedAt")
            .value(is(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.composition")
            .value(is(DEFAULT_COMPOSITION.toString()))
            .jsonPath("$.weight")
            .value(is(DEFAULT_WEIGHT))
            .jsonPath("$.dishStatus")
            .value(is(DEFAULT_DISH_STATUS.toString()));
    }

    @Test
    void getNonExistingDish() {
        // Get the dish
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDish() throws Exception {
        // Initialize the database
        insertedDish = dishRepository.save(dish).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dish
        Dish updatedDish = dishRepository.findById(dish.getId()).block();
        updatedDish
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .description(UPDATED_DESCRIPTION)
            .preparationTime(UPDATED_PREPARATION_TIME)
            .image(UPDATED_IMAGE)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .composition(UPDATED_COMPOSITION)
            .weight(UPDATED_WEIGHT)
            .dishStatus(UPDATED_DISH_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDish.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedDish))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Dish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDishToMatchAllProperties(updatedDish);
    }

    @Test
    void putNonExistingDish() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dish.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, dish.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dish))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDish() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dish.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dish))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDish() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dish.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dish))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Dish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDishWithPatch() throws Exception {
        // Initialize the database
        insertedDish = dishRepository.save(dish).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dish using partial update
        Dish partialUpdatedDish = new Dish();
        partialUpdatedDish.setId(dish.getId());

        partialUpdatedDish
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .preparationTime(UPDATED_PREPARATION_TIME)
            .createdAt(UPDATED_CREATED_AT)
            .dishStatus(UPDATED_DISH_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDish.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDish))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Dish in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDishUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDish, dish), getPersistedDish(dish));
    }

    @Test
    void fullUpdateDishWithPatch() throws Exception {
        // Initialize the database
        insertedDish = dishRepository.save(dish).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dish using partial update
        Dish partialUpdatedDish = new Dish();
        partialUpdatedDish.setId(dish.getId());

        partialUpdatedDish
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .description(UPDATED_DESCRIPTION)
            .preparationTime(UPDATED_PREPARATION_TIME)
            .image(UPDATED_IMAGE)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .composition(UPDATED_COMPOSITION)
            .weight(UPDATED_WEIGHT)
            .dishStatus(UPDATED_DISH_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDish.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDish))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Dish in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDishUpdatableFieldsEquals(partialUpdatedDish, getPersistedDish(partialUpdatedDish));
    }

    @Test
    void patchNonExistingDish() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dish.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, dish.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(dish))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDish() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dish.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(dish))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDish() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dish.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(dish))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Dish in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDish() {
        // Initialize the database
        insertedDish = dishRepository.save(dish).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dish
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, dish.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return dishRepository.count().block();
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

    protected Dish getPersistedDish(Dish dish) {
        return dishRepository.findById(dish.getId()).block();
    }

    protected void assertPersistedDishToMatchAllProperties(Dish expectedDish) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDishAllPropertiesEquals(expectedDish, getPersistedDish(expectedDish));
        assertDishUpdatableFieldsEquals(expectedDish, getPersistedDish(expectedDish));
    }

    protected void assertPersistedDishToMatchUpdatableProperties(Dish expectedDish) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDishAllUpdatablePropertiesEquals(expectedDish, getPersistedDish(expectedDish));
        assertDishUpdatableFieldsEquals(expectedDish, getPersistedDish(expectedDish));
    }
}
