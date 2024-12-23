package com.virality.dishly.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class UsersAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUsersAllPropertiesEquals(Users expected, Users actual) {
        assertUsersAutoGeneratedPropertiesEquals(expected, actual);
        assertUsersAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUsersAllUpdatablePropertiesEquals(Users expected, Users actual) {
        assertUsersUpdatableFieldsEquals(expected, actual);
        assertUsersUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUsersAutoGeneratedPropertiesEquals(Users expected, Users actual) {
        assertThat(expected)
            .as("Verify Users auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUsersUpdatableFieldsEquals(Users expected, Users actual) {
        assertThat(expected)
            .as("Verify Users relevant properties")
            .satisfies(e -> assertThat(e.getUsername()).as("check username").isEqualTo(actual.getUsername()))
            .satisfies(e -> assertThat(e.getFirstName()).as("check firstName").isEqualTo(actual.getFirstName()))
            .satisfies(e -> assertThat(e.getLastName()).as("check lastName").isEqualTo(actual.getLastName()))
            .satisfies(e -> assertThat(e.getEmail()).as("check email").isEqualTo(actual.getEmail()))
            .satisfies(e -> assertThat(e.getPhone()).as("check phone").isEqualTo(actual.getPhone()))
            .satisfies(e -> assertThat(e.getPasswordHash()).as("check passwordHash").isEqualTo(actual.getPasswordHash()))
            .satisfies(e -> assertThat(e.getImage()).as("check image").isEqualTo(actual.getImage()))
            .satisfies(e -> assertThat(e.getStatus()).as("check status").isEqualTo(actual.getStatus()))
            .satisfies(e -> assertThat(e.getGender()).as("check gender").isEqualTo(actual.getGender()))
            .satisfies(e -> assertThat(e.getRole()).as("check role").isEqualTo(actual.getRole()))
            .satisfies(e -> assertThat(e.getVerificationStatus()).as("check verificationStatus").isEqualTo(actual.getVerificationStatus()))
            .satisfies(e -> assertThat(e.getCreatedAt()).as("check createdAt").isEqualTo(actual.getCreatedAt()))
            .satisfies(e -> assertThat(e.getUpdatedAt()).as("check updatedAt").isEqualTo(actual.getUpdatedAt()))
            .satisfies(e -> assertThat(e.getUserStatus()).as("check userStatus").isEqualTo(actual.getUserStatus()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertUsersUpdatableRelationshipsEquals(Users expected, Users actual) {
        assertThat(expected)
            .as("Verify Users relationships")
            .satisfies(e -> assertThat(e.getCity()).as("check city").isEqualTo(actual.getCity()));
    }
}
