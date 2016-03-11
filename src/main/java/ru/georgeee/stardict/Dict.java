package ru.georgeee.stardict;

interface Dict {
    String getWordEntry(int start, int len) throws StarDictException;
}
