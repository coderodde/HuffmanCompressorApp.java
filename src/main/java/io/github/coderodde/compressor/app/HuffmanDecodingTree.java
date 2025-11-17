package io.github.coderodde.compressor.app;

import java.util.Map;
import java.util.Objects;

/**
 * This class implements the Huffman decoding tree.
 * 
 * @author Rodion "rodde" Efremov
 * @param <S> the alphabet symbol type.
 * @version 1.0.0 (Nov 14, 2025)
 * @since 1.0.0 (Nov 14, 2025)
 */
public final class HuffmanDecodingTree<S> {
    
    /**
     * This static inner class implements the Huffman decoding tree node.
     * 
     * @param <S> the alphabet symbol type. 
     */
    private static final class TreeNode<S> {
        S symbol;
        TreeNode<S> zeroChild;
        TreeNode<S> oneChild;
    }
    
    /**
     * The root node of the tree.
     */
    private final TreeNode<S> root = new TreeNode<>();
    
    /**
     * Caches the code length of the previously decoded symbol.
     */
    private int previousCodeLength = -1;
    
    /**
     * Constructs this Huffman decoding tree.
     * 
     * @param codeTable the code table for which to construct the decoding tree. 
     */
    public HuffmanDecodingTree(final HuffmanCodeTable<S> codeTable) {
        Objects.requireNonNull(codeTable, "The input code table is null");
        
        for (final Map.Entry<S, CodeWord> entry : codeTable) {
            final S symbol = entry.getKey();
            final CodeWord codeword = entry.getValue();
            insert(symbol, codeword);
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
    public S decode(final byte[] compressedData, int bitIndex) {
        Objects.requireNonNull(compressedData, "The input raw data is null");
        
        previousCodeLength = 0;
        
        TreeNode<S> node = root;
        
        while (node.symbol == null) {
            final boolean bit = readBit(compressedData, bitIndex);
            node = (bit ? node.oneChild : node.zeroChild);
            ++bitIndex;
            ++previousCodeLength;
        }
        
        return node.symbol;
    }
    
    public int getPreviousCodeLength() {
        return previousCodeLength;
    }
    
    private static boolean readBit(final byte[] rawData, final int bitIndex) {
        final int byteIndex = bitIndex / Byte.SIZE;
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
    private void insert(final S symbol, CodeWord codeword) {
//        codeword = codeword.reverse();
        
        TreeNode<S> node = root;
        
        for (int i = codeword.length() - 1; i >= 0; --i) {
            final boolean bit = codeword.get(i);
            
            if (bit) { 
                // Bit is set to 1.
                if (node.oneChild == null) {
                    node.oneChild = new TreeNode<>();
                }
                
                node = node.oneChild;
            } else {
                // Bit is set to 0.
                if (node.zeroChild == null) {
                    node.zeroChild = new TreeNode<>();
                }
                
                node = node.zeroChild;
            }
        }
        
        if (node.symbol != null) {
            throw new IllegalStateException(
                    String.format(
                            "Dublicate codeword [%s]: " + 
                                "two symbols share the same code.", 
                            codeword.toString()));
        }
        
        node.symbol = symbol;
    }
}
