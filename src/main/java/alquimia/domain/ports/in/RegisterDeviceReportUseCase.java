package alquimia.domain.ports.in;

import alquimia.domain.models.DeviceReport;

/** 2.2 Registers the information of one device. */
public interface RegisterDeviceReportUseCase {
    DeviceReport register(DeviceReportCommand command);
}
