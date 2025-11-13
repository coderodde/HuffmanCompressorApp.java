package io.github.coderodde.encoding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * This class encapsulates a weight distribution.
 * 
 * @author Rodion "rodde" Efremov
 * @param <S> the alphabet symbol type.
 * 
 * @version 1.0.0 (Nov 13, 2025)
 * @since 1.0.0 (Nov 13, 2025)
 */
public final class WeightDistribution<S> implements Iterable<Map.Entry<S, Double>> {

    /**
     * The actual dictionary mapping symbols to their weights.
     */
    private final Map<S, Double> symbolWeightMap = new HashMap<>();
    
    /**
     * Returns the number of symbol/weight mappings.
     * 
     * @return the size of this distribution. 
     */
    public int size() {
        return symbolWeightMap.size();
    }
    
    /**
     * Returns {@code true} if and only if this distribution is empty.
     * 
     * @return a Boolean flag indicating whether this distribution is empty.
     */
    public boolean isEmpty() {
        return symbolWeightMap.isEmpty();
    }
    
    /**
     * Associates the input {@code symbol} with {@code weight}.
     * 
     * @param symbol the symbol to associate.
     * @param weight the associated weight.
     */
    public void associateSymbolWithWeight(final S symbol, final double weight) {
        Objects.requireNonNull(symbol, "The input symbol is null");
        checkWeight(weight);
        symbolWeightMap.put(symbol, weight);
    }
    
    /**
     * Returns the weight that is associated with the {@code symbol}.
     * 
     * @param symbol the symbol to query.
     * @return the weight of {@code symbol}.
     */
    public double getSymbolWeight(final S symbol) {
        Objects.requireNonNull(symbol, "The input symbol is null");
        
        if (!symbolWeightMap.containsKey(symbol)) {
            throw new SymbolNotFoundException(
                    String.format("Symbol '%s' is unknown", symbol));
        }
        
        return symbolWeightMap.get(symbol);
    }
        
    private static void checkWeight(final double weight) {
        if (Double.isNaN(weight)) {
            throw new IllegalArgumentException("weight is NaN");
        }

        if (weight <= 0.0) {
            throw new IllegalArgumentException(
                    String.format("weight(%f) <= 0.0", weight));
        }

        if (Double.isInfinite(weight)) {
            throw new IllegalArgumentException("weight is Infinity");
        }
    }

    /**
     * Returns the iterator over this distribution's entries.
     * 
     * @return the iterator.
     */
    @Override
    public Iterator<Map.Entry<S, Double>> iterator() {
        return new WeightDistributionIterator();
    }
    
    @Override
    public String toString() {
        return symbolWeightMap.toString();
    }
    
    /**
     * The weighted entry.
     * 
     * @param <S> the symbol type.
     */
    private static final class WeightedDistributionEntry<S> 
            implements Map.Entry<S, Double> {

        private final Map.Entry<S, Double> mapEntry;

        WeightedDistributionEntry(final Map.Entry<S, Double> mapEntry) {
            this.mapEntry = mapEntry;
        }
        
        @Override
        public S getKey() {
            return mapEntry.getKey();
        }

        @Override
        public Double getValue() {
            return mapEntry.getValue();
        }

        @Override
        public Double setValue(final Double weight) {
            Objects.requireNonNull(weight, "The input weight is null");
            checkWeight(weight);
            final Double old = mapEntry.getValue();
            mapEntry.setValue(weight);
            return old;
        }
    }
    
    /**
     * Wraps the actual symbol table iterator.
     */
    private final class WeightDistributionIterator
            implements Iterator<Map.Entry<S, Double>> {
        
        private final Iterator<Map.Entry<S, Double>> iterator;

        public WeightDistributionIterator() {
            this.iterator = symbolWeightMap.entrySet().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public WeightedDistributionEntry<S> next() {
            return new WeightedDistributionEntry<>(iterator.next());
        }
    }
}
