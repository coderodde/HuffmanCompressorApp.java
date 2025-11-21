package io.github.coderodde.compressor.app;

import java.util.Objects;

/**
 * This class implements a method for compressing byte-wise files via Huffman-
 * coding.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.0.0 (Nov 14, 2025)
 * @since 1.0.0 (Nov 14, 2025)
 */
public final class HuffmanByteCompressor {
    
    /**
     * Compresses the {@code rawData} {@code byte}-array using the input
     * {@code weightDistribution}.
     * 
     * @param rawData            the raw data to compress.
     * 
     * @return the full binary {@code byte}-array containing all the data needed
     *         to decompress the compressed file.
     */
    public static byte[] compress(final byte[] rawData) {
            
        Objects.requireNonNull(rawData);
        
        if (rawData.length == 0) {
            throw new IllegalArgumentException("The input byte array is empty");
        }
        
        final ByteFrequencyDistribution byteWeightDistribution =
                ByteWeightDistributionBuilder
                        .buildByteWeightDistribution(rawData);
               
        final ByteHuffmanCodeTable codeTable = 
                ByteHuffmanCodeTableBuilder.buildCode(byteWeightDistribution);
        
        final int countNumberOfBytesInCodeHeader = 
                Utils.countBytesInCodeHeader(codeTable.size());
        
        final long countNumberOfBytesInRawData = 
                Utils.countBitsInRawData(codeTable, 
                                         rawData);
        
        final byte[] outputData = 
                new byte[(int)(countNumberOfBytesInCodeHeader + 
                               countNumberOfBytesInRawData)];
        
        final ByteArrayHeaderWriter headerWriter = 
                new ByteArrayHeaderWriter(rawData.length, 
                                          outputData,
                                          codeTable);
        
        headerWriter.write();
        
        final long startingDataBitIndex = headerWriter.getDataStartBitIndex();
        
        final ByteArrayCompressedDataWriter dataWriter = 
                new ByteArrayCompressedDataWriter(outputData,
                                                  rawData, 
                                                  startingDataBitIndex, 
                                                  codeTable);
        dataWriter.write();
        
        return outputData;
    }
}
