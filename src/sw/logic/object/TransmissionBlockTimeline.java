package sw.logic.object;

import sw.data.uart.UartByte;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class TransmissionBlockTimeline implements CaptureTimeline<TransmissionBlockTimeline.TransmissionBlock> {
    private final List<TransmissionBlock> events;
    
    public TransmissionBlockTimeline(Collection<TransmissionBlock> events) {
        this.events = events.stream().sorted().toList();
    }
    
    @Override
    public List<TransmissionBlock> getEvents() {
        return List.copyOf(events);
    }
    
    public static class TransmissionBlock implements TimelineEvent {
        private final String channelId;
        private final Instant timestamp;
        private final List<Byte> data;
        
        public TransmissionBlock(String channelId, Collection<UartByte> uartBytes) {
            this.channelId = channelId;
            timestamp = uartBytes.stream().map(UartByte::getTimestamp).min(Comparator.naturalOrder()).orElseThrow();
            data = uartBytes.stream().map(UartByte::getData).sorted().toList();
        }
        
        public String getChannelId() {
            return channelId;
        }
        
        @Override
        public Instant getTimestamp() {
            return timestamp;
        }
        
        public List<Byte> getData() {
            return List.copyOf(data);
        }
    }
}
