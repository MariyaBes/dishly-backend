package com.virality.dishly.domain;

import static com.virality.dishly.domain.DishTestSamples.*;
import static com.virality.dishly.domain.OrderItemTestSamples.*;
import static com.virality.dishly.domain.OrdersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.virality.dishly.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItem.class);
        OrderItem orderItem1 = getOrderItemSample1();
        OrderItem orderItem2 = new OrderItem();
        assertThat(orderItem1).isNotEqualTo(orderItem2);

        orderItem2.setId(orderItem1.getId());
        assertThat(orderItem1).isEqualTo(orderItem2);

        orderItem2 = getOrderItemSample2();
        assertThat(orderItem1).isNotEqualTo(orderItem2);
    }

    @Test
    void orderTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        Orders ordersBack = getOrdersRandomSampleGenerator();

        orderItem.setOrder(ordersBack);
        assertThat(orderItem.getOrder()).isEqualTo(ordersBack);

        orderItem.order(null);
        assertThat(orderItem.getOrder()).isNull();
    }

    @Test
    void dishTest() {
        OrderItem orderItem = getOrderItemRandomSampleGenerator();
        Dish dishBack = getDishRandomSampleGenerator();

        orderItem.setDish(dishBack);
        assertThat(orderItem.getDish()).isEqualTo(dishBack);

        orderItem.dish(null);
        assertThat(orderItem.getDish()).isNull();
    }
}
