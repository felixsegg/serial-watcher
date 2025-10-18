package sw.logic.service;

import sw.data.keyboard.KeyboardReader;
import sw.logic.object.KeyEventTimeline;
import sw.logic.object.helper.KeyEventLayerMapper;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KeyEventService implements CaptureService<KeyEventTimeline> {
    private KeyboardReader reader;
    private final Map<String, KeyEventHandlers> idHandlerMap = new HashMap<>();
    
    public void prepareCapture() {
        if (reader != null && !reader.isFinished())
            throw new IllegalStateException("Previous reader is still active.");
        idHandlerMap.clear();
        reader = new KeyboardReader();
    }
    
    public void registerKey(String keyId) {
        if (idHandlerMap.containsKey(keyId))
            throw new IllegalStateException("Key Id is already registered.");
        Runnable[] runnables = reader.newHandler(keyId);
        idHandlerMap.put(keyId, new KeyEventHandlers(runnables[0], runnables[1]));
    }
    
    @Override
    public void startCapture() {
        if (reader == null)
            throw new IllegalStateException("Prepare a capture first.");
        
        if (!reader.isFinished())
            throw new IllegalStateException("Previous reader is still active.");
    }
    
    @Override
    public KeyEventTimeline endCaptureAndYield() {
        reader.finish();
        Set<KeyEventTimeline.KeyEvent> events = new HashSet<>();
        for (String keyId : idHandlerMap.keySet()) {
            Map<Instant, KeyboardReader.EventType> idEvents = reader.getEventsForId(keyId);
            events.addAll(idEvents.keySet().stream().map(
                    i -> new KeyEventTimeline.KeyEvent(keyId, i, KeyEventLayerMapper.get(idEvents.get(i))
            )).toList());
        }
        return new KeyEventTimeline(events);
    }
    
    public static class KeyEventHandlers {
        private final Runnable onPress;
        private final Runnable onRelease;
        
        public KeyEventHandlers(Runnable onPress, Runnable onRelease) {
            this.onPress = onPress;
            this.onRelease = onRelease;
        }
        
        public Runnable getOnPress() {
            return onPress;
        }
        
        public Runnable getOnRelease() {
            return onRelease;
        }
    }
}
