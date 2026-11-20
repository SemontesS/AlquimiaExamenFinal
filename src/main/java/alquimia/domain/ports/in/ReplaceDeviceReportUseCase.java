package alquimia.domain.ports.in;

import alquimia.domain.models.DeviceReport;

/** 2.4 Replaces completely the information of a device. */
public interface ReplaceDeviceReportUseCase {
    DeviceReport replace(DeviceReportCommand command);
}
