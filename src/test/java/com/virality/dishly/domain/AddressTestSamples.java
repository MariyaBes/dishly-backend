package com.virality.dishly.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AddressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Address getAddressSample1() {
        return new Address().id(1L).address("address1").ymapY("ymapY1").ymapX("ymapX1");
    }

    public static Address getAddressSample2() {
        return new Address().id(2L).address("address2").ymapY("ymapY2").ymapX("ymapX2");
    }

    public static Address getAddressRandomSampleGenerator() {
        return new Address()
            .id(longCount.incrementAndGet())
            .address(UUID.randomUUID().toString())
            .ymapY(UUID.randomUUID().toString())
            .ymapX(UUID.randomUUID().toString());
    }
}
