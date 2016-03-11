package ru.georgeee.stardict;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stardict {

    private static final int IDX_BUFFER_SIZE = 1024 * 1024;
    private final Map<String, WordPosition> words = new HashMap<>();
    private final DictProvider dictProvider;

    private Stardict(Path idxPath, DictProvider dictProvider) throws IOException {
        this.dictProvider = dictProvider;
        loadIndexFile(idxPath);
    }

    public static Stardict createRAF(Path idxPath, Path dictPath) throws IOException {
        return new Stardict(idxPath, new RAFDictProvider(dictPath.toFile()));
    }

    public static Stardict createRAM(Path idxPath, Path dictPath) throws IOException {
        return new Stardict(idxPath, new RAMDictProvider(dictPath));
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

    private static final Pattern DTRN_PATTERN = Pattern.compile("<dtrn>(.*)</dtrn>");

    public class WordPosition {

        private final int start;
        private final int length;

        public WordPosition(int s, int l) {
            this.start = s;
            this.length = l;
        }

        public String getEntry() throws StarDictException {
            return dictProvider.getWordEntry(start, length);
        }

        public List<List<String>> getTranslations() {
            String entry = getEntry();
            Matcher matcher = DTRN_PATTERN.matcher(entry);
            List<List<String>> result = new ArrayList<>();
            while (matcher.find()) {
                List<String> subResult = new ArrayList<>();
                String dtrn = matcher.group(1);
                dtrn = dtrn.replaceAll("<.*>", "");
                String[] parts = dtrn.split("[,;]");
                for (String s : parts) {
                    s = s.trim();
                    if (s.isEmpty()) {
                        continue;
                    }
                    if (s.contains("(-)")) {
                        String[] subParts = s.split("\\(-\\)", 2);
                        subResult.add(subParts[1]);
                        subResult.add(subParts[0] + subParts[1]);
                    } else {
                        subResult.add(s);
                    }
                }
                result.add(subResult);
            }
            return result;
        }
    }
}
