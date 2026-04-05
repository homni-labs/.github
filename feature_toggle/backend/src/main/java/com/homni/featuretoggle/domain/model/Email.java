package com.homni.featuretoggle.domain.model;

import com.homni.featuretoggle.domain.exception.InvalidEmailException;

/**
 * Email address value object with format validation.
 *
 * @param value the normalized email address
 */
public record Email(String value) {

    /**
     * Creates a validated email address, normalized to lowercase.
     *
     * @param value the raw email string
     */
    public Email {
        if (value == null || value.isBlank()
                || !value.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidEmailException(String.valueOf(value));
        }
        value = value.toLowerCase().trim();
    }
}
