package net.htlgkr.pos3.feinboeck18;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    public void run() {
        String pathAsString = "numbers/numbers4";
        Path path = new File(pathAsString).toPath();
        int subArraySize;
        List<Integer> numbers;

        subArraySize = getSubarraySize(path);
        numbers = getAllOtherNumbers(path);

        Splitter splitter = new Splitter(subArraySize, numbers);
        splitter.splitNumbers(5);
    }

    private int getSubarraySize(Path path) {
        try {
            return Integer.parseInt(Files.lines(path).findFirst().orElse("0"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private List<Integer> getAllOtherNumbers(Path path) {
        List<Integer> numberCollector = new ArrayList<>();
        try {
            Files.lines(path)
                    .skip(1)
                    .forEach(line -> Arrays.stream(line.split(" "))
                            .forEach(numberAsString -> numberCollector.add(Integer.parseInt(numberAsString))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numberCollector;
    }
}