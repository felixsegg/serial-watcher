package sw.data.uart;

import java.time.Instant;

public class UartByte implements Comparable<UartByte> {
    private final byte data;
    private final Instant timestamp;
    private final String analyzerId;
    private final boolean error;
    
    public UartByte(byte data, Instant timestamp, String analyzerId, boolean error) {
        this.data = data;
        this.timestamp = timestamp;
        this.analyzerId = analyzerId;
        this.error = error;
    }
    
    public byte getData() {
        return data;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public String getAnalyzerId() {
        return analyzerId;
    }
    
    public boolean isError() {
        return error;
    }
    
    @Override
    public int compareTo(UartByte o) {
        return this.timestamp.compareTo(o.timestamp);
    }
}
