package com.virality.dishly.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UsersTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Users getUsersSample1() {
        return new Users()
            .id(1L)
            .username("username1")
            .firstName("firstName1")
            .lastName("lastName1")
            .email("email1")
            .phone("phone1")
            .passwordHash("passwordHash1")
            .image("image1")
            .status("status1");
    }

    public static Users getUsersSample2() {
        return new Users()
            .id(2L)
            .username("username2")
            .firstName("firstName2")
            .lastName("lastName2")
            .email("email2")
            .phone("phone2")
            .passwordHash("passwordHash2")
            .image("image2")
            .status("status2");
    }

    public static Users getUsersRandomSampleGenerator() {
        return new Users()
            .id(longCount.incrementAndGet())
            .username(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .passwordHash(UUID.randomUUID().toString())
            .image(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString());
    }
}
