package io.github.coderodde.compressor.app;

import java.util.Random;

public class TestUtils {
    
    private static final int SEED = 13;
    private static final int MAXIMUM_BYTE_ARRAY_LENGTH = 2_000;
    private static final Random RANDOM = new Random(SEED);
    
    private TestUtils() {
        
    }
    
    public static final byte[] getRawData() {
        final int length = 1 + RANDOM.nextInt(MAXIMUM_BYTE_ARRAY_LENGTH);
        final byte[] rawData = new byte[length];
        RANDOM.nextBytes(rawData);
        return rawData;
    }
}
