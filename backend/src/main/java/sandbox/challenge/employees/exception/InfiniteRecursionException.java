package sandbox.challenge.employees.exception;

public class InfiniteRecursionException extends RuntimeException {
    public InfiniteRecursionException(String message) {
        super(message);
    }
}
