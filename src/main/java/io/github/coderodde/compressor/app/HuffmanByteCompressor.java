package io.github.coderodde.compressor.app;

import java.nio.ByteBuffer;
import java.util.Map;
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
     * Specifies how many bytes to use in order to communicate the size of the
     * Huffman code.
     */
    static final int BYTES_PER_CODE_SIZE = 4;
    
    /**
     * Specifies how many bytes to use in order to communicate the actual length
     * (in bytes) of the input byte array.
     */
    static final int BYTES_PER_RAW_DATA_LENGTH = 4;
    
    /**
     * Specifies how many bytes to reserve for describing the byte being 
     * encoded. 
     */
    static final int BYTES_PER_BYTE_DESCRIPTOR = 1;
    
    /**
     * Specifies how many bytes to reserve for signalling the codeword length.
     */
    static final int BYTES_PER_CODEWORD_LENGTH = 1;
    
    /**
     * Specifies how many bytes to use for the codeword.
     */
    static final int BYTES_PER_CODEWORD_MAX = 4;
    
    /**
     * Compresses the {@code rawData} {@code byte}-array using the input
     * {@code weightDistribution}.
     * 
     * @param weightDistribution the weight distribution to use in compressing.
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
        
        final WeightDistribution<Byte> byteWeightDistribution =
                ByteWeightDistributionBuilder
                        .buildByteWeightDistribution(rawData);
               
        final HuffmanCodeTable<Byte> codeTable = 
                HuffmanCodeBuilder.buildCode(byteWeightDistribution);
        
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
