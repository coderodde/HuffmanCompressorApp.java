package io.github.coderodde.compressor.app;

import java.util.Objects;

/**
 * This class is responsible for writing the actual compressed data to a byte 
 * array. This class does not handle the compression header; it is handled by
 * {@link io.github.coderodde.compressor.app.ByteArrayHeaderWriter}.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.0.0 (Nov 17, 2025)
 * @since 1.0.0 (Nov 17, 2025)
 */
public final class ByteArrayCompressedDataWriter {

    /**
     * The target byte array to which all the compressed data will end up.
     */
    private final byte[] compressedOutputData;
    
    /**
     * The actual data to be compressed.
     */
    private final byte[] inputRawData;
    
    /**
     * The index of the first bit in the compressed data. We need this in order
     * to omit the compression header.
     */
    private final long startingBitIndex;
    
    /**
     * The actual (Huffman) code table.
     */
    private final HuffmanCodeTable<Byte> codeTable;
    
    /**
     * Constructs this writer.
     * 
     * @param compressedOutputData the compressed data byte array.
     * @param inputRawData         the input raw data byte array.
     * @param startingBitIndex     the starting bit index for the writing.
     * @param codeTable            the byte encoding table.
     */
    public ByteArrayCompressedDataWriter(
            final byte[] compressedOutputData,
            final byte[] inputRawData,
            final long startingBitIndex,
            final HuffmanCodeTable<Byte> codeTable) {
        
        this.compressedOutputData = 
                Objects.requireNonNull(
                        compressedOutputData,
                        "The output compressed data is null");
        
        this.inputRawData =
                Objects.requireNonNull(
                        inputRawData, 
                        "The input raw data is null");
        
        this.codeTable = 
                Objects.requireNonNull(
                        codeTable, 
                        "The input code table is null");
        
        this.startingBitIndex = startingBitIndex;
    }
    
    /**
     * Writes the entire compressed data of {@code inputRawData}.
     */
    public void write() {
        long currentBitIndex = startingBitIndex;
        
        for (final byte b : inputRawData) {
            final CodeWord codeword = codeTable.getCodeword(b).reverse();
            final int codewordLength = codeword.length();
            
            writeCodeWord(compressedOutputData,
                          currentBitIndex,
                          codeword);
            
            currentBitIndex += codewordLength;
        }
    }
    
    /**
     * Writes a single codeword to the compressed output data byte array.
     * 
     * @param compressedOutputData the compressed output data byte array.
     * @param currentBitIndex      the current bit index.
     * @param codeword             the codeword to write.
     */
    private static void writeCodeWord(final byte[] compressedOutputData,
                                      final long currentBitIndex,
                                      final CodeWord codeword) {
        
        int byteIndex = (int) (currentBitIndex / Byte.SIZE); 
        int bitIndex  = (int) (currentBitIndex % Byte.SIZE);
        
        final int codewordLength = codeword.length();
        
        for (int codewordBitIndex = 0;
                 codewordBitIndex < codewordLength;
                 codewordBitIndex++) {
            
            if (codeword.get(codewordBitIndex)) {
                setBit(compressedOutputData,
                       byteIndex,
                       bitIndex);
            }
            
            bitIndex++;
            
            if (bitIndex == Byte.SIZE) {
                bitIndex = 0;
                byteIndex++;
            }
        }
    }
    
    /**
     * Sets the {@code bitIndex}th bit in 
     * {@code compressedOutputData[byteIndex]}.
     * 
     * @param compressedOutputData the compressed output data byte array.
     * @param byteIndex            the target byte index.
     * @param bitIndex             the target bit index.
     */
    private static void setBit(final byte[] compressedOutputData,
                               final int byteIndex,
                               final int bitIndex) {
        
        final byte mask = (byte)(1 << bitIndex);
        compressedOutputData[byteIndex] |= mask;
    }
}
