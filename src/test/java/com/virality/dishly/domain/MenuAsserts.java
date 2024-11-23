package com.virality.dishly.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class MenuAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMenuAllPropertiesEquals(Menu expected, Menu actual) {
        assertMenuAutoGeneratedPropertiesEquals(expected, actual);
        assertMenuAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMenuAllUpdatablePropertiesEquals(Menu expected, Menu actual) {
        assertMenuUpdatableFieldsEquals(expected, actual);
        assertMenuUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMenuAutoGeneratedPropertiesEquals(Menu expected, Menu actual) {
        assertThat(expected)
            .as("Verify Menu auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMenuUpdatableFieldsEquals(Menu expected, Menu actual) {
        assertThat(expected)
            .as("Verify Menu relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()))
            .satisfies(e -> assertThat(e.getCreatedAt()).as("check createdAt").isEqualTo(actual.getCreatedAt()))
            .satisfies(e -> assertThat(e.getUpdatedAt()).as("check updatedAt").isEqualTo(actual.getUpdatedAt()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMenuUpdatableRelationshipsEquals(Menu expected, Menu actual) {
        assertThat(expected)
            .as("Verify Menu relationships")
            .satisfies(e -> assertThat(e.getChief()).as("check chief").isEqualTo(actual.getChief()));
    }
}
