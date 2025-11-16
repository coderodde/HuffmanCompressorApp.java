package io.github.coderodde.compressor.app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * This class implements the Huffman encoding table.
 * 
 * @author Rodion "rodde" Efremov
 * @param <S> the alphabet symbol type.
 * 
 * @version 1.0.0 (Nov 13, 2025)
 * @since 1.0.0 (Nov 13, 2025)
 */
public final class HuffmanCodeTable<S> 
        implements Iterable<Map.Entry<S, CodeWord>> {

    /**
     * The actual encoding table.
     */
    private final Map<S, CodeWord> symbolToCodeWordMap = new HashMap<>();
    
    /**
     * Returns the number of symbol to codeword mappings.
     * 
     * @return the size of this encoding table. 
     */
    public int size() {
        return symbolToCodeWordMap.size();
    }
    
    /**
     * Returns {@code true} if and only if this encoding table has no mappings.
     * 
     * @return a Boolean flag indicating whether this encoding table is empty. 
     */
    public boolean isEmpty() {
        return symbolToCodeWordMap.isEmpty();
    }
    
    public CodeWord getCodeword(final S symbol) {
        Objects.requireNonNull(symbol, "The input symbol is null");
        
        if (!symbolToCodeWordMap.containsKey(symbol)) {
            throw new SymbolNotFoundException(
                    String.format(
                            "Symbol '%s' is not present in this encoding table",
                            symbol));
        }
        
        return symbolToCodeWordMap.get(symbol);
    }
    
    public void linkSymbolToCodeword(final S symbol, final CodeWord codeword) {
        Objects.requireNonNull(symbol, "The input symbol is null");
        Objects.requireNonNull(codeword, "The input codeword is null");
        
        symbolToCodeWordMap.put(symbol, codeword);
    }

    @Override
    public int hashCode() {
        return symbolToCodeWordMap.hashCode();
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
        
        final HuffmanCodeTable<S> other = (HuffmanCodeTable<S>) object;
        
        if (size() != other.size()) {
            return false;
        }
        
        for (final Map.Entry<S, CodeWord> entry : this) {
            final S symbol = entry.getKey();
            final CodeWord codeword = entry.getValue();
            final CodeWord codewordFromOther = other.getCodeword(symbol);
            
            if (!codeword.equals(codewordFromOther)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public Iterator<Map.Entry<S, CodeWord>> iterator() {
        return new SymbolCodeWordIterator();
    }
    
    @Override
    public String toString() {
        return symbolToCodeWordMap.toString();
    }
    
    /**
     * The symbol/codeword entry.
     * 
     * @param <S> the symbol type.
     */
    private static final class SymbolCodeWordEntry<S> 
            implements Map.Entry<S, CodeWord> {

        private final Map.Entry<S, CodeWord> mapEntry;

        SymbolCodeWordEntry(final Map.Entry<S, CodeWord> mapEntry) {
            this.mapEntry = mapEntry;
        }
        
        @Override
        public S getKey() {
            return mapEntry.getKey();
        }

        @Override
        public CodeWord getValue() {
            return mapEntry.getValue();
        }

        @Override
        public CodeWord setValue(final CodeWord codeword) {
            Objects.requireNonNull(codeword, "The input codeword is null");
            final CodeWord old = mapEntry.getValue();
            mapEntry.setValue(codeword);
            return old;
        }
    }
    
    /**
     * Wraps the actual symbol table iterator.
     */
    private final class SymbolCodeWordIterator
            implements Iterator<Map.Entry<S, CodeWord>> {
        
        private final Iterator<Map.Entry<S, CodeWord>> iterator;

        public SymbolCodeWordIterator() {
            this.iterator = symbolToCodeWordMap.entrySet().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public SymbolCodeWordEntry<S> next() {
            return new SymbolCodeWordEntry<>(iterator.next());
        }
    }
}
