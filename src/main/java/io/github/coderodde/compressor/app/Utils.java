package io.github.coderodde.compressor.app;

import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_BYTE_DESCRIPTOR;
import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_CODEWORD_LENGTH;
import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_CODEWORD_MAX;
import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_CODE_SIZE;
import static io.github.coderodde.compressor.app.HuffmanByteCompressor.BYTES_PER_RAW_DATA_LENGTH;

/**
 * This class contains some various helper methods.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.0.0 (Nov 16, 2025)
 * @since 1.0.0 (Nov 16, 2025)
 */
public final class Utils {

    private Utils() {
        
    }
    
    public static long countBitsInRawData(final HuffmanCodeTable<Byte> code,
                                          final byte[] rawData) {
        long bits = 0;
        
        for (final byte b : rawData) {
            bits += code.getCodeword(b).length();
        }
        
        return bits / Byte.SIZE + (bits % Byte.SIZE != 0 ? 1L : 0L);
    }
    
    public static int getCodeEntryLength() {
        return BYTES_PER_BYTE_DESCRIPTOR + 
               BYTES_PER_CODEWORD_LENGTH +
               BYTES_PER_CODEWORD_MAX;
    }
    
    public static int countBytesInCodeHeader(final int codeSize) {
        final int codeEntryLength = getCodeEntryLength();
        
        return (codeSize * codeEntryLength) + BYTES_PER_CODE_SIZE
                                            + BYTES_PER_RAW_DATA_LENGTH;
    }
}
