package pt.isel.autorouter;

public class Either<L, R> {
    private final L left;
    private final R right;
    private final boolean isLeft;

    private Either(L left, R right, boolean isLeft) {
        this.left = left;
        this.right = right;
        this.isLeft = isLeft;
    }

    public static <L, R> Either<L, R> left(L left) {
        return new Either<>(left, null, true);
    }

    public static <L, R> Either<L, R> right(R right) {
        return new Either<>(null, right, false);
    }

    public boolean isLeft() {
        return isLeft;
    }

    public boolean isRight() {
        return !isLeft;
    }

    public L getLeft() {
        if (!isLeft) {
            throw new IllegalStateException("Either is not left");
        }
        return left;
    }

    public R getRight() {
        if (isLeft) {
            throw new IllegalStateException("Either is not right");
        }
        return right;
    }
}
