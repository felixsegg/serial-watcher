package sw.logic.object.helper;

import sw.data.uart.UartByte;
import sw.logic.object.TransmissionBlockTimeline;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class TransmissionBlockBuilder {
    private final Map<String, List<UartByte>> idDataMap = new HashMap<>();
    
    public TransmissionBlockBuilder add(UartByte uartByte) {
        String analyzerId = uartByte.getAnalyzerId();
        idDataMap.putIfAbsent(analyzerId, new ArrayList<>());
        idDataMap.get(analyzerId).add(uartByte);
        return this;
    }
    
    public TransmissionBlockTimeline.TransmissionBlock finish(String analyzerId) {
        if (!idDataMap.containsKey(analyzerId))
            throw new IllegalArgumentException();
        return new TransmissionBlockTimeline.TransmissionBlock(analyzerId, idDataMap.remove(analyzerId));
    }
    
    public Instant getTimestampOfLatestAddition(String analyzerId) {
        if (!idDataMap.containsKey(analyzerId))
            return null;
        return idDataMap.get(analyzerId).getLast().getTimestamp();
    }
    
    public Set<TransmissionBlockTimeline.TransmissionBlock> finishAll() {
        return idDataMap.keySet().stream().map(this::finish).collect(Collectors.toSet());
    }
}
