package com.virality.dishly.web.rest;

import static com.virality.dishly.domain.UsersAsserts.*;
import static com.virality.dishly.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virality.dishly.IntegrationTest;
import com.virality.dishly.domain.Users;
import com.virality.dishly.domain.enumeration.Gender;
import com.virality.dishly.domain.enumeration.Role;
import com.virality.dishly.domain.enumeration.UserStatus;
import com.virality.dishly.domain.enumeration.VerificationStatus;
import com.virality.dishly.repository.EntityManager;
import com.virality.dishly.repository.UsersRepository;
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
 * Integration tests for the {@link UsersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UsersResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD_HASH = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD_HASH = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final Role DEFAULT_ROLE = Role.ROOT;
    private static final Role UPDATED_ROLE = Role.ADMIN;

    private static final VerificationStatus DEFAULT_VERIFICATION_STATUS = VerificationStatus.VERIFIED;
    private static final VerificationStatus UPDATED_VERIFICATION_STATUS = VerificationStatus.NOT_VERIFIED;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final UserStatus DEFAULT_USER_STATUS = UserStatus.ACTIVE;
    private static final UserStatus UPDATED_USER_STATUS = UserStatus.BLOCKED;

    private static final String ENTITY_API_URL = "/api/users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Users users;

    private Users insertedUsers;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Users createEntity() {
        return new Users()
            .username(DEFAULT_USERNAME)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .passwordHash(DEFAULT_PASSWORD_HASH)
            .image(DEFAULT_IMAGE)
            .status(DEFAULT_STATUS)
            .gender(DEFAULT_GENDER)
            .role(DEFAULT_ROLE)
            .verificationStatus(DEFAULT_VERIFICATION_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .userStatus(DEFAULT_USER_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Users createUpdatedEntity() {
        return new Users()
            .username(UPDATED_USERNAME)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .image(UPDATED_IMAGE)
            .status(UPDATED_STATUS)
            .gender(UPDATED_GENDER)
            .role(UPDATED_ROLE)
            .verificationStatus(UPDATED_VERIFICATION_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .userStatus(UPDATED_USER_STATUS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Users.class).block();
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
        users = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedUsers != null) {
            usersRepository.delete(insertedUsers).block();
            insertedUsers = null;
        }
        deleteEntities(em);
    }

    @Test
    void createUsers() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Users
        var returnedUsers = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(users))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Users.class)
            .returnResult()
            .getResponseBody();

        // Validate the Users in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertUsersUpdatableFieldsEquals(returnedUsers, getPersistedUsers(returnedUsers));

        insertedUsers = returnedUsers;
    }

    @Test
    void createUsersWithExistingId() throws Exception {
        // Create the Users with an existing ID
        users.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkUsernameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        users.setUsername(null);

        // Create the Users, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        users.setEmail(null);

        // Create the Users, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPasswordHashIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        users.setPasswordHash(null);

        // Create the Users, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllUsersAsStream() {
        // Initialize the database
        usersRepository.save(users).block();

        List<Users> usersList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Users.class)
            .getResponseBody()
            .filter(users::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(usersList).isNotNull();
        assertThat(usersList).hasSize(1);
        Users testUsers = usersList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertUsersAllPropertiesEquals(users, testUsers);
        assertUsersUpdatableFieldsEquals(users, testUsers);
    }

    @Test
    void getAllUsers() {
        // Initialize the database
        insertedUsers = usersRepository.save(users).block();

        // Get all the usersList
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
            .value(hasItem(users.getId().intValue()))
            .jsonPath("$.[*].username")
            .value(hasItem(DEFAULT_USERNAME))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].passwordHash")
            .value(hasItem(DEFAULT_PASSWORD_HASH))
            .jsonPath("$.[*].image")
            .value(hasItem(DEFAULT_IMAGE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS))
            .jsonPath("$.[*].gender")
            .value(hasItem(DEFAULT_GENDER.toString()))
            .jsonPath("$.[*].role")
            .value(hasItem(DEFAULT_ROLE.toString()))
            .jsonPath("$.[*].verificationStatus")
            .value(hasItem(DEFAULT_VERIFICATION_STATUS.toString()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.[*].userStatus")
            .value(hasItem(DEFAULT_USER_STATUS.toString()));
    }

    @Test
    void getUsers() {
        // Initialize the database
        insertedUsers = usersRepository.save(users).block();

        // Get the users
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, users.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(users.getId().intValue()))
            .jsonPath("$.username")
            .value(is(DEFAULT_USERNAME))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE))
            .jsonPath("$.passwordHash")
            .value(is(DEFAULT_PASSWORD_HASH))
            .jsonPath("$.image")
            .value(is(DEFAULT_IMAGE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS))
            .jsonPath("$.gender")
            .value(is(DEFAULT_GENDER.toString()))
            .jsonPath("$.role")
            .value(is(DEFAULT_ROLE.toString()))
            .jsonPath("$.verificationStatus")
            .value(is(DEFAULT_VERIFICATION_STATUS.toString()))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.updatedAt")
            .value(is(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.userStatus")
            .value(is(DEFAULT_USER_STATUS.toString()));
    }

    @Test
    void getNonExistingUsers() {
        // Get the users
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingUsers() throws Exception {
        // Initialize the database
        insertedUsers = usersRepository.save(users).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the users
        Users updatedUsers = usersRepository.findById(users.getId()).block();
        updatedUsers
            .username(UPDATED_USERNAME)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .image(UPDATED_IMAGE)
            .status(UPDATED_STATUS)
            .gender(UPDATED_GENDER)
            .role(UPDATED_ROLE)
            .verificationStatus(UPDATED_VERIFICATION_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .userStatus(UPDATED_USER_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUsers.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedUsers))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUsersToMatchAllProperties(updatedUsers);
    }

    @Test
    void putNonExistingUsers() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        users.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, users.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUsers() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        users.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUsers() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        users.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(users))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUsersWithPatch() throws Exception {
        // Initialize the database
        insertedUsers = usersRepository.save(users).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the users using partial update
        Users partialUpdatedUsers = new Users();
        partialUpdatedUsers.setId(users.getId());

        partialUpdatedUsers
            .firstName(UPDATED_FIRST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .status(UPDATED_STATUS)
            .role(UPDATED_ROLE)
            .verificationStatus(UPDATED_VERIFICATION_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .userStatus(UPDATED_USER_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUsers.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUsers))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Users in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUsersUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUsers, users), getPersistedUsers(users));
    }

    @Test
    void fullUpdateUsersWithPatch() throws Exception {
        // Initialize the database
        insertedUsers = usersRepository.save(users).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the users using partial update
        Users partialUpdatedUsers = new Users();
        partialUpdatedUsers.setId(users.getId());

        partialUpdatedUsers
            .username(UPDATED_USERNAME)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .image(UPDATED_IMAGE)
            .status(UPDATED_STATUS)
            .gender(UPDATED_GENDER)
            .role(UPDATED_ROLE)
            .verificationStatus(UPDATED_VERIFICATION_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .userStatus(UPDATED_USER_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUsers.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUsers))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Users in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUsersUpdatableFieldsEquals(partialUpdatedUsers, getPersistedUsers(partialUpdatedUsers));
    }

    @Test
    void patchNonExistingUsers() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        users.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, users.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUsers() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        users.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUsers() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        users.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(users))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Users in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUsers() {
        // Initialize the database
        insertedUsers = usersRepository.save(users).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the users
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, users.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return usersRepository.count().block();
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

    protected Users getPersistedUsers(Users users) {
        return usersRepository.findById(users.getId()).block();
    }

    protected void assertPersistedUsersToMatchAllProperties(Users expectedUsers) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUsersAllPropertiesEquals(expectedUsers, getPersistedUsers(expectedUsers));
        assertUsersUpdatableFieldsEquals(expectedUsers, getPersistedUsers(expectedUsers));
    }

    protected void assertPersistedUsersToMatchUpdatableProperties(Users expectedUsers) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUsersAllUpdatablePropertiesEquals(expectedUsers, getPersistedUsers(expectedUsers));
        assertUsersUpdatableFieldsEquals(expectedUsers, getPersistedUsers(expectedUsers));
    }
}
