package com.scangarella.pipe.util;

import com.scangarella.pipe.stereotype.Pipe;

/**
 * Utility pipe to transform a byte array to a string.
 */
public class ByteArrayToStringPipe extends Pipe<byte[], String> {

    /**
     * Transforms a series of bytes into a string.
     * @param bytes the bytes to be transformed.
     * @return the resulting string.
     */
    @Override
    public String ingest(byte[] bytes) {
        return new String(bytes);
    }
}
