import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Separator implements Separable {

    private final int countThreads;
    private final long partSize; // = 736_966_656; // CD-disk )
    private final int buffSize; // = 1000;
    int progress = 0;
    private File file;

    public Separator(String filePath) throws Exception {
        this(filePath, -1, -1, -1);
    }

    public Separator(String filePath, int countThreads, long partSize, int buffSize) throws Exception {

        file = new File(filePath);
        if (!file.exists())
            throw new FileNotFoundException();

        if (countThreads > 0)
            this.countThreads = countThreads;
        else
            this.countThreads = 4;
        if (partSize > 0)
            this.partSize = partSize;
        else
            this.partSize = file.length() / 2;
        if (buffSize > 0)
            this.buffSize = buffSize;
        else
            this.buffSize = 1000;
    }

    @Override
    public boolean split() throws ExecutionException, InterruptedException {

        long fileLength = file.length();

        if (fileLength < partSize) {
            System.out.println("File " + file.getName() + " less as part size. Exit");
            return false;
        }

        int parts = (int) (fileLength / partSize);
        if ((fileLength % partSize) != 0)
            parts++;

        ExecutorService executor = Executors.newFixedThreadPool(countThreads);
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < parts; i++) {
            futures.add(executor.submit(new CopyPartFile(file, i, partSize, buffSize)));
        }

        for (Future<Boolean> f : futures)
            f.get();
        executor.shutdown();

        System.out.println("Splitting end.");
        System.out.println("Used " + countThreads + " threads");
        System.out.println("Used buffer of " + buffSize + " bytes size");
        System.out.println("Created " + parts + " parts");

        return true;
    }
}
