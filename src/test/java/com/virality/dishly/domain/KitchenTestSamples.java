package com.virality.dishly.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class KitchenTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Kitchen getKitchenSample1() {
        return new Kitchen().id(1L).name("name1").image("image1");
    }

    public static Kitchen getKitchenSample2() {
        return new Kitchen().id(2L).name("name2").image("image2");
    }

    public static Kitchen getKitchenRandomSampleGenerator() {
        return new Kitchen().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).image(UUID.randomUUID().toString());
    }
}
