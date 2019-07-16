import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ForkJoinPool;

public class ForkSeparator implements Separable {

    private final int countThreads;
    private final long maxPartSize;
    private final int buffSize; // = 1000;
    int progress = 0;
    private File file;

    public ForkSeparator(String filePath) throws Exception {
        this(filePath, -1, -1, -1);
    }

    public ForkSeparator(String filePath, int countThreads, long maxPartSize, int buffSize) throws Exception {

        file = new File(filePath);
        if (!file.exists())
            throw new FileNotFoundException();

        if (countThreads > 0)
            this.countThreads = countThreads;
        else
            this.countThreads = 4;
        if (maxPartSize > 0)
            this.maxPartSize = maxPartSize;
        else
            this.maxPartSize = file.length() / 2;
        if (buffSize > 0)
            this.buffSize = buffSize;
        else
            this.buffSize = 1000;
    }


    @Override
    public boolean split() {

        ForkJoinPool forkJoinPool = new ForkJoinPool(countThreads);
        forkJoinPool.invoke(new ForkCopyFilePart(file, 0, file.length(), maxPartSize, buffSize));

        return true;
    }
}
