package com.ht.RCSAndroidGUI.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.ht.RCSAndroidGUI.Debug;


/**
 * The Class WChar.
 */
public final class WChar {
    //#ifdef DEBUG
    private static Debug debug = new Debug("WChar");
    //#endif

    /**
     * Gets the bytes.
     * 
     * @param string
     *            the string
     * @return the bytes
     */
    public static byte[] getBytes(final String string) {
        return getBytes(string, false);
    }

    /**
     * Gets the bytes.
     * 
     * @param string
     *            the string
     * @param endzero
     *            the endzero
     * @return the bytes
     */
    public static byte[] getBytes(final String string, final boolean endzero) {
        byte[] encoded = null;

        try {
            encoded = string.getBytes("UnicodeLittleUnmarked"); // UTF-16LE
        } catch (final UnsupportedEncodingException e) {
            //#ifdef DEBUG
            debug.error("UnsupportedEncodingException");
            //#endif
        }

        if (endzero) {
            final byte[] zeroencoded = new byte[encoded.length + 2];
            Utils.copy(zeroencoded, encoded, encoded.length);
            encoded = zeroencoded;
        }

        return encoded;
    }

    public static byte[] pascalize(byte[] message) {

        int len = message.length;
        if (message[message.length - 2] != 0
                || message[message.length - 1] != 0) {
            len += 2; //aggiunge lo spazio per lo zero
        }

        final byte[] pascalzeroencoded = new byte[len + 4];
        Utils.copy(pascalzeroencoded, Utils.intToByteArray(len), 4);
        Utils.copy(pascalzeroencoded, 4, message, 0, message.length);

        //#ifdef DEBUG
        debug.trace("pascalize " + Utils.byteArrayToHex(message) + " = " +Utils.byteArrayToHex(pascalzeroencoded));
        //#endif
        
        //#ifdef DBC
        Check.ensures(pascalzeroencoded[len - 1] == 0, "pascalize not null");
        //#endif
        return pascalzeroencoded;
    }

    /**
     * Gets the string.
     * 
     * @param message
     *            the message
     * @param endzero
     *            the endzero
     * @return the string
     */
    public static String getString(final byte[] message, final boolean endzero) {
        return getString(message, 0, message.length, endzero);
    }

    /**
     * Gets the string.
     * 
     * @param message
     *            the message
     * @param offset
     *            the offset
     * @param length
     *            the length
     * @param endzero
     *            the endzero
     * @return the string
     */
    public static String getString(final byte[] message, final int offset,
            final int length, final boolean endzero) {
        String decoded = "";

        try {
            decoded = new String(message, offset, length,
                    "UnicodeLittleUnmarked");

        } catch (final UnsupportedEncodingException e) {
            //#ifdef DEBUG
            debug.error("UnsupportedEncodingException");
            //#endif
        }

        if (endzero) {
            final int lastPos = decoded.indexOf('\0');
            if (lastPos > -1) {
                decoded = decoded.substring(0, lastPos);
            }
        }

        return decoded;
    }

    private WChar() {
    }

	public static String readPascal(DataBuffer dataBuffer) throws IOException {
		 int len = dataBuffer.readInt();
	        if(len < 0 || len > 65536){
	            return null;
	        }
	        
	        byte[] payload= new byte[len];
	        dataBuffer.read(payload);
	        return WChar.getString(payload, true);
	}

	public static byte[] pascalize(String string) {
		return pascalize(WChar.getBytes(string));
	}

}
