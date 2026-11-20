package alquimia.domain.services;

import alquimia.domain.models.DeviceReport;
import alquimia.domain.models.Distance;
import alquimia.domain.models.MessageFragment;
import alquimia.domain.models.SurveillanceDevice;
import alquimia.domain.ports.in.DeviceReportCommand;
import alquimia.domain.ports.in.UpdateDeviceReportUseCase;
import alquimia.domain.ports.out.DeviceReportRepository;

/** 2.5 Partial update: what is not provided is kept. */
public class UpdateDeviceReportService implements UpdateDeviceReportUseCase {

    private final DeviceReportRepository repository;

    public UpdateDeviceReportService(DeviceReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeviceReport update(DeviceReportCommand command) {
        SurveillanceDevice device = SurveillanceDevice.from(command.device());
        Distance distance = command.distance() == null ? null : Distance.of(command.distance());
        MessageFragment message = command.message() == null ? null : MessageFragment.of(command.message());
        return repository.update(device, current -> current.updatedWith(distance, message))
                .orElseThrow(() -> ConsultDeviceReportsService.notFound(device));
    }
}
