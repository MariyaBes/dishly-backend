package com.virality.dishly.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAddressAllPropertiesEquals(Address expected, Address actual) {
        assertAddressAutoGeneratedPropertiesEquals(expected, actual);
        assertAddressAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAddressAllUpdatablePropertiesEquals(Address expected, Address actual) {
        assertAddressUpdatableFieldsEquals(expected, actual);
        assertAddressUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAddressAutoGeneratedPropertiesEquals(Address expected, Address actual) {
        assertThat(expected)
            .as("Verify Address auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAddressUpdatableFieldsEquals(Address expected, Address actual) {
        assertThat(expected)
            .as("Verify Address relevant properties")
            .satisfies(e -> assertThat(e.getAddress()).as("check address").isEqualTo(actual.getAddress()))
            .satisfies(e -> assertThat(e.getYmapY()).as("check ymapY").isEqualTo(actual.getYmapY()))
            .satisfies(e -> assertThat(e.getYmapX()).as("check ymapX").isEqualTo(actual.getYmapX()))
            .satisfies(e -> assertThat(e.getCreatedAt()).as("check createdAt").isEqualTo(actual.getCreatedAt()))
            .satisfies(e -> assertThat(e.getUpdatedAt()).as("check updatedAt").isEqualTo(actual.getUpdatedAt()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAddressUpdatableRelationshipsEquals(Address expected, Address actual) {
        assertThat(expected)
            .as("Verify Address relationships")
            .satisfies(e -> assertThat(e.getCity()).as("check city").isEqualTo(actual.getCity()));
    }
}