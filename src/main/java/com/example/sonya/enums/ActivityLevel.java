package com.example.sonya.enums;

import lombok.Getter;

@Getter
public enum ActivityLevel {
    LOW(1.2),
    MEDIUM(1.55),
    HIGH(1.9);

    private final double multiplier;

    ActivityLevel(double multiplier) {
        this.multiplier = multiplier;
    }
}

