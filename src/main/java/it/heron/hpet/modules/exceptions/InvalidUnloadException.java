package it.heron.hpet.modules.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidUnloadException extends RuntimeException {
    public InvalidUnloadException(String message) {
        super(message);
    }
}
