package io.github.coderodde.compressor.app;

import java.util.Objects;

/**
 * This class implements the Huffman decoding tree.
 * 
 * @author Rodion "rodde" Efremov
 * @param <S> the alphabet symbol type.
 * @version 1.0.0 (Nov 14, 2025)
 * @since 1.0.0 (Nov 14, 2025)
 */
public final class ByteHuffmanDecoderTree<S> {
    
    /**
     * This static inner class implements the Huffman decoding tree node.
     * 
     * @param <S> the alphabet symbol type. 
     */
    private static final class TreeNode {
        Byte value;
        TreeNode zeroChild;
        TreeNode oneChild;
    }
    
    /**
     * The root node of the tree.
     */
    private final TreeNode root = new TreeNode();
    
    /**
     * Caches the code length of the previously decoded symbol.
     */
    private long previousCodeLength = -1L;
    
    /**
     * Constructs this Huffman decoding tree.
     * 
     * @param codeTable the code table for which to construct the decoding tree. 
     */
    public ByteHuffmanDecoderTree(final ByteHuffmanCodeTable codeTable) {
        Objects.requireNonNull(codeTable, "The input code table is null");
        
        for (int value = 0; 
                 value < Configuration.CODE_TABLE_CAPACITY;
                 value++) {
            final CodeWord codeword = codeTable.get((byte) value);
            
            if (codeword != null) {
                final Byte byteValue = Byte.valueOf((byte) value);
                insert(byteValue, codeword);
            }
        }
    }
    
    /**
     * Decodes a codeword to a symbol.
     * 
     * @param compressedData the compressed data for decompression.
     * @param bitIndex       the index of the starting bit of a codeword to 
     *                       scan.
     * @return the encoded symbol.
     */
    public byte decode(final byte[] compressedData, long bitIndex) {
        Objects.requireNonNull(compressedData, "The input raw data is null");
        
        previousCodeLength = 0;
        
        TreeNode node = root;
        
        while (node.value == null) {
            final boolean bit = readBit(compressedData,
                                        bitIndex);
            
            node = bit ? node.oneChild : node.zeroChild;
            
            ++bitIndex;
            ++previousCodeLength;
        }
        
        return node.value;
    }
    
    public long getPreviousCodeLength() {
        return previousCodeLength;
    }
    
    private static boolean readBit(final byte[] rawData, final long bitIndex) {
        final int byteIndex = (int) (bitIndex / Byte.SIZE);
        final byte targetByte = rawData[byteIndex];
        final byte mask = (byte)(1 << (bitIndex % Byte.SIZE));
        
        return ((mask & targetByte) != 0);
    }
    
    /**
     * Inserts the symbol/codeword pair into this tree.
     * 
     * @param symbol   the symbol to insert.
     * @param codeword the codeword associated with the input symbol.
     */
    private void insert(final Byte value, final CodeWord codeword) {
        TreeNode node = root;
        
        for (int i = codeword.length() - 1; i >= 0; --i) {
            final boolean bit = codeword.get(i);
            
            if (bit) { 
                // Bit is set to 1.
                if (node.oneChild == null) {
                    node.oneChild = new TreeNode();
                }
                
                node = node.oneChild;
            } else {
                // Bit is set to 0.
                if (node.zeroChild == null) {
                    node.zeroChild = new TreeNode();
                }
                
                node = node.zeroChild;
            }
        }
        
        if (node.value != null) {
            throw new IllegalStateException(
                    String.format(
                            "Dublicate codeword [%s]: " + 
                                "two symbols share the same code.", 
                            codeword.toString()));
        }
        
        node.value = value;
    }
}
