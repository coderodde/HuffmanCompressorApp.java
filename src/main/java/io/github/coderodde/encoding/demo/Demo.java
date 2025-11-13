package io.github.coderodde.encoding.demo;

import io.github.coderodde.encoding.HuffmanCodeBuilder;
import io.github.coderodde.encoding.HuffmanCodeTable;
import io.github.coderodde.encoding.WeightDistribution;

/**
 * This class demonstrates the Huffmann encoding.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.1.0 (Nov 13, 2025)
 * @since 1.0.0 (Now 12, 2025)
 */
public final class Demo {

    public static void main(String[] args) {
        demo1();
        dctWeek2Task1();
    }
    
    private static void demo1() {
        System.out.println("--- demo1 ---");
        
        WeightDistribution<Character> weightDistribution = 
                new WeightDistribution<>();
        
        weightDistribution.associateSymbolWithWeight('a', 5.0);
        weightDistribution.associateSymbolWithWeight('b', 9.0);
        weightDistribution.associateSymbolWithWeight('c', 12.);
        weightDistribution.associateSymbolWithWeight('d', 13.0);
        weightDistribution.associateSymbolWithWeight('e', 16.0);
        weightDistribution.associateSymbolWithWeight('f', 45.0);
        
        HuffmanCodeTable<Character> code = 
                HuffmanCodeBuilder.buildCode(weightDistribution);
        
        System.out.println(code);
    }
    
    private static void dctWeek2Task1() {
        System.out.println("--- dctWeek2Task1 ---");
        
        WeightDistribution<Character> weightDistribution = 
                new WeightDistribution<>();
        
        weightDistribution.associateSymbolWithWeight('a', 1.0 / 6.0);
        weightDistribution.associateSymbolWithWeight('b', 1.0 / 5.0);
        weightDistribution.associateSymbolWithWeight('c', 1.0 / 20.0);
        weightDistribution.associateSymbolWithWeight('d', 1.0 / 5.0);
        weightDistribution.associateSymbolWithWeight('e', 1.0 / 120.0);
        weightDistribution.associateSymbolWithWeight('f', 1.0 / 8.0);
        weightDistribution.associateSymbolWithWeight('g', 1.0 / 4.0);
        
        HuffmanCodeTable<Character> code = 
                HuffmanCodeBuilder.buildCode(weightDistribution);
        
        System.out.println(code);
    }
}
