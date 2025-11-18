package io.github.coderodde.compressor.app;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class ByteArrayCompressedDataReaderTest {
    
    private static final int STRESS_TEST_ITERATIONS = 50;
    
    private static final byte[] COMPRESSED_DATA = new byte[10_000];
    
    @Test
    public void readSmallTest() {
        final byte[] rawData = { 45, 46, 47, 47, 46, 47  };
        
        final WeightDistribution<Byte> wd =
                ByteWeightDistributionBuilder
                        .buildByteWeightDistribution(rawData);
        
        final HuffmanCodeTable<Byte> codeTable = 
                HuffmanCodeBuilder.buildCode(wd);
        
        final ByteArrayCompressedDataWriter writer = 
                new ByteArrayCompressedDataWriter(COMPRESSED_DATA, 
                                                  rawData,
                                                  0,
                                                  codeTable);
        writer.write();
        
        final HuffmanDecoderTree<Byte> decoderTree = 
                new HuffmanDecoderTree<>(codeTable);
        
        final byte[] resultRawData = new byte[rawData.length];
        
        final ByteArrayCompressedDataReader reader = 
                new ByteArrayCompressedDataReader(resultRawData, 
                                                  COMPRESSED_DATA, 
                                                  0,
                                                  decoderTree);
        reader.read();
        assertTrue(Arrays.equals(rawData, resultRawData));
    }
    
//    @Test
    public void readStressTest() {
        for (int i = 0; i < STRESS_TEST_ITERATIONS; ++i) {
            Arrays.fill(COMPRESSED_DATA, (byte) 0);
            
            final byte[] rawData = TestUtils.getRawData();
            
            // Write the compressed data:
            final WeightDistribution<Byte> wd = 
                    ByteWeightDistributionBuilder
                            .buildByteWeightDistribution(rawData);
            
            final HuffmanCodeTable<Byte> codeTable = 
                    HuffmanCodeBuilder.buildCode(wd);
            
            final ByteArrayCompressedDataWriter writer = 
                    new ByteArrayCompressedDataWriter(COMPRESSED_DATA,
                                                      rawData, 
                                                      0,
                                                      codeTable);
            
            writer.write();
            
            // Read the source data:
            final HuffmanDecoderTree<Byte> decoderTree = 
                    new HuffmanDecoderTree<>(codeTable);
            
            final byte[] resultRawData = new byte[rawData.length];
            
            final ByteArrayCompressedDataReader reader = 
                    new ByteArrayCompressedDataReader(resultRawData, 
                                                      COMPRESSED_DATA, 
                                                      0, 
                                                      decoderTree);
            
            reader.read();
            
            assertTrue(Arrays.equals(rawData, resultRawData));
        }
    }
}
