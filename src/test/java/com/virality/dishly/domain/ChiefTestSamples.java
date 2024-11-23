package com.virality.dishly.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ChiefTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Chief getChiefSample1() {
        return new Chief().id(1L).educationDocument("educationDocument1").medicalBook("medicalBook1");
    }

    public static Chief getChiefSample2() {
        return new Chief().id(2L).educationDocument("educationDocument2").medicalBook("medicalBook2");
    }

    public static Chief getChiefRandomSampleGenerator() {
        return new Chief()
            .id(longCount.incrementAndGet())
            .educationDocument(UUID.randomUUID().toString())
            .medicalBook(UUID.randomUUID().toString());
    }
}
