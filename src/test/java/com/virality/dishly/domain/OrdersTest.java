package com.virality.dishly.domain;

import static com.virality.dishly.domain.ChiefTestSamples.*;
import static com.virality.dishly.domain.CityTestSamples.*;
import static com.virality.dishly.domain.OrdersTestSamples.*;
import static com.virality.dishly.domain.UsersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.virality.dishly.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrdersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Orders.class);
        Orders orders1 = getOrdersSample1();
        Orders orders2 = new Orders();
        assertThat(orders1).isNotEqualTo(orders2);

        orders2.setId(orders1.getId());
        assertThat(orders1).isEqualTo(orders2);

        orders2 = getOrdersSample2();
        assertThat(orders1).isNotEqualTo(orders2);
    }

    @Test
    void userTest() {
        Orders orders = getOrdersRandomSampleGenerator();
        Users usersBack = getUsersRandomSampleGenerator();

        orders.setUser(usersBack);
        assertThat(orders.getUser()).isEqualTo(usersBack);

        orders.user(null);
        assertThat(orders.getUser()).isNull();
    }

    @Test
    void chiefTest() {
        Orders orders = getOrdersRandomSampleGenerator();
        Chief chiefBack = getChiefRandomSampleGenerator();

        orders.setChief(chiefBack);
        assertThat(orders.getChief()).isEqualTo(chiefBack);

        orders.chief(null);
        assertThat(orders.getChief()).isNull();
    }

    @Test
    void cityTest() {
        Orders orders = getOrdersRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        orders.setCity(cityBack);
        assertThat(orders.getCity()).isEqualTo(cityBack);

        orders.city(null);
        assertThat(orders.getCity()).isNull();
    }
}
