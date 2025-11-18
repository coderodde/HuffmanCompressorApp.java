package io.github.coderodde.compressor.app;

import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_BYTE_DESCRIPTOR;
import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_CODEWORD_LENGTH;
import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_CODEWORD_MAX;
import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_CODE_SIZE;
import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_RAW_DATA_LENGTH;
import java.nio.ByteBuffer;

/**
 * This class implements a method for <b>decompressing</b> byte-wise files via 
 * Huffman-coding.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.0.0 (Nov 14, 2025)
 * @since 1.0.0 (Nov 14, 2025)
 */
public final class HuffmanByteDecompressor {

    public static byte[] decompress(final byte[] compressedData) {
        final ByteArrayHeaderReader headerReader = 
                new ByteArrayHeaderReader(compressedData);
        
        final int rawDataLength = headerReader.getRawDataLength();
        final byte[] rawData = new byte[rawDataLength];
        
        final HuffmanCodeTable<Byte> codeTable = headerReader.getCodeTable();
        final HuffmanDecoderTree<Byte> decoder = 
                new HuffmanDecoderTree<>(codeTable);
        
        final int startingBitIndex = 
                Utils.countBytesInCodeHeader(codeTable.size()) * Byte.SIZE;
        
        final ByteArrayCompressedDataReader dataReader = 
                new ByteArrayCompressedDataReader(rawData, 
                                                  compressedData, 
                                                  startingBitIndex,
                                                  decoder);
        
        dataReader.read();
        return rawData;
    }
    
    private static int getCodeSize(final byte[] rawData) {
        final byte[] sizeBytes = new byte[BYTES_PER_CODE_SIZE];
        
        System.arraycopy(rawData,
                         0,
                         sizeBytes, 
                         0,
                         sizeBytes.length);
        
        final ByteBuffer byteBuffer = ByteBuffer.wrap(sizeBytes);
        final int codeSize = byteBuffer.getInt();
        
        System.out.println("code size: " + codeSize);
        
        return codeSize;
    }
    
    /**
     * Returns the size of the header in bytes.
     * 
     * @param rawData the raw data to decode.
     * @return the size of the header in bytes.
     */
    private static int getHeaderSize(final byte[] rawData) {
        final int codeSize = getCodeSize(rawData);
        final int codeEntrySize = BYTES_PER_BYTE_DESCRIPTOR + 
                                  BYTES_PER_CODEWORD_LENGTH +
                                  BYTES_PER_CODEWORD_MAX;
        
        return BYTES_PER_CODE_SIZE + 
               BYTES_PER_RAW_DATA_LENGTH +
              (codeSize * codeEntrySize);
    }
}
