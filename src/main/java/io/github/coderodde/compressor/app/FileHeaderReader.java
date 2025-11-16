package io.github.coderodde.compressor.app;

import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_CODEWORD_MAX;
import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_CODE_SIZE;
import java.nio.ByteBuffer;
import java.util.Objects;
import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_RAW_DATA_LENGTH;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * This class implements the reader returning the file header data such as the 
 * length of the raw data being compressed and its decoding Huffman tree.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.0.0 (Nov 16, 2025)
 * @since 1.0.0 (Nov 16, 2025)
 */
public final class FileHeaderReader {

    /**
     * The compressed data byte array containing the header.
     */
    private final byte[] compressedData;
    
    /**
     * We cache this in order users of this class can query the length of the 
     * raw data that would result from decompression.
     */
    private final int rawDataLength;
    
    /**
     * The code table being read.
     */
    private final HuffmanCodeTable<Byte> codeTable;
    
    public FileHeaderReader(final byte[] compressedData) {
        this.compressedData =
                Objects.requireNonNull(compressedData,
                                       "The input compressed data is null");
        
        final int codeTableSize = readCodeTableSize();
        this.rawDataLength = readRawDataLength();
        this.codeTable = readCodeTable(codeTableSize);
    }
    
    public int getRawDataLength() {
        return rawDataLength;
    }
    
    public HuffmanCodeTable<Byte> getCodeTable() {
        return codeTable;
    }
    
    private int readCodeTableSize() {
        final byte[] codeTableSizeBytes = new byte[BYTES_PER_CODE_SIZE];
        
        System.arraycopy(compressedData,
                         0,
                         codeTableSizeBytes, 
                         0, 
                         codeTableSizeBytes.length);
        
        final ByteBuffer byteBuffer = ByteBuffer.wrap(codeTableSizeBytes);
        return byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    private int readRawDataLength() {
        final byte[] rawDataLengthBytes = new byte[BYTES_PER_RAW_DATA_LENGTH];
        
        System.arraycopy(compressedData,
                         BYTES_PER_CODE_SIZE,
                         rawDataLengthBytes, 
                         0, 
                         rawDataLengthBytes.length);
        
        final ByteBuffer byteBuffer = ByteBuffer.wrap(rawDataLengthBytes);
        return byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    private HuffmanCodeTable<Byte> readCodeTable(final int codeTableSize) {
        final HuffmanCodeTable<Byte> codeTable = new HuffmanCodeTable<>();
        final int codeEntryLength = Utils.getCodeEntryLength();
        
        int byteCursor = BYTES_PER_CODE_SIZE + BYTES_PER_RAW_DATA_LENGTH;
        
        for (int codeIndex = 0; codeIndex < codeTableSize; ++codeIndex) {
            readCodeEntry(codeTable,
                          compressedData,
                          byteCursor);
            
            byteCursor += codeEntryLength;
        }
        
        return codeTable;
    }
    
    private static void readCodeEntry(final HuffmanCodeTable<Byte> codeTable,
                                      final byte[] compressedData,
                                      final int byteCursor) {
        final byte value  = compressedData[byteCursor];
        final byte length = compressedData[byteCursor + 1];
        final byte[] codeEntryData = 
                Arrays.copyOfRange(compressedData, 
                                   byteCursor + 2, 
                                   byteCursor + 2 + BYTES_PER_CODEWORD_MAX);
        
//        reverseBytes(codeEntryData);
        
        final CodeWord codeword = inferCodeWord(length, codeEntryData);
        
        codeTable.linkSymbolToCodeword(value, codeword);
    }
    
    private static CodeWord inferCodeWord(final int length,
                                          final byte[] codeData) {
        final int bits = ByteBuffer.wrap(codeData)
                                   .order(ByteOrder.LITTLE_ENDIAN)
                                   .getInt();
        
        final CodeWord codeword = new CodeWord(length);
        
        int mask = 1;
        
        for (int i = 0; i < length; ++i) {
            if ((bits & mask) != 0) {
                codeword.set(i);
            }
            
            mask <<= 1;
        }
        
        return codeword;
    }
    
//    private static void reverseBytes(final byte[] array) {
//        for (int i = 0, j = array.length - 1; i < j; ++i, --j) {
//            final byte tmp = array[i];
//            array[i] = array[j];
//            array[j] = tmp;
//        }
//    }
}
