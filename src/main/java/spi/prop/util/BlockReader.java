package spi.prop.util;

import java.io.IOException;
import java.io.InputStream;

public class BlockReader {
	public BlockReader(InputStream inStream) {
		this.inStream = inStream;
		this.inByteBuf = new byte[8192];
		this.blockBuf = new byte[1024];
	}

	int inLimit = 0;
	int inOff = 0;
	private InputStream inStream;
	private byte[] inByteBuf;
	private byte[] blockBuf;
	public byte[] block;

	int readBlock() throws IOException {
		int len = 0;
		char c = 0;

		boolean precedingBackslash = false;

		while (true) {
			if (inOff >= inLimit) {
				inLimit = inStream.read(inByteBuf);
				inOff = 0;
				if (inLimit <= 0) {
					if (len == 0) {
						return -1;
					}
					this.block = new byte[len];
					System.arraycopy(blockBuf, 0, this.block, 0, len);
					return len;
				}
			}

			byte b = inByteBuf[inOff++];

			//The line below is equivalent to calling a
			//ISO8859-1 decoder.
			c = (char) (0xff & b);

			blockBuf[len++] = b;

			if (c != '\n' && c != '\r') {
				if (len == blockBuf.length) {
					int newLength = blockBuf.length * 2;
					if (newLength < 0) {
						newLength = Integer.MAX_VALUE;
					}
					byte[] buf = new byte[newLength];
					System.arraycopy(blockBuf, 0, buf, 0, blockBuf.length);
					blockBuf = buf;
				}
				if (c == '\\') {
					precedingBackslash = !precedingBackslash;
				} else {
					precedingBackslash = false;
				}
			} else {
				if (!precedingBackslash) {
					this.block = new byte[len];
					System.arraycopy(blockBuf, 0, this.block, 0, len);
					return len;
				}
			}
		}
	}
}