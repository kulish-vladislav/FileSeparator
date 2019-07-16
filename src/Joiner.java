import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Joiner {

    private String pathToFolder;
    private String fileName;

    private int countThreads = 4;
    private int buffSize = 1000;
    private File dir; // master file (without part number)
    private List<File> fileParts;
    private long pos = 0;

    public Joiner(String pathToFolder, String fileName) throws IOException {
        this(pathToFolder, fileName, -1, -1);
    }

    public Joiner(String pathToFolder, String fileName, int countThreads, int buffSize) throws IOException {
        this.pathToFolder = pathToFolder;
        this.fileName = fileName;

        dir = new File(pathToFolder);
        if (!dir.isDirectory())
            throw new FileNotFoundException(dir.getCanonicalPath() + " not found");
        countParts();

        if (countThreads > 0)
            this.countThreads = countThreads;
        if (buffSize > 0)
            this.buffSize = buffSize;
    }

    private void countParts() {
        fileParts = new ArrayList<>();
        String[] filesInDir = dir.list();
        for (String file : filesInDir) {
            int tmp = file.lastIndexOf('_');
            if (tmp == -1)
                continue;
            if (file.substring(0, tmp).equals(fileName))
                fileParts.add(new File(dir, file));
        }
        System.out.println("Count parts = " + fileParts.size());
    }

    public boolean join() throws IOException, ExecutionException, InterruptedException {

        File resFile = new File(dir, "res_" + fileName);
        if (resFile.exists())
            resFile.delete();
        if (!resFile.createNewFile())
            throw new IOException("Cannot create result file.");

        File file;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < fileParts.size(); i++) {
            file = fileParts.get(i);
            futures.add(executorService.submit(new JoinerCopy(resFile, pos, file, buffSize)));
//            executorService.submit(new JoinerCopy(resFile, pos, file, buffSize)).get();
            pos += file.length();
            System.out.println("submit " + i);
        }

        for (Future<Boolean> f : futures)
            f.get();
        executorService.shutdown();

        System.out.println("Joining end.");
        System.out.println("Used " + countThreads + " threads");
        System.out.println("Used buffer of " + buffSize + " bytes size");
        System.out.println("Create file " + resFile.getName() + " sizeof " + resFile.length() + " bytes");

        return true;
    }


}
