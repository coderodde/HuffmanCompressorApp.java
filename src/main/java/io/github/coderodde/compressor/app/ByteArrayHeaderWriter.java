package io.github.coderodde.compressor.app;

import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_CODEWORD_MAX;
import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_CODE_SIZE;
import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_RAW_DATA_LENGTH;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.Objects;

/**
 * This class writes the file header to the compressed file.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.0.0 (Nov 16, 2025)
 * @since 1.0.0 (Nov 16, 2025)
 */
public final class ByteArrayHeaderWriter {

    /**
     * The minimum length of the raw data byte array.
     */
    private static final int MINIMUM_RAW_DATA_LENGTH = 1;
    
    /**
     * The length of the raw data in bytes.
     */
    private final int rawDataLength;
    
    /**
     * The output data containing the compressed file.
     */
    private final byte[] outputData;
    
    /**
     * The index of the bit in the compressed data byte array at which writing
     * compressed data must begin.
     */
    private long dataStartBitIndex;
    
    /**
     * The code table to write to the compressed file header.
     */
    private final HuffmanCodeTable<Byte> codeTable;
    
    public ByteArrayHeaderWriter(final int rawDataLength,
                            final byte[] outputData,
                            final HuffmanCodeTable<Byte> codeTable) {
        
        checkRawDataLength(rawDataLength);
        Objects.requireNonNull(outputData, "The output data array is null");
        Objects.requireNonNull(codeTable, "The input code table is null");
        checkCodeTable(codeTable);
        
        this.rawDataLength = rawDataLength;
        this.outputData    = outputData;
        this.codeTable     = codeTable;
    }
    
    public void write() {
        writeCodeSize();
        writeRawDataLength();
        writeCodeTable();
    }
    
    public long getDataStartBitIndex() {
        return dataStartBitIndex;
    }
    
    /**
     * Writes the code size to the very first 32-bit integer of the compressed
     * file.
     */
    private void writeCodeSize() {
        final byte[] codeSizeBytes = 
                ByteBuffer.allocate(BYTES_PER_CODE_SIZE)
                          .order(ByteOrder.LITTLE_ENDIAN)
                          .putInt(codeTable.size())
                          .array();
        
        System.arraycopy(codeSizeBytes, 
                         0, 
                         outputData, 
                         0, 
                         codeSizeBytes.length);
    }
    
    /**
     * Writes the length of the raw data file to the second 32-bit word in the 
     * compressed file.
     */
    private void writeRawDataLength() {
        final byte[] rawDataLengthBytes = 
                ByteBuffer.allocate(BYTES_PER_RAW_DATA_LENGTH)
                          .order(ByteOrder.LITTLE_ENDIAN)
                          .putInt(rawDataLength)
                          .array();
        
        System.arraycopy(rawDataLengthBytes,
                         0,
                         outputData, 
                         BYTES_PER_CODE_SIZE, 
                         rawDataLengthBytes.length);
    }
    
    /**
     * Writes the actual code table to the compressed file header to starting 
     * from the 8th byte.
     */
    private void writeCodeTable() {
        int currentByteIndex = BYTES_PER_CODE_SIZE + BYTES_PER_RAW_DATA_LENGTH;
        
        for (final Map.Entry<Byte, CodeWord> entry : codeTable) {
            outputData[currentByteIndex++] = entry.getKey();
            outputData[currentByteIndex++] = (byte) entry.getValue().length();
            
            final byte[] codewordBytes = entry.getValue().toByteArray();
            
            System.arraycopy(codewordBytes, 
                             0, 
                             outputData, 
                             currentByteIndex, 
                             codewordBytes.length);
            
            currentByteIndex += BYTES_PER_CODEWORD_MAX;
        }
        
        this.dataStartBitIndex = currentByteIndex;
    }
    
    private static void checkRawDataLength(final int rawDataLength) {
        if (rawDataLength < MINIMUM_RAW_DATA_LENGTH) {
            throw new TooShortRawDataLengthException(
                    String.format(
                            "The length of the raw data is too small: %d. " + 
                            "Must be at least %d.", 
                            rawDataLength, 
                            MINIMUM_RAW_DATA_LENGTH));
        }
    }
    
    private static void checkCodeTable(final HuffmanCodeTable<Byte> codeTable) {
        if (codeTable.isEmpty()) {
            throw new EmptyCodeTableException();
        }
    }
}
