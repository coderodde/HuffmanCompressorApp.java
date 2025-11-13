package io.github.coderodde.encoding;

import java.util.Objects;

/**
 * Implements a wrapper containing a symbol from a generic alphabet and its 
 * weight.
 * 
 * @author Rodion "rodde" Efremov
 * @param <S> the actual alphabet symbol type.
 * @version 1.0.0 (Nov 13, 2025)
 * @since 1.0.0 (Nov 13, 2025)
 */
public final class WeightedSymbol<S> {
    
    public final S symbol;
    public final double weight;

    public WeightedSymbol(final S symbol, final double weight) {
        this.symbol = Objects.requireNonNull(symbol);
        this.weight = validateWeight(weight);
    }

    private static double validateWeight(final double weight) {
        if (Double.isNaN(weight)) {
            throw new IllegalArgumentException(
                    "The input weight is NaN");
        }

        if (weight <= 0.0) {
            throw new IllegalArgumentException(
                    String.format("weight(%f) <= 0.0", weight));
        }

        if (Double.isInfinite(weight)) {
            throw new IllegalArgumentException("weight is +Infinity");
        }

        return weight;
    }

    @Override
    public String toString() {
        return String.format("[%s: %f]", symbol.toString(), weight);
    }
    
    @Override
    public boolean equals(final Object object) {
        final WeightedSymbol<S> other = (WeightedSymbol<S>) object;
        return symbol.equals(other.symbol);
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }
}