package sw.data.uart;

/**
 * Unsigned byte helper class
 */
public final class UByte {
    public static String toHex(byte value) {
        return String.format("0x%02X", value & 0xFF);
    }
    
    public static String toHexNoPrefix(byte value) {
        return String.format("%02X", value & 0xFF);
    }
    
    public static byte fromHex(String hex) {
        return (byte) Integer.parseInt(hex.replace("0x", ""), 16);
    }
}
