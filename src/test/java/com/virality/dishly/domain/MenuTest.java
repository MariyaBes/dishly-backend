package com.virality.dishly.domain;

import static com.virality.dishly.domain.ChiefTestSamples.*;
import static com.virality.dishly.domain.DishTestSamples.*;
import static com.virality.dishly.domain.MenuTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.virality.dishly.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MenuTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Menu.class);
        Menu menu1 = getMenuSample1();
        Menu menu2 = new Menu();
        assertThat(menu1).isNotEqualTo(menu2);

        menu2.setId(menu1.getId());
        assertThat(menu1).isEqualTo(menu2);

        menu2 = getMenuSample2();
        assertThat(menu1).isNotEqualTo(menu2);
    }

    @Test
    void dishTest() {
        Menu menu = getMenuRandomSampleGenerator();
        Dish dishBack = getDishRandomSampleGenerator();

        menu.addDish(dishBack);
        assertThat(menu.getDishes()).containsOnly(dishBack);
        assertThat(dishBack.getMenu()).isEqualTo(menu);

        menu.removeDish(dishBack);
        assertThat(menu.getDishes()).doesNotContain(dishBack);
        assertThat(dishBack.getMenu()).isNull();

        menu.dishes(new HashSet<>(Set.of(dishBack)));
        assertThat(menu.getDishes()).containsOnly(dishBack);
        assertThat(dishBack.getMenu()).isEqualTo(menu);

        menu.setDishes(new HashSet<>());
        assertThat(menu.getDishes()).doesNotContain(dishBack);
        assertThat(dishBack.getMenu()).isNull();
    }

    @Test
    void chiefTest() {
        Menu menu = getMenuRandomSampleGenerator();
        Chief chiefBack = getChiefRandomSampleGenerator();

        menu.setChief(chiefBack);
        assertThat(menu.getChief()).isEqualTo(chiefBack);

        menu.chief(null);
        assertThat(menu.getChief()).isNull();
    }
}
