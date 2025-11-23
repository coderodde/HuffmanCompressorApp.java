package io.github.coderodde.compressor.app;

/**
 * This class implements a <b>binary</b> code word in data compression 
 * scenarios.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.1.2 (Nov 21, 2025)
 * @since 1.0.0 (Oct 28, 2025)
 */
public class CodeWord {

    private int length;
    private long bits;
    
    public CodeWord(final int length) {
        checkLength(length);
        this.length = length;
    }
    
    public CodeWord reverse() {
        final CodeWord reversed = new CodeWord(length);
        
        for (int i = length - 1, j = 0; i >= 0; --i, ++j) {
            if (get(i)) {
                reversed.set(j);
            }
        }
        
        return reversed;
    }
    
    public byte[] toByteArray() {
        final int byteArrayLength = 
                length / Byte.SIZE + (length % Byte.SIZE != 0 ? 1 : 0);
        
        final byte[] byteArray = new byte[byteArrayLength];
        
        for (int byteArrayIndex = 0;
                 byteArrayIndex < byteArrayLength;
                 byteArrayIndex++) {
            
            byteArray[byteArrayIndex] = extractByte(byteArrayIndex);
        }
        
        return byteArray;
    }
    
    public void prependBit(final boolean bit) {
        ++length;
        
        if (bit) {
            set(length - 1);
        }
    }
    
    public int length() {
        return length;
    }
    
    public boolean get(final int index) {
        checkIndex(index);
        final long mask = 1L << index;
        return ((bits & mask) != 0);
    }
    
    public void set(final int index) {
        checkIndex(index);
        final long mask = 1L << index;
        bits |= mask;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        
        if (object == null) {
            return false;
        }
    
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        
        final CodeWord other = (CodeWord) object;
        
        if (length() != other.length()) {
            return false;
        }
        
        for (int i = 0; i < length(); ++i) {
            if (get(i) != other.get(i)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(length);
        
        for (int i = length - 1; i >= 0; --i) {
            sb.append(get(i) ? "1" : "0");
        }
        
        return sb.toString();
    }
    
    private byte extractByte(final int byteArrayIndex) {
        final long tmp = (bits >>> (byteArrayIndex * Byte.SIZE));
        return (byte) (tmp & 0xff);
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
