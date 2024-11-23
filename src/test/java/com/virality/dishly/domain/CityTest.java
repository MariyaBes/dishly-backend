package com.virality.dishly.domain;

import static com.virality.dishly.domain.AddressTestSamples.*;
import static com.virality.dishly.domain.CityTestSamples.*;
import static com.virality.dishly.domain.UsersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.virality.dishly.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(City.class);
        City city1 = getCitySample1();
        City city2 = new City();
        assertThat(city1).isNotEqualTo(city2);

        city2.setId(city1.getId());
        assertThat(city1).isEqualTo(city2);

        city2 = getCitySample2();
        assertThat(city1).isNotEqualTo(city2);
    }

    @Test
    void addreeTest() {
        City city = getCityRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        city.addAddree(addressBack);
        assertThat(city.getAddrees()).containsOnly(addressBack);
        assertThat(addressBack.getCity()).isEqualTo(city);

        city.removeAddree(addressBack);
        assertThat(city.getAddrees()).doesNotContain(addressBack);
        assertThat(addressBack.getCity()).isNull();

        city.addrees(new HashSet<>(Set.of(addressBack)));
        assertThat(city.getAddrees()).containsOnly(addressBack);
        assertThat(addressBack.getCity()).isEqualTo(city);

        city.setAddrees(new HashSet<>());
        assertThat(city.getAddrees()).doesNotContain(addressBack);
        assertThat(addressBack.getCity()).isNull();
    }

    @Test
    void userTest() {
        City city = getCityRandomSampleGenerator();
        Users usersBack = getUsersRandomSampleGenerator();

        city.addUser(usersBack);
        assertThat(city.getUsers()).containsOnly(usersBack);
        assertThat(usersBack.getCity()).isEqualTo(city);

        city.removeUser(usersBack);
        assertThat(city.getUsers()).doesNotContain(usersBack);
        assertThat(usersBack.getCity()).isNull();

        city.users(new HashSet<>(Set.of(usersBack)));
        assertThat(city.getUsers()).containsOnly(usersBack);
        assertThat(usersBack.getCity()).isEqualTo(city);

        city.setUsers(new HashSet<>());
        assertThat(city.getUsers()).doesNotContain(usersBack);
        assertThat(usersBack.getCity()).isNull();
    }
}
