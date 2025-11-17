package io.github.coderodde.compressor.app;

import java.util.Objects;

/**
 * This class is responsible for decompressing the actual compressed data to the
 * compression source data.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.0.0 (Nov 17, 2025)
 * @since 1.0.0 (Nov 17, 2025)
 */
public final class ByteArrayCompressedDataReader {

    /**
     * The resultant decompressed data.
     */
    private final byte[] outputRawData;
    
    /**
     * The input compressed data.
     */
    private final byte[] inputCompressedData;
    
    /**
     * The index of the bit where compressed data begins.
     */
    private final int startingBitIndex;
    
    /**
     * The (Huffman) decoder tree.
     */
    private final HuffmanDecodingTree<Byte> decoderTree;
    
    /**
     * Constructs this compressed data reader/decompressor.
     * 
     * @param outputRawData       the resultant decompressed data.
     * @param inputCompressedData the input compressed data.
     * @param startingBitIndex    the index of the first bit to decompress right
     *                            after the header.
     * @param decoderTree         the decoder tree.
     */
    public ByteArrayCompressedDataReader(final byte[] outputRawData,
                                         final byte[] inputCompressedData,
                                         final int startingBitIndex,
                                         final HuffmanDecodingTree<Byte> 
                                                 decoderTree) {
        
        this.outputRawData = 
                Objects.requireNonNull(
                        outputRawData, 
                        "The output raw data is null");
        
        this.inputCompressedData = 
                Objects.requireNonNull(
                        inputCompressedData,
                        "The input compressed data is null");
        
        this.decoderTree =
                Objects.requireNonNull(
                        decoderTree, 
                        "The input decoder tree is null");
        
        this.startingBitIndex = startingBitIndex;
    }
    
    /**
     * Decompresses and reads the compressed data.
     */
    public void read() {
        final int totalBytes = outputRawData.length;
        int currentBitIndex = startingBitIndex;
        
        for (int byteIndex = 0; 
                 byteIndex != totalBytes;
                 byteIndex++) {
            
            final Byte dataByte = decoderTree.decode(inputCompressedData,
                                                     currentBitIndex);
            outputRawData[byteIndex] = dataByte;
            currentBitIndex += decoderTree.getPreviousCodeLength();
        }
    }
}
