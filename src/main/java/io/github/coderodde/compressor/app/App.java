package io.github.coderodde.compressor.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class implements a Huffman compressor for binary (byte-wise) data.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.1.2 (Nov 21, 2025)
 * @since 1.0.0 (Nov 14, 2025))
 */
public final class App {
    
    /**
     * The exit status for abnormal termination.
     */
    private static final int EXIT_FAILURE = 1;
    
    /**
     * This extension is added to the compressed files.
     */
    private static final String COMPRESSED_FILE_EXTENSION = ".huf";

    private App() {
        
    }
    
    public static void main(String[] args) {
        
        try {
            if (args.length == 1) {
                compressFile(args[0]);
            } else if (args.length == 2) {
                decompressFile(args[0], args[1]);
            } else {
                printUsage();
            }
        } catch (final Exception ex) {
            error(ex.getMessage());
            System.exit(EXIT_FAILURE);
        }
    }
        
    private static void printUsage() {
        final String jarName = getJarFileName();
        
        System.out.printf(
                String.format(
                        "Usage: %s FILE - to compress FILE into FILE.huf\n", 
                        jarName));
        
        System.out.printf(
                String.format(
                        "       %s FILE.huf OUTPUT_FILE - " + 
                        "to decompress FILE.huf into OUTPUT_FILE\n", 
                        jarName));
    }
    
    private static void compressFile(final String inputFileName) throws IOException {
        final File inputFile = new File(inputFileName);
        
        if (!inputFile.exists()) {
            error(String.format("The input file '%s' does not exist.\n", 
                                inputFileName));
            
            System.exit(EXIT_FAILURE);
        }
        
        if (inputFileName.endsWith(COMPRESSED_FILE_EXTENSION)) {
            error(String.format("Input file '%s' already seems to  be compressed.\n", 
                                inputFileName));
            
            System.exit(EXIT_FAILURE);
        }
        
        final Path path = inputFile.toPath();
        
        long ta = System.currentTimeMillis();
        final byte[] rawData = Files.readAllBytes(path);
        long tb = System.currentTimeMillis();
        
        info(String.format(
                "Read all file bytes in %d milliseconds.\n", tb - ta));
        
        ta = System.currentTimeMillis();
        final byte[] compressedData = HuffmanByteCompressor.compress(rawData);
        tb = System.currentTimeMillis();
        
        info(String.format(
                "Compressed the data in %d milliseconds.\n", tb - ta));
        
        final File outputFile = 
                new File(inputFileName + COMPRESSED_FILE_EXTENSION);
        
        ta = System.currentTimeMillis();
        Files.write(outputFile.toPath(), 
                    compressedData);
        tb = System.currentTimeMillis();
        
        info(String.format(
                "Written the compressed data in %d milliseconds.\n", tb - ta));
    }
    
    private static void decompressFile(final String compressedFileName,
                                       final String outputFileName) throws IOException {
        
        final File compressedFile = new File(compressedFileName);
        final File outputFile = new File(outputFileName);
        
        if (!compressedFile.exists()) {
            error(String.format("Compressed file '%s' does not exist.\n", 
                                compressedFileName));
            
            System.exit(EXIT_FAILURE);
        }
        
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        
        final Path compressedFilePath = compressedFile.toPath();
        
        long ta = System.currentTimeMillis();
        final byte[] compressedData = Files.readAllBytes(compressedFilePath);
        long tb = System.currentTimeMillis();
        
        info(String.format(
                "Read the compressed data in %d milliseconds.\n", tb - ta));
        
        final Path outputFilePath = outputFile.toPath();
        
        ta = System.currentTimeMillis();
        final byte[] originalData =
                HuffmanByteDecompressor.decompress(compressedData);
        tb = System.currentTimeMillis();
        
        info(String.format(
                "Decompressed the data in %d milliseconds.\n", tb - ta));
        
        ta = System.currentTimeMillis();
        Files.write(outputFilePath, originalData);
        tb = System.currentTimeMillis();
        
        info(String.format(
                "Written the decompressed data in %d milliseconds.\n",
                tb - ta));
    }
    
    private static void error(final String message) {
        System.err.printf("[ERROR] %s", message);
    }
    
    private static void info(final String message) {
        System.out.printf("[INFO] %s", message);
    }
    
    /**
     * Obtains the current name of this JAR-file.
     * 
     * @return the name of this JAR-file.
     */
    private static String getJarFileName() {
        return new File(App.class.getProtectionDomain().getCodeSource()
                                                       .getLocation()
                                                       .getPath())
                                                       .getName();
    }
}
