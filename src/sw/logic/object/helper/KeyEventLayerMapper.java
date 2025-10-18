package sw.logic.object.helper;

import sw.data.keyboard.KeyboardReader;
import sw.logic.object.KeyEventTimeline;


public class KeyEventLayerMapper {
    public static KeyEventTimeline.KeyEventType get(KeyboardReader.EventType et) {
        return switch (et) {
            case PRESS -> KeyEventTimeline.KeyEventType.PRESS;
            case RELEASE -> KeyEventTimeline.KeyEventType.RELEASE;
            case null -> throw new NullPointerException();
        };
    }
    
    public static KeyboardReader.EventType get( KeyEventTimeline.KeyEventType et) {
        return switch (et) {
            case PRESS -> KeyboardReader.EventType.PRESS;
            case RELEASE -> KeyboardReader.EventType.RELEASE;
            case null -> throw new NullPointerException();
        };
    }
}
