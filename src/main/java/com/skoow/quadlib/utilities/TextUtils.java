package com.skoow.quadlib.utilities;

import org.apache.commons.text.similarity.CosineSimilarity;

import java.util.HashMap;
import java.util.Map;

public class TextUtils {
    public static float similarity(String a, String b) {
        CosineSimilarity cosine = new CosineSimilarity();
        Map<CharSequence, Integer> text1 = tokenizeAndCount(cleanText(a));
        Map<CharSequence, Integer> text2 = tokenizeAndCount(cleanText(b));
        return cosine.cosineSimilarity(text1,text2).floatValue();
    }
    private static String cleanText(String text) {
        return text.replaceAll("[^\\p{L}\\p{N}\\s]+", "").toLowerCase();
    }
    private static Map<CharSequence, Integer> tokenizeAndCount(String text) {
        Map<CharSequence, Integer> freqMap = new HashMap<>();
        for (String word : text.split("\\s+")) {
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }
        return freqMap;
    }
}
