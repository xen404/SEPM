package at.ac.tuwien.sepm.groupphase.backend.exception;

public class IllegalOperationException extends RuntimeException {
    public IllegalOperationException() {}

    public IllegalOperationException(String message) {
        super(message);
    }
}
