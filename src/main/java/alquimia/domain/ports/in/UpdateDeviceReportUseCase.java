package alquimia.domain.ports.in;

import alquimia.domain.models.DeviceReport;

/** 2.5 Updates partially the information of a device. Only the provided values change. */
public interface UpdateDeviceReportUseCase {
    DeviceReport update(DeviceReportCommand command);
}
