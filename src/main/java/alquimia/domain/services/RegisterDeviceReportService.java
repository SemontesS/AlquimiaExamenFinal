package alquimia.domain.services;

import alquimia.domain.exceptions.DuplicateDeviceReportException;
import alquimia.domain.models.DeviceReport;
import alquimia.domain.ports.in.DeviceReportCommand;
import alquimia.domain.ports.in.RegisterDeviceReportUseCase;
import alquimia.domain.ports.out.DeviceReportRepository;

/** 2.2 Registers the report of a device. A second registration must use replace or update. */
public class RegisterDeviceReportService implements RegisterDeviceReportUseCase {

    private final DeviceReportRepository repository;

    public RegisterDeviceReportService(DeviceReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeviceReport register(DeviceReportCommand command) {
        DeviceReport report = command.toReport();
        if (!repository.saveIfAbsent(report)) {
            throw new DuplicateDeviceReportException("The device " + report.device().displayName()
                    + " already has information. Use replace or update to change it.");
        }
        return report;
    }
}
