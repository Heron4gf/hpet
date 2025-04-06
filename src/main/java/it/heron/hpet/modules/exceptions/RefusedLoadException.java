package it.heron.hpet.modules.exceptions;

import lombok.NoArgsConstructor;

public @NoArgsConstructor class RefusedLoadException extends RuntimeException {
    public RefusedLoadException(String message) {
        super(message);
    }
}
