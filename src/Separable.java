import java.util.concurrent.ExecutionException;

public interface Separable {
    boolean split() throws ExecutionException, InterruptedException;
}
