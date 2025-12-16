package com.example.newnomads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Env {
    private static final Map<String, String> env = new HashMap<>();

    static {
        try (BufferedReader br = new BufferedReader(new FileReader("C:/Users/upsil/OneDrive/Desktop/java/NewNomadsJavaFX/.env"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) env.put(parts[0].trim(), parts[1].trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return env.get(key);
    }
}
