import java.io.*;
import java.util.concurrent.Callable;

public class CopyPartFile implements Callable<Boolean> {

    int partNum;
    File file;
    long partSize;
    int buffSize;

    public CopyPartFile(File file, int partNum, long partSize, int buffSize) {
        this.file = file;
        this.partNum = partNum;
        this.partSize = partSize;
        this.buffSize = buffSize;
    }

    @Override
    public Boolean call() throws Exception {
        long pos = partNum * partSize;
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileOutputStream fos = new FileOutputStream(file.getCanonicalPath() + "_" + partNum);
        raf.seek(pos);

        byte[] bytes = new byte[buffSize];

        long sizeToRead = partSize;
        if ((pos + partSize) > file.length())
            sizeToRead = file.length() - pos;

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

        return true;
    }
}
