package net.htlgkr.pos3.feinboeck18;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class Splitter {
    private final int subArrSize;
    private final List<Integer> numbersToSplit;

    public Splitter(int subArrSize, List<Integer> numbersToSplit) {
        this.subArrSize = subArrSize;
        this.numbersToSplit = numbersToSplit;
    }

    private static int applyAsInt(Future<Integer> returnedInt) {
        try {
            return returnedInt.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void splitNumbers(int threads) {
        ExecutorService splitter = Executors.newFixedThreadPool(threads);
        int max = 0;
        int startWith = 0;
        int spaceBetweenThreads = (numbersToSplit.size() / threads) - 1;
        List<Callable<Integer>> callables = new ArrayList<>();

        for (int i = 1; i <= threads; i++) {
            if(i != threads) {
                callables.add(new MaximumSearcher(numbersToSplit, subArrSize, startWith, startWith + spaceBetweenThreads));
                startWith += spaceBetweenThreads + 1;
            } else
                callables.add(new MaximumSearcher(numbersToSplit, subArrSize, startWith, numbersToSplit.size() - 1));
        }

        try {
            max = splitter.invokeAll(callables).stream()
                    .mapToInt(Splitter::applyAsInt)
                    .max()
                    .orElse(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            splitter.shutdown();
        }

        System.out.println(max);
    }

    private static class MaximumSearcher implements Callable<Integer> {
        private final List<Integer> numbersToSplit;
        private final int subArrSize;
        private int currentValue;
        private final int endValue;

        public MaximumSearcher(List<Integer> numbersToSplit, int subArrSize, int starterValue, int endValue) {
            this.numbersToSplit = numbersToSplit;
            this.subArrSize = subArrSize;
            this.currentValue = starterValue;
            this.endValue = endValue;
        }

        @Override
        public Integer call() {
            int max = 0;
            while(currentValue <= endValue) {
                if(currentValue + subArrSize <= numbersToSplit.size()) {
                    Set<Integer> setOfNumbers = new HashSet<>();
                    for (int i = currentValue; i < currentValue + subArrSize; i++)
                        setOfNumbers.add(numbersToSplit.get(i));
                    if(setOfNumbers.size() > max)
                        max = setOfNumbers.size();
                } else
                    break;
                currentValue++;
            }
            return max;
        }
    }
}
