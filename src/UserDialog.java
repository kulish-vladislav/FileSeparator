import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class UserDialog {

    private Scanner scanner = new Scanner(System.in);

    public void menu() {
        System.out.println("Hello user");

        while (true) {
            System.out.println();
            System.out.println("1. Start program");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            switch (scanner.nextLine()) {
                case "1":
                    start();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Bad input. Please try again.");
            }
        }
    }

    private void start() {
        Mode mode = getMode();
        long workTime = 0;
        try {
            switch (mode) {
                case Separate:
                    workTime = workSeparate();
                    break;
                case Join:
                    workTime = workJoin();
                    break;
                case ForkSeparate:
                    workTime = workForkSeparate();
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        System.out.println("Working end. Time work = " + workTime);
        System.out.println("Press enter to continue...");
        scanner.nextLine();
    }

    private Mode getMode() {
        while (true) {
            System.out.print("Choice working mode(0 - separate, 1 - fork separate, 2 - join): ");
            switch (scanner.nextLine()) {
                case "0":
                    return Mode.Separate;
                case "1":
                    return Mode.ForkSeparate;
                case "2":
                    return Mode.Join;
                default:
                    System.out.println("Wrong.");
            }
        }
    }

    private String getFilePath(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }

    private long getPartSize(String msg) {
        System.out.print(msg);
        try {
            long size = scanner.nextLong(10);
            if (size <= 0)
//                throw new Exception("Threads count must be positive number.");
                throw new Exception();
            return size;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("Use default part size");
        }
        return -1;
    }

    private int getPositiveInteger(String msg) {
        System.out.print(msg);
        try {
            int number = scanner.nextInt(10);
            if (number <= 0)
//                throw new Exception("Threads count must be positive number.");
                throw new Exception();
            return number;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("Use default value");
        }
        return -1;
    }

    private long workSeparate() throws Exception {
        String filePath = getFilePath("Enter path to file: ");
        int countThreads = getPositiveInteger("Enter count threads('-1' -- default): ");
        long sizePart = getPartSize("Enter size of part('-1' -- default): ");
        int buffSize = getPositiveInteger("Enter buffer size('-1' -- default): ");

        Separable separator = new Separator(filePath, countThreads, sizePart, buffSize);
        System.out.println("Starting separate");
        long start = System.currentTimeMillis();
        separator.split();
        long end = System.currentTimeMillis();
        return end - start;
    }

    private long workJoin() throws IOException, InterruptedException, ExecutionException {
        String folder = getFilePath("Enter path to folder: ");
        String fileName = getFilePath("Enter 'primary' file name(without '_??'): ");
        int countThreads = getPositiveInteger("Enter count threads('-1' -- default): ");
        int buffSize = getPositiveInteger("Enter buffer size('-1' -- default): ");

        Joiner joiner = new Joiner(folder, fileName, countThreads, buffSize);
        System.out.println("Starting join");
        long start = System.currentTimeMillis();
        joiner.join();
        long end = System.currentTimeMillis();
        return end - start;
    }

    private long workForkSeparate() throws Exception {
        String filePath = getFilePath("Enter path to file: ");
        int countThreads = getPositiveInteger("Enter count threads('-1' -- default): ");
        long sizePart = getPartSize("Enter max size of part('-1' -- default): ");
        int buffSize = getPositiveInteger("Enter buffer size('-1' -- default): ");

        Separable separator = new ForkSeparator(filePath, countThreads, sizePart, buffSize);

        System.out.println("Starting separat");
        long start = System.currentTimeMillis();
        separator.split();
        long end = System.currentTimeMillis();
        return end - start;
    }

    private enum Mode {Separate, ForkSeparate, Join}


}
