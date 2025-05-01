package it.heron.hpet.modules.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidUnloadException extends RuntimeException {
    /**
     * Constructs a new InvalidUnloadException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidUnloadException(String message) {
        super(message);
    }
}
