package io.github.coderodde.compressor.app;

/**
 * This class contains configuration constants.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.0.0 (Nov 19, 2025)
 * @since 1.0.0 (Nov 19, 2025)
 */
final class Configuration {

    /**
     * This capacity stands for {@code 2^8 = 256}, the number of distinct byte 
     * values.
     */
    static final int CODE_TABLE_CAPACITY = 256;
    
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
    
    private Configuration() {
        
    }
}
