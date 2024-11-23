package com.virality.dishly.web.rest;

import static com.virality.dishly.domain.OrdersAsserts.*;
import static com.virality.dishly.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virality.dishly.IntegrationTest;
import com.virality.dishly.domain.Orders;
import com.virality.dishly.domain.enumeration.OrderStatus;
import com.virality.dishly.domain.enumeration.PaymentMethod;
import com.virality.dishly.domain.enumeration.PaymentStatus;
import com.virality.dishly.repository.EntityManager;
import com.virality.dishly.repository.OrdersRepository;
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
 * Integration tests for the {@link OrdersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrdersResourceIT {

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_SUM = 1L;
    private static final Long UPDATED_SUM = 2L;

    private static final PaymentMethod DEFAULT_PAYMENT_METHOD = PaymentMethod.SBP;
    private static final PaymentMethod UPDATED_PAYMENT_METHOD = PaymentMethod.CARD;

    private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.PENDING;
    private static final PaymentStatus UPDATED_PAYMENT_STATUS = PaymentStatus.COMPLETED;

    private static final Long DEFAULT_TRANSACTION_ID = 1L;
    private static final Long UPDATED_TRANSACTION_ID = 2L;

    private static final OrderStatus DEFAULT_ORDER_STATUS = OrderStatus.CREATED;
    private static final OrderStatus UPDATED_ORDER_STATUS = OrderStatus.PAID;

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Orders orders;

    private Orders insertedOrders;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orders createEntity() {
        return new Orders()
            .status(DEFAULT_STATUS)
            .updatedAt(DEFAULT_UPDATED_AT)
            .createdAt(DEFAULT_CREATED_AT)
            .sum(DEFAULT_SUM)
            .paymentMethod(DEFAULT_PAYMENT_METHOD)
            .paymentStatus(DEFAULT_PAYMENT_STATUS)
            .transactionId(DEFAULT_TRANSACTION_ID)
            .orderStatus(DEFAULT_ORDER_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orders createUpdatedEntity() {
        return new Orders()
            .status(UPDATED_STATUS)
            .updatedAt(UPDATED_UPDATED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .sum(UPDATED_SUM)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .transactionId(UPDATED_TRANSACTION_ID)
            .orderStatus(UPDATED_ORDER_STATUS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Orders.class).block();
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
        orders = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedOrders != null) {
            ordersRepository.delete(insertedOrders).block();
            insertedOrders = null;
        }
        deleteEntities(em);
    }

    @Test
    void createOrders() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Orders
        var returnedOrders = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orders))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Orders.class)
            .returnResult()
            .getResponseBody();

        // Validate the Orders in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertOrdersUpdatableFieldsEquals(returnedOrders, getPersistedOrders(returnedOrders));

        insertedOrders = returnedOrders;
    }

    @Test
    void createOrdersWithExistingId() throws Exception {
        // Create the Orders with an existing ID
        orders.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orders))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orders.setStatus(null);

        // Create the Orders, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orders))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        orders.setCreatedAt(null);

        // Create the Orders, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orders))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllOrdersAsStream() {
        // Initialize the database
        ordersRepository.save(orders).block();

        List<Orders> ordersList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Orders.class)
            .getResponseBody()
            .filter(orders::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(ordersList).isNotNull();
        assertThat(ordersList).hasSize(1);
        Orders testOrders = ordersList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertOrdersAllPropertiesEquals(orders, testOrders);
        assertOrdersUpdatableFieldsEquals(orders, testOrders);
    }

    @Test
    void getAllOrders() {
        // Initialize the database
        insertedOrders = ordersRepository.save(orders).block();

        // Get all the ordersList
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
            .value(hasItem(orders.getId().intValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].sum")
            .value(hasItem(DEFAULT_SUM.intValue()))
            .jsonPath("$.[*].paymentMethod")
            .value(hasItem(DEFAULT_PAYMENT_METHOD.toString()))
            .jsonPath("$.[*].paymentStatus")
            .value(hasItem(DEFAULT_PAYMENT_STATUS.toString()))
            .jsonPath("$.[*].transactionId")
            .value(hasItem(DEFAULT_TRANSACTION_ID.intValue()))
            .jsonPath("$.[*].orderStatus")
            .value(hasItem(DEFAULT_ORDER_STATUS.toString()));
    }

    @Test
    void getOrders() {
        // Initialize the database
        insertedOrders = ordersRepository.save(orders).block();

        // Get the orders
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, orders.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(orders.getId().intValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS))
            .jsonPath("$.updatedAt")
            .value(is(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.sum")
            .value(is(DEFAULT_SUM.intValue()))
            .jsonPath("$.paymentMethod")
            .value(is(DEFAULT_PAYMENT_METHOD.toString()))
            .jsonPath("$.paymentStatus")
            .value(is(DEFAULT_PAYMENT_STATUS.toString()))
            .jsonPath("$.transactionId")
            .value(is(DEFAULT_TRANSACTION_ID.intValue()))
            .jsonPath("$.orderStatus")
            .value(is(DEFAULT_ORDER_STATUS.toString()));
    }

    @Test
    void getNonExistingOrders() {
        // Get the orders
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOrders() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.save(orders).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orders
        Orders updatedOrders = ordersRepository.findById(orders.getId()).block();
        updatedOrders
            .status(UPDATED_STATUS)
            .updatedAt(UPDATED_UPDATED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .sum(UPDATED_SUM)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .transactionId(UPDATED_TRANSACTION_ID)
            .orderStatus(UPDATED_ORDER_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedOrders.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedOrders))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrdersToMatchAllProperties(updatedOrders);
    }

    @Test
    void putNonExistingOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orders.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orders))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orders))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orders))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrdersWithPatch() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.save(orders).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orders using partial update
        Orders partialUpdatedOrders = new Orders();
        partialUpdatedOrders.setId(orders.getId());

        partialUpdatedOrders.updatedAt(UPDATED_UPDATED_AT).orderStatus(UPDATED_ORDER_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrders.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrders))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Orders in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrdersUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedOrders, orders), getPersistedOrders(orders));
    }

    @Test
    void fullUpdateOrdersWithPatch() throws Exception {
        // Initialize the database
        insertedOrders = ordersRepository.save(orders).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orders using partial update
        Orders partialUpdatedOrders = new Orders();
        partialUpdatedOrders.setId(orders.getId());

        partialUpdatedOrders
            .status(UPDATED_STATUS)
            .updatedAt(UPDATED_UPDATED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .sum(UPDATED_SUM)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .transactionId(UPDATED_TRANSACTION_ID)
            .orderStatus(UPDATED_ORDER_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrders.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrders))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Orders in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrdersUpdatableFieldsEquals(partialUpdatedOrders, getPersistedOrders(partialUpdatedOrders));
    }

    @Test
    void patchNonExistingOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, orders.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orders))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orders))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orders.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orders))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Orders in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrders() {
        // Initialize the database
        insertedOrders = ordersRepository.save(orders).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orders
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, orders.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ordersRepository.count().block();
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

    protected Orders getPersistedOrders(Orders orders) {
        return ordersRepository.findById(orders.getId()).block();
    }

    protected void assertPersistedOrdersToMatchAllProperties(Orders expectedOrders) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrdersAllPropertiesEquals(expectedOrders, getPersistedOrders(expectedOrders));
        assertOrdersUpdatableFieldsEquals(expectedOrders, getPersistedOrders(expectedOrders));
    }

    protected void assertPersistedOrdersToMatchUpdatableProperties(Orders expectedOrders) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrdersAllUpdatablePropertiesEquals(expectedOrders, getPersistedOrders(expectedOrders));
        assertOrdersUpdatableFieldsEquals(expectedOrders, getPersistedOrders(expectedOrders));
    }
}
