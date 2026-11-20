package alquimia.domain.exceptions;

/** Base type of every business error raised by the domain. */
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }
}
