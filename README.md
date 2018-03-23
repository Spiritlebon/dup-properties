# dup-properties
Java class which can remove duplicates properties (all but last) and preserve original file format.  
Also handle multiline properties.

This class extends the java.util.Properties class.  
The load0 method has been tweeked to use a custom BlockReader instead of the internal lineReader class.

Note that the load(Reader) implementation will throw an exception, because the class is byte[] oriented (and not char[]) to preserve any original characters without the encoding problem.

min java version: 1.5

```java
        BlockProperties blockprop = new BlockProperties();
        
        try (InputStream in = new FileInputStream(new File("sample.properties"))) {
            blockprop.load(in);
        }
        
        try (OutputStream out = new FileOutputStream(new File("sample_out.properties"))) {
            // store properties without dups and preserving original format
            blockprop.store(out, true);
        }

        try (OutputStream out = new FileOutputStream(new File("sample_out2.properties"))) {
        		// original Properties.store method. Remove dups, but all original format is lost, so as the properties order.
            blockprop.store(out, null);
        }


```
