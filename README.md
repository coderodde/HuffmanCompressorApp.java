#### Compressing

In order to compress a file `file.dat`, invoke the result `.jar` file as follows:
```
java -jar HuffmanCompressorApp.jar file.dat
```
That will create a compressed file `file.data.huf`.

#### Decompressing

In order to decompress a `.huf` file, invoke as follows:
```
java -jar HuffmanCompressorApp.jar file.dat.huf uncompressed.file.txt
```

#### Checking if two files are same
On Windows command line, type:
```
fc /b FILE1 FILE2
```
On *nix, type:
```
cmp -s FILE1 FILE2
```
