package ru.georgeee.stardict.example;

import ru.georgeee.stardict.Stardict;
import ru.georgeee.stardict.Stardict.WordPosition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Map;

public class UsageExample {
    private final Stardict stardict;

    public UsageExample(Stardict stardict) {
        this.stardict = stardict;
    }

    public static void main(String[] args) throws IOException {
        ru.georgeee.stardict.example.UsageExample usageExample = new ru.georgeee.stardict.example.UsageExample(Stardict.createRAM(Paths.get(args[0]), Paths.get(args[1])));
//        usageExample.showWords();
        usageExample.testByConsole();
    }

    public void testByConsole() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String s;
            while ((s = br.readLine()) != null && !s.isEmpty()) {
                WordPosition res = stardict.getWords().get(s);
                System.out.println(s + ":" + (res != null ? res.getEntry() : "<not found>"));
                if (res != null) {
                    System.out.println(res.getTranslations());
                }
            }
        }
    }

    public void showWords() {
        int i = 0;
        for (Map.Entry<String, WordPosition> en : stardict.getWords().entrySet()) {
            System.out.println(en.getKey() + " :" + en.getValue().getEntry());
        }
    }
}
