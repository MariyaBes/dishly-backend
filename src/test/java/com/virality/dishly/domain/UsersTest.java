package com.virality.dishly.domain;

import static com.virality.dishly.domain.CityTestSamples.*;
import static com.virality.dishly.domain.UsersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.virality.dishly.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UsersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Users.class);
        Users users1 = getUsersSample1();
        Users users2 = new Users();
        assertThat(users1).isNotEqualTo(users2);

        users2.setId(users1.getId());
        assertThat(users1).isEqualTo(users2);

        users2 = getUsersSample2();
        assertThat(users1).isNotEqualTo(users2);
    }

    @Test
    void cityTest() {
        Users users = getUsersRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        users.setCity(cityBack);
        assertThat(users.getCity()).isEqualTo(cityBack);

        users.city(null);
        assertThat(users.getCity()).isNull();
    }
}
