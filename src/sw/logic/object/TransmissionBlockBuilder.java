package sw.logic.object;

import sw.data.uart.UartByte;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;

public class TransmissionBlockBuilder {
    private final Collection<UartByte> data = new HashSet<>();
    private Instant timestampOfLatestAddition;
    
    public TransmissionBlockBuilder add(UartByte uartByte) {
        data.add(uartByte);
        timestampOfLatestAddition = uartByte.getTimestamp();
        return this;
    }
    
    public Instant getTimestampOfLatestAddition() {
        return timestampOfLatestAddition;
    }
    
    public TransmissionBlock build() {
        return new TransmissionBlock(data);
    }
}
