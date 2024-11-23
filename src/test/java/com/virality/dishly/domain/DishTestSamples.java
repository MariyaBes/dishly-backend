package com.virality.dishly.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DishTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Dish getDishSample1() {
        return new Dish().id(1L).name("name1").price(1).preparationTime(1).image("image1").status("status1").weight(1);
    }

    public static Dish getDishSample2() {
        return new Dish().id(2L).name("name2").price(2).preparationTime(2).image("image2").status("status2").weight(2);
    }

    public static Dish getDishRandomSampleGenerator() {
        return new Dish()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .price(intCount.incrementAndGet())
            .preparationTime(intCount.incrementAndGet())
            .image(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .weight(intCount.incrementAndGet());
    }
}
