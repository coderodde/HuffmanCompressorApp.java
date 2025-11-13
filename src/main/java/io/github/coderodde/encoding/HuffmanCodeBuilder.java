package io.github.coderodde.encoding;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * This class implements the Huffman code builder over weight distributions.
 * 
 * @author Rodion "rodde" Efremov
 * @version 2.0.0 (Nov 13, 2025)
 * @since 1.0.0 (Nov 12, 2025)
 */
public final class HuffmanCodeBuilder {
    
    private HuffmanCodeBuilder() {
        // Hide constructor.
    }
    
    public static <S> HuffmanCodeTable<S>
        buildCode(final WeightDistribution<S> weightDistribution) {
            
        Objects.requireNonNull(weightDistribution,
                               "The input probability distribution is null");
        
        final HuffmanCodeTable<S> codeTable = new HuffmanCodeTable<>();
        
        if (weightDistribution.isEmpty()) {
            return codeTable;
        }
        
        final Queue<WeightedSymbolSet<S>> queue = new PriorityQueue<>();
       
        for (final Map.Entry<S, Double> entry : weightDistribution) {
            final double weight = entry.getValue();
            final S symbol      = entry.getKey();
            
            final Set<WeightedSymbol<S>> set = new HashSet<>();
            
            set.add(new WeightedSymbol<>(symbol, weight));
            queue.add(new WeightedSymbolSet<>(set, weight));
        }
        
        for (final Map.Entry<S, Double> weightEntry : weightDistribution) {
            codeTable.linkSymbolToCodeword(weightEntry.getKey(), 
                                           new CodeWord(0));
        }
        
        while (queue.size() > 1) {
            final WeightedSymbolSet<S> entry1 = queue.remove();
            final WeightedSymbolSet<S> entry2 = queue.remove();
            
            for (final WeightedSymbol<S> symbolEntry : entry1.set) {
                final S symbol = symbolEntry.symbol;
                codeTable.getCodeword(symbol).prependBit(true);
            }
            
            for (final WeightedSymbol<S> symbolEntry : entry2.set) {
                final S symbol = symbolEntry.symbol;
                codeTable.getCodeword(symbol).prependBit(false);
            }
            
            entry1.set.addAll(entry2.set);
            
            queue.add(new WeightedSymbolSet<>(
                            entry1.set, 
                            entry1.totalSetWeight + 
                            entry2.totalSetWeight));
        }
        
        return codeTable;
    }
        
    private static final class WeightedSymbolSet<T> 
            implements Comparable<WeightedSymbolSet<T>> {
        
        Set<WeightedSymbol<T>> set;
        double totalSetWeight;
        
        WeightedSymbolSet(final Set<WeightedSymbol<T>> set,
                           final double totalSetWeight) {
            this.set = set;
            this.totalSetWeight = totalSetWeight;
        }

        @Override
        public int compareTo(final WeightedSymbolSet<T> o) {
            return Double.compare(totalSetWeight, o.totalSetWeight);
        }
    }
}
