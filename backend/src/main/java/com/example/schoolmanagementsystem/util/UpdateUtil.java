package com.example.schoolmanagementsystem.util;

import org.springframework.stereotype.Component;

@Component
public class UpdateUtil {
    public <T> boolean isFieldNullOrWithoutChange(T initialValue, T updateValue) {
        return updateValue == null || updateValue.equals(initialValue);
    }
}
