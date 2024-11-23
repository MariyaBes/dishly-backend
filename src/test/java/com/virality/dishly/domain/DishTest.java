package com.virality.dishly.domain;

import static com.virality.dishly.domain.DishTestSamples.*;
import static com.virality.dishly.domain.KitchenTestSamples.*;
import static com.virality.dishly.domain.MenuTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.virality.dishly.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DishTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dish.class);
        Dish dish1 = getDishSample1();
        Dish dish2 = new Dish();
        assertThat(dish1).isNotEqualTo(dish2);

        dish2.setId(dish1.getId());
        assertThat(dish1).isEqualTo(dish2);

        dish2 = getDishSample2();
        assertThat(dish1).isNotEqualTo(dish2);
    }

    @Test
    void kitchenTest() {
        Dish dish = getDishRandomSampleGenerator();
        Kitchen kitchenBack = getKitchenRandomSampleGenerator();

        dish.setKitchen(kitchenBack);
        assertThat(dish.getKitchen()).isEqualTo(kitchenBack);

        dish.kitchen(null);
        assertThat(dish.getKitchen()).isNull();
    }

    @Test
    void menuTest() {
        Dish dish = getDishRandomSampleGenerator();
        Menu menuBack = getMenuRandomSampleGenerator();

        dish.setMenu(menuBack);
        assertThat(dish.getMenu()).isEqualTo(menuBack);

        dish.menu(null);
        assertThat(dish.getMenu()).isNull();
    }
}
