package io.github.coderodde.encoding;

import java.util.BitSet;

/**
 * This class implements a <b>binary</b> code word in data compression 
 * scenarios.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.1.0 (Nov 13, 2025)
 * @since 1.0.0 (Oct 28, 2025)
 */
public class CodeWord {

    private int length;
    private final BitSet bits;
    
    public CodeWord(final int length) {
        checkLength(length);
        this.length = length;
        this.bits = new BitSet(length);
    }
    
    public void prependBit(final boolean bit) {
        if (bit) {
            bits.set(length);
        }
        
        ++length;
    }
    
    public int length() {
        return length;
    }
    
    public boolean get(final int index) {
        checkIndex(index);
        return bits.get(index);
    }
    
    public void set(final int index) {
        checkIndex(index);
        bits.set(index);
    }
    
    public void unset(final int index) {
        checkIndex(index);
        bits.set(index, false);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(length);
        
        for (int i = length - 1; i >= 0; --i) {
            sb.append(get(i) ? "1" : "0");
        }
        
        return sb.toString();
    }
    
    private void checkIndex(final int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(
                    String.format("index(%d) < 0", index));
        }
        
        if (index >= this.length) {
            throw new IndexOutOfBoundsException(
                    String.format("index(%d) >= length(%d)", 
                                  index, 
                                  length));
        }
    }
    
    private static void checkLength(final int length) {
        if (length < 0) {
            throw new IllegalArgumentException(
                    String.format("length(%d) < 1", length));
        }
    }
}
