package com.virality.dishly.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ChiefLinksTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ChiefLinks getChiefLinksSample1() {
        return new ChiefLinks()
            .id(1L)
            .telegramLink("telegramLink1")
            .vkLink("vkLink1")
            .odnoklassnikiLink("odnoklassnikiLink1")
            .youtubeLink("youtubeLink1")
            .rutubeLink("rutubeLink1");
    }

    public static ChiefLinks getChiefLinksSample2() {
        return new ChiefLinks()
            .id(2L)
            .telegramLink("telegramLink2")
            .vkLink("vkLink2")
            .odnoklassnikiLink("odnoklassnikiLink2")
            .youtubeLink("youtubeLink2")
            .rutubeLink("rutubeLink2");
    }

    public static ChiefLinks getChiefLinksRandomSampleGenerator() {
        return new ChiefLinks()
            .id(longCount.incrementAndGet())
            .telegramLink(UUID.randomUUID().toString())
            .vkLink(UUID.randomUUID().toString())
            .odnoklassnikiLink(UUID.randomUUID().toString())
            .youtubeLink(UUID.randomUUID().toString())
            .rutubeLink(UUID.randomUUID().toString());
    }
}
