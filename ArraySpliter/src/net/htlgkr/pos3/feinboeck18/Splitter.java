package net.htlgkr.pos3.feinboeck18;

import java.util.ArrayList;
import java.util.List;

public class Splitter {
    public static List<int[]> splitIntoSubArrays(int subArrSize, int[] inputNumbs) {
        List<int[]> tempSubArrays = new ArrayList<>();
        if(inputNumbs.length == subArrSize) {
            tempSubArrays.add(inputNumbs);
            return tempSubArrays;
        }
        
    }
}
