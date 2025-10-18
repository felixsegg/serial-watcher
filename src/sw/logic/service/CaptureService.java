package sw.logic.service;

import sw.logic.object.CaptureTimeline;

public interface CaptureService<CT extends CaptureTimeline<?>> {
    void startCapture();
    CT endCaptureAndYield();
}
