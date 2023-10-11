package com.example.schoolmanagementsystem.util;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateUtilTest {
    private final UpdateUtil updateUtil = new UpdateUtil();
    private final Faker FAKER = new Faker();

    @Test
    void isFieldEmptyOrWithoutChangeShouldReturnTrue() {
        String initialValue = FAKER.lorem().word();

        assertThat(updateUtil.isFieldNullOrWithoutChange(initialValue, initialValue))
                .isTrue();

        assertThat(updateUtil.isFieldNullOrWithoutChange(initialValue, null))
                .isTrue();
    }

    @Test
    void isFieldEmptyOrWithoutChangeShouldReturnFalse() {
        String initialValue = FAKER.lorem().word();
        String updateValue = FAKER.lorem().word();

        assertThat(updateUtil.isFieldNullOrWithoutChange(initialValue, updateValue))
                .isFalse();
    }
}