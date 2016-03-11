package ru.georgeee.stardict;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class RAMDictProvider implements DictProvider {
    private final byte [] bytes;

    public RAMDictProvider(Path dictPath) throws IOException {
        bytes = Files.readAllBytes(dictPath);
    }

    @Override
    public String getWordEntry(int start, int len) throws StarDictException {
        return new String(bytes, start, len);
    }
}
