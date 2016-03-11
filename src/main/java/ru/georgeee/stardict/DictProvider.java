package ru.georgeee.stardict;

interface DictProvider {
    String getWordEntry(int start, int len) throws StarDictException;
}
