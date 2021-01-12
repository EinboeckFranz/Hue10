package net.htlgkr.pos3.feinboeck18.crackers;

import net.htlgkr.pos3.feinboeck18.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PWCracker {
    private final String encryptedPW;

    private int passwordLevel;

    public PWCracker(String encryptedPW) {
        this.encryptedPW = encryptedPW;
    }

    public String getPassword(int passwordLevel) throws Exception {
        String breakPosition;
        List<Callable<String>> callables = new ArrayList<>();
        this.passwordLevel = passwordLevel;

        switch (passwordLevel) {
            case 0:
                for (String startValue = "aaaa"; startValue != null; startValue = breakPosition) {
                    breakPosition = getNextBreakValue(startValue);
                    callables.add(new Guesser(this, startValue, breakPosition, this.encryptedPW));
                }
            break;
            case 1:
                for (String startValue = "AAAAAA"; startValue != null; startValue = breakPosition) {
                    breakPosition = getNextBreakValue(startValue);
                    callables.add(new Guesser(this, startValue, breakPosition, this.encryptedPW));
                }
            break;
            case 2:
                for (String startValue = "00000"; startValue != null; startValue = breakPosition) {
                    breakPosition = getNextBreakValue(startValue);
                    callables.add(new Guesser(this, startValue, breakPosition, this.encryptedPW));
                }
            break;
            case 3:
                PWCrackerLevel3 pwCrackerLevel3 = new PWCrackerLevel3(this.encryptedPW);
                return pwCrackerLevel3.getPassword();
            default:
                System.out.println("Unknown Password Level please try again");
            break;
        }

        ExecutorService callablesExecutor = Executors.newFixedThreadPool(callables.size());
        try {
            return callablesExecutor.invokeAny(callables);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            callablesExecutor.shutdown();
            Guesser.threadIsRunning = false;
        }
        throw new Exception("Error while cracking this Password");
    }

    //Found this code on Stack Overflow
    private String getNextBreakValue(String currentStartValue) {
        char[] charsOfStartValue = currentStartValue.toCharArray();
        int intValueOfChar = charsOfStartValue[0];
        intValueOfChar = intValueOfChar + 1;

        if(this.passwordLevel == 1) {
            if(intValueOfChar==91)
                return null;
        } else {
            switch (intValueOfChar) {
                case 58:
                    intValueOfChar = 65;
                break;
                case 91:
                    intValueOfChar = 97;
                break;
                case 123:
                    return null;
            }
        }

        charsOfStartValue[0] = (char) intValueOfChar;
        return String.valueOf(charsOfStartValue);
    }

    //Also found this piece of Code on Stack Overflow
    public String getNextValueToCheck(String currentValue, int index) {
        StringBuilder currentBuilder = new StringBuilder();
        char[] currentValueAsCharArray = currentValue.toCharArray();
        int intValueOfChar = currentValueAsCharArray[index];
        intValueOfChar = intValueOfChar + 1;

        //LEVEL 1 Logic
        if (this.passwordLevel == 1) {
            if (intValueOfChar == 91) {
                if (index == 0)
                    return null;
                else {
                    currentValueAsCharArray[index] = 65;
                    return getNextValueToCheck(String.valueOf(currentValueAsCharArray), index - 1);
                }
            } else
                currentValueAsCharArray[index] = (char) intValueOfChar;

        } else {
            switch (intValueOfChar) {
                case 58:
                    currentValueAsCharArray[index] = 65;
                break;
                case 91:
                    currentValueAsCharArray[index] = 97;
                break;
                case 123:
                    if(index == 0) return null;
                    if(!(this.passwordLevel == 0))
                        currentValueAsCharArray[index] = 48;
                    else
                        currentValueAsCharArray[index] = 97;

                    for (char c :currentValueAsCharArray)
                        currentBuilder.append(c);
                    return getNextValueToCheck(currentBuilder.toString(), index-1);
                default:
                    currentValueAsCharArray[index] = (char) intValueOfChar;
                break;
            }
        }
        return String.valueOf(currentValueAsCharArray);
    }
}
class Guesser implements Callable<String> {
    private final PWCracker pwCracker;
    private String currentValue;
    private final String endString;
    private final String encryptedPW;

    public static boolean threadIsRunning;

    public Guesser(PWCracker pwCracker, String startString, String endString, String encryptedPW) {
        this.pwCracker = pwCracker;
        this.currentValue = startString;
        this.endString = endString;
        this.encryptedPW = encryptedPW;

        threadIsRunning = true;
    }

    @Override
    public String call() {
        while(!currentValue.equals(endString) && threadIsRunning) {
            if(StringUtil.applySha256(currentValue).equals(encryptedPW))
                return currentValue;
            currentValue = pwCracker.getNextValueToCheck(currentValue, currentValue.length()-1);
        }
        while(threadIsRunning) { }
        return "";
    }
}