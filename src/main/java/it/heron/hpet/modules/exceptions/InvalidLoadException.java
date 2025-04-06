package it.heron.hpet.modules.exceptions;

import lombok.NoArgsConstructor;

public @NoArgsConstructor class InvalidLoadException extends RuntimeException {
    public InvalidLoadException(String message) {
        super(message);
    }
}
