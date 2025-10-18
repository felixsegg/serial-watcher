package sw.logic.service;

import sw.data.uart.Logic2UartReader;
import sw.data.uart.UartByte;
import sw.data.uart.UartReader;
import sw.logic.object.TransmissionBlockTimeline;
import sw.logic.object.helper.TransmissionBlockBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class UartService implements CaptureService<TransmissionBlockTimeline> {
    private UartReader uartReader;
    private final long blockTimeoutNs;
    
    public UartService(long blockTimeoutNs) {
        this.blockTimeoutNs = blockTimeoutNs;
    }
    
    public void startCapture() {
        uartReader = new Logic2UartReader();
        uartReader.startCapture();
    }
    
    public TransmissionBlockTimeline endCaptureAndYield() {
        if (uartReader == null)
            throw new IllegalStateException("Has to start reader first.");
        
        uartReader.endCapture();
        
        TransmissionBlockBuilder builder = new TransmissionBlockBuilder();
        Set<TransmissionBlockTimeline.TransmissionBlock> transmissionBlocks = new HashSet<>();
        for (UartByte ub : uartReader.getData().stream().sorted().toList()) {
            Instant latestTimestamp = builder.getTimestampOfLatestAddition(ub.getAnalyzerId());
            if (latestTimestamp != null && Duration.between(latestTimestamp, ub.getTimestamp()).toNanos() > blockTimeoutNs)
                transmissionBlocks.add(builder.finish(ub.getAnalyzerId()));
            builder.add(ub);
        }
        transmissionBlocks.addAll(builder.finishAll());
        
        return new TransmissionBlockTimeline(transmissionBlocks);
    }
    
    public long getBlockTimeoutNs() {
        return blockTimeoutNs;
    }
}
