package sw.logic.object;

import org.apache.commons.collections4.list.UnmodifiableList;
import sw.data.uart.UartByte;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;

public class TransmissionBlock {
    private final Instant timestamp;
    private final UnmodifiableList<Byte> data;
    
    public TransmissionBlock(Collection<UartByte> uartBytes) {
        timestamp = uartBytes.stream().map(UartByte::getTimestamp).min(Comparator.naturalOrder()).orElseThrow();
        data = new UnmodifiableList<>(uartBytes.stream().map(UartByte::getData).sorted().toList());
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public UnmodifiableList<Byte> getData() {
        return data;
    }
}
