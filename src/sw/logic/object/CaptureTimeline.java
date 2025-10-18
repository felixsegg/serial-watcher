package sw.logic.object;

import java.time.Instant;
import java.util.Collection;

public interface CaptureTimeline<T extends CaptureTimeline.TimelineEvent> {
    Collection<T> getEvents();
    
    interface TimelineEvent extends Comparable<TimelineEvent> {
        Instant getTimestamp();
        
        @Override
        default int compareTo(TimelineEvent o) {
            return getTimestamp().compareTo(o.getTimestamp());
        }
    }
}
