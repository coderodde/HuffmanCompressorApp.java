package io.github.coderodde.compressor.app;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class HuffmanDecodingTreeTest {
    
    @Test
    public void constructAndDecode() {
        final byte[] sourceData = { 45, 46, 47, 47, 46, 47 };
        final byte[] targetData = new byte[10];
        
        final WeightDistribution<Byte> wd = 
                ByteWeightDistributionBuilder
                        .buildByteWeightDistribution(sourceData);
        
        final HuffmanCodeTable<Byte> codeTable = 
                HuffmanCodeBuilder.buildCode(wd);
        
        final ByteArrayCompressedDataWriter writer = 
                new ByteArrayCompressedDataWriter(targetData,
                                                  sourceData,
                                                  0,
                                                  codeTable);
        
        writer.write();
        
        final HuffmanDecodingTree<Byte> decoderTree = 
                new HuffmanDecodingTree<>(codeTable);
        
        final byte[] decompressedData = new byte[sourceData.length];
        
        final ByteArrayCompressedDataReader reader = 
                new ByteArrayCompressedDataReader(decompressedData, 
                                                  targetData, 
                                                  0,
                                                  decoderTree);
        
        reader.read();
        
        assertTrue(Arrays.equals(sourceData, decompressedData));
    }
}
