package net.htlgkr.pos3.feinboeck18;

import net.htlgkr.pos3.feinboeck18.crackers.PWCracker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    private Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    private void run() {
        String path;
        String passwordCrypto;
        PWCracker cracker;
        long startTime;
        String clearTextPW;

        while(true) {
            System.out.print("Which Level PW should be cracked?");
            try {
                int passwordLevel = Integer.parseInt(scanner.nextLine());
                path = "passwords/password" + passwordLevel;
                //Reading in PW
                passwordCrypto = readInPassword(path, passwordLevel);
                cracker = new PWCracker(passwordCrypto);
                //TIMER START
                startTime = System.nanoTime();
                clearTextPW = cracker.getPassword(passwordLevel);
                //TIMER END
                printTimeItTookToBreach(startTime, clearTextPW);
            } catch (NumberFormatException nfe) {
                System.exit(0);
            } catch (Exception e) {
                System.out.println("Could not convert your PW-Level");
            }
        }
    }

    private String readInPassword(String path, int pwLevel) {
        try {
            return Files.lines(new File(path).toPath())
                    .reduce((string, string2) -> pwLevel != 3 ? string + string2 : string + "\n" + string2
                    ).orElse("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void printTimeItTookToBreach(long startTime, String password) {
        System.out.println("The password is " + password + " and it took " +
                TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) + "ms to breach");
    }
}
