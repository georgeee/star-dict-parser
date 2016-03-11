package ru.georgeee.stardict;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

public class StarDictParser {

    public static final int MAX_RESULT = 40;
    private static final Comparator<Map.Entry<String, WordPosition>> WORD_COMPARATOR = new Comparator<Map.Entry<String, WordPosition>>() {
        public int compare(Map.Entry<String, WordPosition> ea, Map.Entry<String, WordPosition> eb) {
            return ea.getKey().compareToIgnoreCase(eb.getKey());
        }
    };
    private static final int IDX_BUFFER_SIZE = 1024 * 1024;
    private final Map<String, WordPosition> words = new HashMap<>();
    private final Dict dict;

    private StarDictParser(Path idxPath, Dict dict) throws IOException {
        this.dict = dict;
        loadIndexFile(idxPath);
    }

    public static StarDictParser createRAF(Path idxPath, File dictFile) throws IOException {
        return new StarDictParser(idxPath, new RAFDict(dictFile));
    }

    public static StarDictParser createRAM(Path idxPath, File dictFile) throws IOException {
        return new StarDictParser(idxPath, new RAMDict(dictFile.toPath()));
    }

    public List<Map.Entry<String, WordPosition>> searchWord(String term) {
        List<Entry<String, WordPosition>> resa = new ArrayList<>();
        List<Entry<String, WordPosition>> resb = new ArrayList<>();

        int i;
        for (Map.Entry<String, WordPosition> en : words.entrySet()) {
            if (en.getKey() == null) {
                throw new IllegalStateException("Null word key");
            }
            i = en.getKey().toLowerCase().indexOf(term);
            if (i == 0) {
                resa.add(en);
            } else if (i > 0 && resb.size() < MAX_RESULT) {
                resb.add(en);
            }
            if (resa.size() > MAX_RESULT) {
                break;
            }
        }

        Collections.sort(resa, WORD_COMPARATOR);
        Collections.sort(resb, WORD_COMPARATOR);

        if (resa.size() < MAX_RESULT) {
            int need = MAX_RESULT - resa.size();
            if (need > resb.size()) {
                need = resb.size();
            }
            resa.addAll(resb.subList(0, need));
        }
        return resa;
    }

    private void loadIndexFile(Path idxPath) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(idxPath.toFile()))) {
            byte[] buf = new byte[IDX_BUFFER_SIZE];
            int c, i = 0;
            do {
                c = bis.read();
                if (c != -1) {
                    if (c == 0) {
                        String word = new String(buf, 0, i, "UTF-8");
                        for (i = 0; i < 8; ++i) {
                            c = bis.read();
                            if (c == -1) {
                                throw new IllegalStateException("Unexpected end of index file");
                            } else {
                                buf[i] = (byte) c;
                            }
                        }
                        ByteBuffer byteBuffer = ByteBuffer.wrap(buf);
                        byteBuffer.order(ByteOrder.BIG_ENDIAN);
                        int start = byteBuffer.getInt();
                        int len = byteBuffer.getInt();
                        words.put(word, new WordPosition(start, len));
                        i = 0;
                    } else {
                        buf[i++] = (byte) c;
                    }
                } else if (i != 0) {
                    throw new IllegalStateException("Unexpected end of index file");
                }
            } while (c != -1);
        }
    }

    public Map<String, WordPosition> getWords() {
        return words;
    }

    public class WordPosition {

        private final int start;
        private final int length;

        public WordPosition(int s, int l) {
            this.start = s;
            this.length = l;
        }

        public String getEntry() throws StarDictException {
            return dict.getWordEntry(start, length);
        }
    }
}
