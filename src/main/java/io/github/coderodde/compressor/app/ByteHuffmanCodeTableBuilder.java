package io.github.coderodde.compressor.app;

import static io.github.coderodde.compressor.app.Configuration.CODE_TABLE_CAPACITY;
import java.util.HashSet;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * This class implements the Huffman code builder over weight distributions.
 * 
 * @author Rodion "rodde" Efremov
 * @version 2.0.1 (Nov 14, 2025)
 * @since 1.0.0 (Nov 12, 2025)
 */
public final class ByteHuffmanCodeTableBuilder {
    
    private ByteHuffmanCodeTableBuilder() {
        // Hide constructor.
    }
    
    public static ByteHuffmanCodeTable
        buildCode(final ByteFrequencyDistribution byteFrequencyDistribution) {
            
        Objects.requireNonNull(byteFrequencyDistribution,
                               "The input byte frequency distribution is null");
        
        if (byteFrequencyDistribution.isEmpty()) {
            throw new IllegalArgumentException(
                    "The input byte frequency distribution is empty");
        }
        
        final ByteHuffmanCodeTable codeTable = new ByteHuffmanCodeTable();
        final Queue<WeightedByteSet> queue   = new PriorityQueue<>();
        
        for (int i = 0; i < CODE_TABLE_CAPACITY; ++i) {
            // Grab the 8 least significant bits of 'i':
            final byte value = (byte)(i & 0xff);
            final long frequency =
                    byteFrequencyDistribution.getFrequency(value);
            
            if (frequency > 0L) {
                // Once here, value is present in the compressed data:
                final Set<Byte> set = new HashSet<>();
                set.add(value);
                codeTable.put(value, new CodeWord(0));
                queue.add(new WeightedByteSet(set, frequency));
            }
        }
        
        while (queue.size() > 1) {
            final WeightedByteSet entry1 = queue.remove();
            final WeightedByteSet entry2 = queue.remove();
            
            for (final byte value : entry1.getSet()) {
                codeTable.get(value).prependBit(true);
            }
            
            for (final byte value : entry2.getSet()) {
                codeTable.get(value).prependBit(false);
            }
            
            entry1.getSet().addAll(entry2.getSet());
            
            queue.add(new WeightedByteSet(
                            entry1.getSet(),
                            entry1.getTotalWeight() + 
                            entry2.getTotalWeight()));
        }
        
        return codeTable;
    }
}
