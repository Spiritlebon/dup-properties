package spi.prop.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class BlockProperties extends Properties {

    private static final long serialVersionUID = -8402393169881366739L;

    public List<Block> blockList = new LinkedList<Block>();

    public synchronized void load(Reader reader) throws IOException {
        throw new RuntimeException("Reader not implemented, use InputStream implementation instead.");
    }

    public synchronized void load(InputStream inStream) throws IOException {
        load0(new BlockReader(inStream));
    }

    private void load0(BlockReader blockReader) throws IOException {
        while (blockReader.readBlock() >= 0) {
            Block block = new Block();
            block.block = blockReader.block;

            Properties prop = new Properties();
            prop.load(new InputStreamReader(new ByteArrayInputStream(blockReader.block)));
            for (Object key : prop.keySet()) {
                if (key.toString().trim().length() > 0) {
                    block.key = key;
                    block.value = prop.get(key);
                    checkdup(block);
                    put(block.key, block.value);
                }
            }
            this.blockList.add(block);
        }
    }

    private void checkdup(Block block) {
        for (Block b : this.blockList) {
            if (b.key != null) {
                if (b.key.equals(block.key)) {
                    b.isdup = true;
                    b.islastdup = false;
                    block.isdup = true;
                    block.islastdup = true;
                }
            }
        }
    }

    public synchronized void store(OutputStream out) throws IOException {
        store(new BufferedOutputStream(out), false);
    }

    public synchronized void store(OutputStream out, boolean filterdup) throws IOException {
        store0(new BufferedOutputStream(out), filterdup);
    }

    private void store0(BufferedOutputStream bw, boolean filterdup) throws IOException {
        for (Block block : this.blockList) {
            if (!block.isdup || block.islastdup) {
                bw.write(block.block);
            }
        }
        bw.flush();
    }
}
