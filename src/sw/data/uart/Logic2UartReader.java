package sw.data.uart;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import saleae.Device;
import saleae.RadixType;
import xyz.froud.saleae.automation.Capture;
import xyz.froud.saleae.automation.Manager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Logic2UartReader implements UartReader {
    private static final Manager l2Manager;
    
    private Capture activeCapture;
    
    static {
        try {
            l2Manager = new Manager();
        } catch (Manager.IncompatibleApiVersionException e) {
            throw new RuntimeException(e); // TODO: Log this or something
        }
    }
    
    @Override
    public void startCapture() {
        if (activeCapture != null)
            throw new IllegalStateException("Can't start another capture.");
        
        String deviceId = getDeviceIds().stream().findFirst().orElse(null);
        if (deviceId == null)
            throw new RuntimeException("Could not find any devices");
        
        final Manager.DeviceConfig deviceConfig = new Manager.DeviceConfig();
        deviceConfig.digitalChannels = List.of(0, 1);
        deviceConfig.digitalSampleRate = 1_000_000;
        
        final Manager.CaptureConfigManual captureConfig = new Manager.CaptureConfigManual();
        
        activeCapture = l2Manager.startCapture("todo", deviceConfig, captureConfig);
    }
    
    @Override
    public void endCapture() {
        if (activeCapture == null)
            throw new IllegalStateException("Capture hasn't started yet.");
        activeCapture.stop();
    }
    
    @Override
    public Set<UartByte> getData() {
        
        Capture.AnalyzerSettings as0 = new Capture.AnalyzerSettings();
        Capture.AnalyzerSettings as1 = new Capture.AnalyzerSettings();
        as0.put("Input Channel", 0);
        as1.put("Input Channel", 1);
        
        Capture.AnalyzerHandle h0 = activeCapture.addAnalyzer("Async Serial", "CH0", as0);
        Capture.AnalyzerHandle h1 = activeCapture.addAnalyzer("Async Serial", "CH1", as1);
        
        List<Capture.DataTableAnalyzerConfig> analyzers = List.of(
                new Capture.DataTableAnalyzerConfig(h0, RadixType.RADIX_TYPE_HEXADECIMAL),
                new Capture.DataTableAnalyzerConfig(h1, RadixType.RADIX_TYPE_HEXADECIMAL)
        );
        activeCapture.exportDataTableCsv(getTempFile().getAbsolutePath(), analyzers, true, List.of(), new Capture.DataTableFilterWrapper("", List.of()));
        
        Set<UartByte> data = new HashSet<>();
        
        try (CSVReader reader = new CSVReader(new FileReader(getTempFile()))) {
            String[] header = reader.readNext();
            int idxName = Arrays.asList(header).indexOf("name");
            int idxStartTime = Arrays.asList(header).indexOf("start_time");
            int idxData = Arrays.asList(header).indexOf("\"data\"");
            int idxError = Arrays.asList(header).indexOf("\"error\"");
            
            for (String[] row : reader.readAll())
                data.add(new UartByte(UByte.fromHex(row[idxData]), Instant.parse(row[idxStartTime]), row[idxName], !row[idxError].isBlank()));
            
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e); // TODO: Log or something
        } finally {
            getTempFile().delete();
        }
        return data;
    }
    
    public static List<String> getDeviceIds() {
        return l2Manager.getDevices(false).stream().map(Device::getDeviceId).toList();
    }
    
    private File getTempFile() {
        File file = new File(System.getProperty("user.dir") + "\\temp", "temp.csv");
        if (!file.getParentFile().exists()) {
            boolean created = file.getParentFile().mkdirs();
            if (!created) {
                return null; // TODO: Maybe log this
            }
        }
        return file;
    }
}
