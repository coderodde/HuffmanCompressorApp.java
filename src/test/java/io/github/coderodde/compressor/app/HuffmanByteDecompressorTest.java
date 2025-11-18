package io.github.coderodde.compressor.app;

import java.util.Arrays;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class HuffmanByteDecompressorTest {

    private static final int STRESS_TEST_ITERATIONS = 50;
    
    @Test
    public void decompressStressTest() {
        for (int i = 0; i < STRESS_TEST_ITERATIONS; ++i) {
            System.out.println("i = " + i);
            stressTest();
        }
    }
    
    private void stressTest() {
        final byte[] sourceData = Utils.getRawData();
        final byte[] compressedData = 
                HuffmanByteCompressor.compress(sourceData);
        
        final byte[] targetData = 
                HuffmanByteDecompressor.decompress(compressedData);
        
        assertTrue(Arrays.equals(sourceData, targetData));
    }
}
