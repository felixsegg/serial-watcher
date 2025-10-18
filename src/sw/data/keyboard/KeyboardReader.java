package sw.data.keyboard;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class exists simply to have the keyboard events recorded alongside the uart events in the data layer of the
 * application
 */
public class KeyboardReader {
    public enum EventType {
        PRESS, RELEASE
    }
    
    private final AtomicBoolean finished = new AtomicBoolean(false);
    private final Map<String, Map<Instant, EventType>> events = new HashMap<>();
    
    public Runnable[] newHandler(String eventClassId) {
        if (events.containsKey(eventClassId))
            throw new IllegalStateException("Id already registered.");
        
        Runnable onPressed = () -> {
            Instant i = Instant.now();
            if (isFinished())
                throw new IllegalStateException("Can't handle key events in finished handler.");
            events.putIfAbsent(eventClassId, new HashMap<>());
            events.get(eventClassId).put(i, EventType.PRESS);
        };
        
        Runnable onReleased = () -> {
            Instant i = Instant.now();
            if (isFinished())
                throw new IllegalStateException("Can't handle key events in finished handler.");
            events.putIfAbsent(eventClassId, new HashMap<>());
            events.get(eventClassId).put(i, EventType.RELEASE);
        };
        
        return new Runnable[]{onPressed, onReleased};
    }
    
    public Map<Instant, EventType> getEventsForId(String eventClassId) {
        if (!isFinished())
            throw new IllegalStateException("Can't yield results while the Handler is still active.");
        if (!events.containsKey(eventClassId))
            throw new IllegalArgumentException("Not a registered Id.");
        return events.get(eventClassId);
    }
    
    public void finish() {
        finished.set(false);
    }
    
    public boolean isFinished() {
        return finished.get();
    }
}
