import java.io.*;
import java.util.concurrent.Callable;


public class JoinerCopy implements Callable<Boolean> {

    private File result;
    private File in;
    private long pos; // start position to write


    private int buffSize;

    public JoinerCopy(File result, long pos, File in, int buffSize) {
        this.result = result;
        this.pos = pos;
        this.in = in;
        this.buffSize = buffSize;
    }

    private void copy() throws IOException {

        FileInputStream fis = new FileInputStream(in);
        RandomAccessFile raf = new RandomAccessFile(result, "rw");

        byte[] bytes = new byte[buffSize];

        while (fis.available() > 0) {
            int read = fis.read(bytes);
            raf.seek(pos);
            raf.write(bytes, 0, read);
        }
        raf.close();
        fis.close();

    }

    @Override
    public Boolean call() throws Exception {
        copy();
        return null;
    }
}
