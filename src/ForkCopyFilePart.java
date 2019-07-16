import java.io.*;
import java.util.concurrent.RecursiveAction;

/**
 * Created by student on 25.02.2017.
 */
public class ForkCopyFilePart extends RecursiveAction {

    long partSize; // max size of part
    File file;
    long start;
    long end;
    int buffSize;

    public ForkCopyFilePart(File file, long start, long end, long partSize, int buffSize) {
        this.file = file;
        this.start = start;
        this.end = end;
        this.partSize = partSize;
        this.buffSize = buffSize;
    }

    private void copy() throws IOException {
        int pathNum = 0;
        if (start != 0)
            pathNum = (int) (start / partSize) + 1;
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileOutputStream fos = new FileOutputStream(file.getCanonicalPath() + "_" + pathNum);

        int sizeToRead = (int) (end - start);
        raf.seek(start);


        byte[] bytes = new byte[buffSize];

        int countRead = (int) (sizeToRead / buffSize);
        int sizeApendix = (int) (sizeToRead % buffSize);

        for (int i = 0; i < countRead; i++) {
            raf.read(bytes);
            fos.write(bytes);
            fos.flush();
        }

        raf.read(bytes, 0, sizeApendix);
        fos.write(bytes, 0, sizeApendix);
        fos.flush();
        fos.close();
        raf.close();
    }

    @Override
    protected void compute() {
        if ((end - start) < partSize) {
            try {
                copy();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {

            long mid = (start + end) / 2;

            invokeAll(new ForkCopyFilePart(file, start, mid, partSize, buffSize),
                    new ForkCopyFilePart(file, mid, end, partSize, buffSize));
        }

    }
}
