package com.streamliners.karobarqr.models;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
public class EncodedData implements Serializable {
    public static List<String> stringList = new ArrayList<>();
    public static List<String> encoHash = new ArrayList<>();

    public static List<String> returnList()  {
        return encoHash;
    }
    public static void encodeIt() {
        for (String a:stringList) {
            String encrypted = encodedReturn(a);
            encoHash.add(encrypted);
        }
    }

    public static String encodedReturn(String a) {
        return Base62.base62Encode(a.getBytes(StandardCharsets.UTF_8));
    }

    public static void addToList(long a) {
        stringList.add(String.valueOf(a));
    }
}

