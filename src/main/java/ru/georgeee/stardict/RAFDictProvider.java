package ru.georgeee.stardict;

import java.io.*;

class RAFDictProvider implements DictProvider, Closeable {
    private final RandomAccessFile randomAccessFile;

    public RAFDictProvider(File file) throws FileNotFoundException {
        this.randomAccessFile = new RandomAccessFile(file, "r");
    }

    @Override
    public String getWordEntry(int start, int len) throws ru.georgeee.stardict.StarDictException {
        try {
            byte[] buf = new byte[len];
            this.randomAccessFile.seek(start);
            int ir = this.randomAccessFile.read(buf);
            if (ir != len) {
                throw new ru.georgeee.stardict.StarDictException("Error occurred, not enought bytes read, wanting:" + len + ",got:" + ir);
            }
            return new String(buf, "utf-8");
        } catch (IOException e) {
            throw new StarDictException(e);
        }
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }
}
