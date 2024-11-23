package com.virality.dishly.domain;

import static com.virality.dishly.domain.DishTestSamples.*;
import static com.virality.dishly.domain.KitchenTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.virality.dishly.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class KitchenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Kitchen.class);
        Kitchen kitchen1 = getKitchenSample1();
        Kitchen kitchen2 = new Kitchen();
        assertThat(kitchen1).isNotEqualTo(kitchen2);

        kitchen2.setId(kitchen1.getId());
        assertThat(kitchen1).isEqualTo(kitchen2);

        kitchen2 = getKitchenSample2();
        assertThat(kitchen1).isNotEqualTo(kitchen2);
    }

    @Test
    void dishTest() {
        Kitchen kitchen = getKitchenRandomSampleGenerator();
        Dish dishBack = getDishRandomSampleGenerator();

        kitchen.addDish(dishBack);
        assertThat(kitchen.getDishes()).containsOnly(dishBack);
        assertThat(dishBack.getKitchen()).isEqualTo(kitchen);

        kitchen.removeDish(dishBack);
        assertThat(kitchen.getDishes()).doesNotContain(dishBack);
        assertThat(dishBack.getKitchen()).isNull();

        kitchen.dishes(new HashSet<>(Set.of(dishBack)));
        assertThat(kitchen.getDishes()).containsOnly(dishBack);
        assertThat(dishBack.getKitchen()).isEqualTo(kitchen);

        kitchen.setDishes(new HashSet<>());
        assertThat(kitchen.getDishes()).doesNotContain(dishBack);
        assertThat(dishBack.getKitchen()).isNull();
    }
}
