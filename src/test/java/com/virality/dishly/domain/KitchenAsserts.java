package com.virality.dishly.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class KitchenAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertKitchenAllPropertiesEquals(Kitchen expected, Kitchen actual) {
        assertKitchenAutoGeneratedPropertiesEquals(expected, actual);
        assertKitchenAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertKitchenAllUpdatablePropertiesEquals(Kitchen expected, Kitchen actual) {
        assertKitchenUpdatableFieldsEquals(expected, actual);
        assertKitchenUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertKitchenAutoGeneratedPropertiesEquals(Kitchen expected, Kitchen actual) {
        assertThat(expected)
            .as("Verify Kitchen auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertKitchenUpdatableFieldsEquals(Kitchen expected, Kitchen actual) {
        assertThat(expected)
            .as("Verify Kitchen relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()))
            .satisfies(e -> assertThat(e.getImage()).as("check image").isEqualTo(actual.getImage()))
            .satisfies(e -> assertThat(e.getCreatedAt()).as("check createdAt").isEqualTo(actual.getCreatedAt()))
            .satisfies(e -> assertThat(e.getUpdatedAt()).as("check updatedAt").isEqualTo(actual.getUpdatedAt()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertKitchenUpdatableRelationshipsEquals(Kitchen expected, Kitchen actual) {
        // empty method
    }
}