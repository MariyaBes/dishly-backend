package com.virality.dishly.web.rest;

import static com.virality.dishly.domain.AddressAsserts.*;
import static com.virality.dishly.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virality.dishly.IntegrationTest;
import com.virality.dishly.domain.Address;
import com.virality.dishly.repository.AddressRepository;
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
 * Integration tests for the {@link AddressResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AddressResourceIT {

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_YMAP_Y = "AAAAAAAAAA";
    private static final String UPDATED_YMAP_Y = "BBBBBBBBBB";

    private static final String DEFAULT_YMAP_X = "AAAAAAAAAA";
    private static final String UPDATED_YMAP_X = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/addresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Address address;

    private Address insertedAddress;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Address createEntity() {
        return new Address()
            .address(DEFAULT_ADDRESS)
            .ymapY(DEFAULT_YMAP_Y)
            .ymapX(DEFAULT_YMAP_X)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Address createUpdatedEntity() {
        return new Address()
            .address(UPDATED_ADDRESS)
            .ymapY(UPDATED_YMAP_Y)
            .ymapX(UPDATED_YMAP_X)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Address.class).block();
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
        address = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAddress != null) {
            addressRepository.delete(insertedAddress).block();
            insertedAddress = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAddress() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Address
        var returnedAddress = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(address))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Address.class)
            .returnResult()
            .getResponseBody();

        // Validate the Address in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAddressUpdatableFieldsEquals(returnedAddress, getPersistedAddress(returnedAddress));

        insertedAddress = returnedAddress;
    }

    @Test
    void createAddressWithExistingId() throws Exception {
        // Create the Address with an existing ID
        address.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAddressesAsStream() {
        // Initialize the database
        addressRepository.save(address).block();

        List<Address> addressList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Address.class)
            .getResponseBody()
            .filter(address::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(addressList).isNotNull();
        assertThat(addressList).hasSize(1);
        Address testAddress = addressList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertAddressAllPropertiesEquals(address, testAddress);
        assertAddressUpdatableFieldsEquals(address, testAddress);
    }

    @Test
    void getAllAddresses() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get all the addressList
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
            .value(hasItem(address.getId().intValue()))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].ymapY")
            .value(hasItem(DEFAULT_YMAP_Y))
            .jsonPath("$.[*].ymapX")
            .value(hasItem(DEFAULT_YMAP_X))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getAddress() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        // Get the address
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, address.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(address.getId().intValue()))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.ymapY")
            .value(is(DEFAULT_YMAP_Y))
            .jsonPath("$.ymapX")
            .value(is(DEFAULT_YMAP_X))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.updatedAt")
            .value(is(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getNonExistingAddress() {
        // Get the address
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAddress() throws Exception {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the address
        Address updatedAddress = addressRepository.findById(address.getId()).block();
        updatedAddress
            .address(UPDATED_ADDRESS)
            .ymapY(UPDATED_YMAP_Y)
            .ymapX(UPDATED_YMAP_X)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAddress.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedAddress))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAddressToMatchAllProperties(updatedAddress);
    }

    @Test
    void putNonExistingAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        address.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, address.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        address.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        address.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(address))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAddressWithPatch() throws Exception {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the address using partial update
        Address partialUpdatedAddress = new Address();
        partialUpdatedAddress.setId(address.getId());

        partialUpdatedAddress.ymapX(UPDATED_YMAP_X).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAddress.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAddress))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Address in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAddressUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAddress, address), getPersistedAddress(address));
    }

    @Test
    void fullUpdateAddressWithPatch() throws Exception {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the address using partial update
        Address partialUpdatedAddress = new Address();
        partialUpdatedAddress.setId(address.getId());

        partialUpdatedAddress
            .address(UPDATED_ADDRESS)
            .ymapY(UPDATED_YMAP_Y)
            .ymapX(UPDATED_YMAP_X)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAddress.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAddress))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Address in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAddressUpdatableFieldsEquals(partialUpdatedAddress, getPersistedAddress(partialUpdatedAddress));
    }

    @Test
    void patchNonExistingAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        address.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, address.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        address.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(address))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAddress() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        address.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(address))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Address in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAddress() {
        // Initialize the database
        insertedAddress = addressRepository.save(address).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the address
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, address.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return addressRepository.count().block();
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

    protected Address getPersistedAddress(Address address) {
        return addressRepository.findById(address.getId()).block();
    }

    protected void assertPersistedAddressToMatchAllProperties(Address expectedAddress) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAddressAllPropertiesEquals(expectedAddress, getPersistedAddress(expectedAddress));
        assertAddressUpdatableFieldsEquals(expectedAddress, getPersistedAddress(expectedAddress));
    }

    protected void assertPersistedAddressToMatchUpdatableProperties(Address expectedAddress) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAddressAllUpdatablePropertiesEquals(expectedAddress, getPersistedAddress(expectedAddress));
        assertAddressUpdatableFieldsEquals(expectedAddress, getPersistedAddress(expectedAddress));
    }
}
