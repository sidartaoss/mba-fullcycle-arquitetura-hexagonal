package br.com.fullcycle.hexagonal.application.domain.person;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

public record Email(
        String value
) {

    public Email {
        if (value == null || !value.matches("^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$")) {
            throw new ValidationException("Invalid value for Email");
        }
    }
}
