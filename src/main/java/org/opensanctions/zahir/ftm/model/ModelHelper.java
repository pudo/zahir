package org.opensanctions.zahir.ftm.model;

import java.security.MessageDigest;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public class ModelHelper {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss");
    
    public static List<String> getJsonStringArray(JsonNode node, String key) {
        List<String> strings = new ArrayList<>();
        if (node == null || !node.has(key)) {
            return strings;
        }
        JsonNode value = node.get(key);
        if (value.isArray()) {
            for (JsonNode element : value) {
                strings.add(element.asText());
            }
        }
        return strings;
    }

    public static Set<String> getJsonStringSet(JsonNode node, String key) {
        Set<String> strings = new HashSet<>();
        strings.addAll(getJsonStringArray(node, key));
        return strings;
    }

    public static Map<String, String> getJsonStringMap(JsonNode node, String key) {
        Map<String, String> strings = new HashMap<>();
        if (node == null || !node.has(key)) {
            return strings;
        }
        JsonNode value = node.get(key);
        if (value.isObject()) {
            Iterator<String> it = value.fieldNames();
            while (it.hasNext()) {
                String k = it.next();
                strings.put(k, value.get(k).asText());
            }
        }
        return strings;
    }
    
    public static String hexDigest(MessageDigest md) {
        StringBuilder sb = new StringBuilder();
        for (byte b : md.digest()) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String toTimeStamp(Instant instant) {
        return DATE_FORMAT.format(instant);
    }

    public static String toTimeStamp(long epochSeconds) {
        return DATE_FORMAT.format(Instant.ofEpochSecond(epochSeconds));
    }

    public static long fromTimeStamp(String timestamp) {
        return Instant.from(DATE_FORMAT.parse(timestamp)).getEpochSecond();
    }
}
