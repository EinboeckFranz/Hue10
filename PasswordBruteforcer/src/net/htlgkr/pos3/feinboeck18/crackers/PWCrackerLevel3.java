package net.htlgkr.pos3.feinboeck18.crackers;

import net.htlgkr.pos3.feinboeck18.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PWCrackerLevel3 {
    private final String encryptedPW;

    public PWCrackerLevel3(String encryptedPW) {
        this.encryptedPW = encryptedPW;
    }

    public String getPassword() {
        List<String> mythicalCreaturesList = getCreatures();
        if(mythicalCreaturesList == null) return null;

        List<Callable<String>> callables = new ArrayList<>();
        int startIndex = 0;
        int toAdd = mythicalCreaturesList.size() / 20;

        for (int counter = 0; counter < 19; counter++) {
            callables.add(new CreaturesGuesser(mythicalCreaturesList, startIndex, startIndex + toAdd, encryptedPW));
            startIndex += toAdd + 1;
        }

        ExecutorService threadExecutor = Executors.newFixedThreadPool(callables.size());
        String password = "";
        try {
            password = threadExecutor.invokeAny(callables);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            threadExecutor.shutdown();
            CreaturesGuesser.runs = false;
        }
        return password;
    }

    private List<String> getCreatures() {
        Document listOfMythicalCreaturesHTMLDoc;
        try {
            listOfMythicalCreaturesHTMLDoc = Jsoup.connect("https://de.wikipedia.org/wiki/Liste_von_Fabelwesen").get();
            return listOfMythicalCreaturesHTMLDoc.select("ul li a").stream()
                    .map(creature -> creature.attr("title"))
                    .filter(creature -> !creature.equals(""))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
class CreaturesGuesser implements Callable<String> {
    private final List<String> listOfCreatures;
    private int currentIndex;
    private final int endIndex;
    private final String passwordToCrack;

    public static boolean runs;

    public CreaturesGuesser(List<String> mythicalCreaturesList, int startIndex, int endIndex, String encryptedPW) {
        this.listOfCreatures = mythicalCreaturesList;
        this.currentIndex = startIndex;
        this.endIndex = endIndex;
        this.passwordToCrack = encryptedPW;

        runs = true;
    }

    @Override
    public String call() {
        while(currentIndex <= endIndex && runs) {
            if(StringUtil.applySha256(listOfCreatures.get(currentIndex)).equals(passwordToCrack))
                return listOfCreatures.get(currentIndex);
            currentIndex++;
        }
        while(runs) {}
        return null;
    }
}