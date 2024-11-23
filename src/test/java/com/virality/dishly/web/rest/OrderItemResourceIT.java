package com.virality.dishly.web.rest;

import static com.virality.dishly.domain.OrderItemAsserts.*;
import static com.virality.dishly.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virality.dishly.IntegrationTest;
import com.virality.dishly.domain.OrderItem;
import com.virality.dishly.repository.EntityManager;
import com.virality.dishly.repository.OrderItemRepository;
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
 * Integration tests for the {@link OrderItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrderItemResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final Integer DEFAULT_PRICE = 1;
    private static final Integer UPDATED_PRICE = 2;

    private static final Integer DEFAULT_TOTAL_PRICE = 1;
    private static final Integer UPDATED_TOTAL_PRICE = 2;

    private static final String ENTITY_API_URL = "/api/order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OrderItem orderItem;

    private OrderItem insertedOrderItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItem createEntity() {
        return new OrderItem().quantity(DEFAULT_QUANTITY).price(DEFAULT_PRICE).totalPrice(DEFAULT_TOTAL_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItem createUpdatedEntity() {
        return new OrderItem().quantity(UPDATED_QUANTITY).price(UPDATED_PRICE).totalPrice(UPDATED_TOTAL_PRICE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OrderItem.class).block();
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
        orderItem = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedOrderItem != null) {
            orderItemRepository.delete(insertedOrderItem).block();
            insertedOrderItem = null;
        }
        deleteEntities(em);
    }

    @Test
    void createOrderItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OrderItem
        var returnedOrderItem = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItem))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OrderItem.class)
            .returnResult()
            .getResponseBody();

        // Validate the OrderItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertOrderItemUpdatableFieldsEquals(returnedOrderItem, getPersistedOrderItem(returnedOrderItem));

        insertedOrderItem = returnedOrderItem;
    }

    @Test
    void createOrderItemWithExistingId() throws Exception {
        // Create the OrderItem with an existing ID
        orderItem.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllOrderItemsAsStream() {
        // Initialize the database
        orderItemRepository.save(orderItem).block();

        List<OrderItem> orderItemList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(OrderItem.class)
            .getResponseBody()
            .filter(orderItem::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(orderItemList).isNotNull();
        assertThat(orderItemList).hasSize(1);
        OrderItem testOrderItem = orderItemList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertOrderItemAllPropertiesEquals(orderItem, testOrderItem);
        assertOrderItemUpdatableFieldsEquals(orderItem, testOrderItem);
    }

    @Test
    void getAllOrderItems() {
        // Initialize the database
        insertedOrderItem = orderItemRepository.save(orderItem).block();

        // Get all the orderItemList
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
            .value(hasItem(orderItem.getId().intValue()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].price")
            .value(hasItem(DEFAULT_PRICE))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(DEFAULT_TOTAL_PRICE));
    }

    @Test
    void getOrderItem() {
        // Initialize the database
        insertedOrderItem = orderItemRepository.save(orderItem).block();

        // Get the orderItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, orderItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(orderItem.getId().intValue()))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.price")
            .value(is(DEFAULT_PRICE))
            .jsonPath("$.totalPrice")
            .value(is(DEFAULT_TOTAL_PRICE));
    }

    @Test
    void getNonExistingOrderItem() {
        // Get the orderItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOrderItem() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.save(orderItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItem
        OrderItem updatedOrderItem = orderItemRepository.findById(orderItem.getId()).block();
        updatedOrderItem.quantity(UPDATED_QUANTITY).price(UPDATED_PRICE).totalPrice(UPDATED_TOTAL_PRICE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedOrderItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedOrderItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrderItemToMatchAllProperties(updatedOrderItem);
    }

    @Test
    void putNonExistingOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(orderItem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.save(orderItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItem using partial update
        OrderItem partialUpdatedOrderItem = new OrderItem();
        partialUpdatedOrderItem.setId(orderItem.getId());

        partialUpdatedOrderItem.quantity(UPDATED_QUANTITY).price(UPDATED_PRICE).totalPrice(UPDATED_TOTAL_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrderItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrderItem, orderItem),
            getPersistedOrderItem(orderItem)
        );
    }

    @Test
    void fullUpdateOrderItemWithPatch() throws Exception {
        // Initialize the database
        insertedOrderItem = orderItemRepository.save(orderItem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the orderItem using partial update
        OrderItem partialUpdatedOrderItem = new OrderItem();
        partialUpdatedOrderItem.setId(orderItem.getId());

        partialUpdatedOrderItem.quantity(UPDATED_QUANTITY).price(UPDATED_PRICE).totalPrice(UPDATED_TOTAL_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOrderItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrderItemUpdatableFieldsEquals(partialUpdatedOrderItem, getPersistedOrderItem(partialUpdatedOrderItem));
    }

    @Test
    void patchNonExistingOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, orderItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrderItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        orderItem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(orderItem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrderItem() {
        // Initialize the database
        insertedOrderItem = orderItemRepository.save(orderItem).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the orderItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, orderItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return orderItemRepository.count().block();
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

    protected OrderItem getPersistedOrderItem(OrderItem orderItem) {
        return orderItemRepository.findById(orderItem.getId()).block();
    }

    protected void assertPersistedOrderItemToMatchAllProperties(OrderItem expectedOrderItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrderItemAllPropertiesEquals(expectedOrderItem, getPersistedOrderItem(expectedOrderItem));
        assertOrderItemUpdatableFieldsEquals(expectedOrderItem, getPersistedOrderItem(expectedOrderItem));
    }

    protected void assertPersistedOrderItemToMatchUpdatableProperties(OrderItem expectedOrderItem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOrderItemAllUpdatablePropertiesEquals(expectedOrderItem, getPersistedOrderItem(expectedOrderItem));
        assertOrderItemUpdatableFieldsEquals(expectedOrderItem, getPersistedOrderItem(expectedOrderItem));
    }
}
