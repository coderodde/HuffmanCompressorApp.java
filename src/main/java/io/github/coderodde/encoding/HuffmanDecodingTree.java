package io.github.coderodde.encoding;

import java.util.List;
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
        
        boolean isLeaf() {
            return symbol != null;
        }
    }
    
    /**
     * The root node of the tree.
     */
    private final TreeNode<S> root = new TreeNode<>();
    
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
     * @param codeword the codeword encoding a symbol.
     * 
     * @return the encoded symbol.
     */
    public S decode(final CodeWord codeword) {
        Objects.requireNonNull(codeword, "The input codeword is null");
        
        TreeNode<S> node = root;
        
        for (int i = 0; i < codeword.length(); ++i) {
            final boolean bit = codeword.get(i);
            
            if (bit) {
                // The current bit is set, move to the 1-child:
                node = node.oneChild;
            } else {
                // The current bit is not set, move to the 0-child:
                node = node.zeroChild;
            }
            
            if (node == null) {
                throw new IllegalStateException(
                        String.format(
                                "Encountered an invalid codeword (%s)", 
                                codeword.toString()));
            }
        }
        
        return node.symbol;
    }
    
    /**
     * Inserts the symbol/codeword pair into this tree.
     * 
     * @param symbol   the symbol to insert.
     * @param codeword the codeword associated with the input symbol.
     */
    private void insert(final S symbol, final CodeWord codeword) {
        TreeNode<S> node = root;
        
        for (int i = 0; i < codeword.length(); ++i) {
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
