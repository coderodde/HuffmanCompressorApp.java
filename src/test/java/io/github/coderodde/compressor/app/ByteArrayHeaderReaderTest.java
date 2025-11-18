package io.github.coderodde.compressor.app;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public final class ByteArrayHeaderReaderTest {   
    
    private static final int STRESS_TEST_ITERATIONS = 50;
    
    private static final byte[] COMPRESSED_DATA = new byte[10_000];
    
    @Before
    public void clearCompressedData() {
        Arrays.fill(COMPRESSED_DATA, (byte) 0);
    }
    
    @Test
    public void smallTest() {
        final byte[] rawData = { 88, 40, 48 };
        final WeightDistribution<Byte> wd = 
                ByteWeightDistributionBuilder
                        .buildByteWeightDistribution(rawData);
        
        final HuffmanCodeTable<Byte> expectedCodeTable = 
                HuffmanCodeBuilder.buildCode(wd);
        
        final ByteArrayHeaderWriter writer = new ByteArrayHeaderWriter(rawData.length,
                                                             COMPRESSED_DATA,
                                                             expectedCodeTable);
        
        writer.write();
        
        final ByteArrayHeaderReader reader = 
                new ByteArrayHeaderReader(COMPRESSED_DATA);
        
        final int resultRawDataLength = reader.getRawDataLength();
        final HuffmanCodeTable<Byte> resultCodeTable = reader.getCodeTable();
        
        assertEquals(rawData.length, resultRawDataLength);
        assertEquals(expectedCodeTable, resultCodeTable);
    }
    
    @Test
    public void stressTest() {
        for (int i = 0; i < STRESS_TEST_ITERATIONS; ++i) {
            // Clear the output buffer in order to get rid of junk:
            Arrays.fill(COMPRESSED_DATA, (byte) 0);
            
            final byte[] rawData = TestUtils.getRawData();
            
            final WeightDistribution<Byte> weightDistribution =
                    ByteWeightDistributionBuilder
                            .buildByteWeightDistribution(rawData);
            
            final HuffmanCodeTable<Byte> expectedCodeTable = 
                    HuffmanCodeBuilder.buildCode(weightDistribution);
            
            final ByteArrayHeaderWriter writer = 
                    new ByteArrayHeaderWriter(rawData.length,
                                         COMPRESSED_DATA, 
                                         expectedCodeTable);
            
            writer.write();
            
            final ByteArrayHeaderReader reader = 
                    new ByteArrayHeaderReader(COMPRESSED_DATA);
            
            final int rawDataLength = reader.getRawDataLength();
            final HuffmanCodeTable<Byte> readCodeTable = reader.getCodeTable();
            
            assertEquals(rawData.length, rawDataLength);
            assertEquals(expectedCodeTable, readCodeTable);
        }
    }
}
