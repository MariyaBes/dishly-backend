package com.virality.dishly.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SignatureDishTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SignatureDish getSignatureDishSample1() {
        return new SignatureDish().id(1L).name("name1").image("image1");
    }

    public static SignatureDish getSignatureDishSample2() {
        return new SignatureDish().id(2L).name("name2").image("image2");
    }

    public static SignatureDish getSignatureDishRandomSampleGenerator() {
        return new SignatureDish().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).image(UUID.randomUUID().toString());
    }
}
