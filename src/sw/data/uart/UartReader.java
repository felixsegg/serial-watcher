package sw.data.uart;

import java.util.Collection;

public interface UartReader {
    void startCapture();
    void endCapture();
    Collection<UartByte> getData();
}
