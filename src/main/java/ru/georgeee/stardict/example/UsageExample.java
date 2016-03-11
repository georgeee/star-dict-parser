package ru.georgeee.stardict.example;

import ru.georgeee.stardict.StarDictException;
import ru.georgeee.stardict.StarDictParser;
import ru.georgeee.stardict.StarDictParser.WordPosition;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class UsageExample {
    private final StarDictParser parser;

    public UsageExample(StarDictParser parser) {
        this.parser = parser;
    }

    public static void main(String[] args) throws IOException, StarDictException {
        UsageExample usageExample = new UsageExample(StarDictParser.createRAM(Paths.get(args[1]), new File(args[0])));
//        usageExample.showWords();
        usageExample.testByConsole();
    }

    public void testByConsole() throws IOException, StarDictException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String s;
            while ((s = br.readLine()) != null && !s.isEmpty()) {
                int ind = s.indexOf(':');
                List<Map.Entry<String, WordPosition>> res = parser.searchWord(ind == -1 ? s : s.substring(0, ind));
                if(ind == -1) {
                    int i = 0;
                    for (Map.Entry<String, WordPosition> en : res) {
                        System.out.println(i++ + " : " + en.getKey());
                    }
                }else{
                    int i = Integer.parseInt(s.substring(ind + 1));
                    System.out.println(i + " : " + res.get(i).getKey());
                    System.out.println(res.get(i).getValue().getEntry());
                }
            }
        }
    }

    public void showWords() throws StarDictException {
        int i = 0;
        for (Map.Entry<String, WordPosition> en : parser.getWords().entrySet()) {
            System.out.println(en.getKey() + " :" + en.getValue().getEntry());
        }
    }
}
