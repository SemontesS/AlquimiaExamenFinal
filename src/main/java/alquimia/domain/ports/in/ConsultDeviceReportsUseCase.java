package alquimia.domain.ports.in;

import alquimia.domain.models.DeviceReport;
import java.util.List;

/** Consults the information stored per device. */
public interface ConsultDeviceReportsUseCase {
    List<DeviceReport> findAll();

    DeviceReport findByDevice(String device);
}
