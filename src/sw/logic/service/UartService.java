package sw.logic.service;

import sw.data.uart.Logic2UartReader;
import sw.data.uart.UartByte;
import sw.data.uart.UartReader;
import sw.logic.object.TransmissionBlock;
import sw.logic.object.TransmissionBlockBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class UartService {
    UartReader uartReader;
    
    public void startCapture() {
        uartReader = new Logic2UartReader();
        uartReader.startCapture();
    }
    
    public Map<String, List<TransmissionBlock>> endCaptureAndYield(long blockTimeoutNs) {
        if (uartReader == null)
            throw new IllegalStateException("Has to start reader first.");
        uartReader.endCapture();
        
        return generateTransmissionBlockLists(blockTimeoutNs, uartReader.getData());
    }
    
    private Map<String, List<TransmissionBlock>> generateTransmissionBlockLists(long blockTimeoutNs, Collection<UartByte> uartBytes) {
        List<UartByte> sortedData = uartBytes.stream().sorted().toList();
        
        Map<String, List<TransmissionBlock>> result = new HashMap<>();
        
        Map<String, TransmissionBlockBuilder> builderMap = new HashMap<>();
        for (UartByte ub : sortedData) {
            if (ub.isError())
                continue;
            
            String analyzerId = ub.getAnalyzerId();
            if (!builderMap.containsKey(analyzerId)) {
                TransmissionBlockBuilder newBuilder = new TransmissionBlockBuilder().add(ub);
                builderMap.put(analyzerId, newBuilder);
                continue;
            }
            
            Instant latestTimestamp = builderMap.get(analyzerId).getTimestampOfLatestAddition();
            if (latestTimestamp != null && Duration.between(latestTimestamp, ub.getTimestamp()).toNanos() > blockTimeoutNs) {
                if (!result.containsKey(analyzerId))
                    result.put(analyzerId, new ArrayList<>());
                result.get(analyzerId).add(builderMap.get(analyzerId).build());
                TransmissionBlockBuilder newBuilder = new TransmissionBlockBuilder().add(ub);
                builderMap.put(analyzerId, newBuilder);
                continue;
            }
            
            builderMap.get(analyzerId).add(ub);
        }
        
        // Final loop for the unfinalized builders
        for (String analyzerId : builderMap.keySet()) {
            TransmissionBlockBuilder builder = builderMap.get(analyzerId);
            if (builder == null)
                continue;
            if (!result.containsKey(analyzerId))
                result.put(analyzerId, new ArrayList<>());
            result.get(analyzerId).add(builder.build());
        }
        return result;
    }
}
