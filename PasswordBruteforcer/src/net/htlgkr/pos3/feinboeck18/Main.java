package net.htlgkr.pos3.feinboeck18;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    private void run() {

    }

    private String readInFromFile(String path) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            return Files.lines(new File(path).toPath())
                    .map(stringBuilder::append)
                    .toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class Printer {
        public static void printTimeItTookToBreach(long startTime, String password) {
            System.out.println("The password is " + password + " and it took " +
                    TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) + "ms to breach");
        }
    }
}
