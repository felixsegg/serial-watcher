package sw.logic.object;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KeyEventTimeline implements CaptureTimeline<KeyEventTimeline.KeyEvent> {
    public enum KeyEventType {
        PRESS, RELEASE
        
    }
    
    private final List<KeyEvent> events;
    
    public KeyEventTimeline(Collection<KeyEvent> events) {
        this.events = events.stream().sorted().toList();
    }
    
    public List<KeyEvent> getEvents() {
        return List.copyOf(events);
    }
    
    public static class KeyEvent implements TimelineEvent {
        private final String keyId;
        private final Instant timestamp;
        private final KeyEventType type;
        
        public KeyEvent(String keyId, Instant timestamp, KeyEventType type) {
            this.keyId = keyId;
            this.timestamp = timestamp;
            this.type = type;
        }
        
        public String getKeyId() {
            return keyId;
        }
        
        @Override
        public Instant getTimestamp() {
            return timestamp;
        }
        
        public KeyEventType getType() {
            return type;
        }
    }
}
