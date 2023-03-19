package pt.isel.autorouter;

public class Either<T> {
    private final T value;
    private final Exception error;

    public Either(T value) {
        this.value = value;
        this.error = null;
    }

    public Either(Exception error) {
        this.value = null;
        this.error = error;
    }

    public boolean isSuccess() {
        return value != null;
    }

    public boolean isError() {
        return error != null;
    }

    public T getValue() {
        if (isSuccess()) {
            return value;
        } else {
            throw new IllegalStateException("Cannot get value from error Either");
        }
    }

    public Exception getError() {
        if (isError()) {
            return error;
        } else {
            throw new IllegalStateException("Cannot get error from success Either");
        }
    }
}
