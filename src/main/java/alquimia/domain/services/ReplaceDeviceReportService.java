package alquimia.domain.services;

import alquimia.domain.models.DeviceReport;
import alquimia.domain.models.Distance;
import alquimia.domain.models.MessageFragment;
import alquimia.domain.models.SurveillanceDevice;
import alquimia.domain.ports.in.DeviceReportCommand;
import alquimia.domain.ports.in.ReplaceDeviceReportUseCase;
import alquimia.domain.ports.out.DeviceReportRepository;

/** 2.4 Full replacement: distance and message are both required. */
public class ReplaceDeviceReportService implements ReplaceDeviceReportUseCase {

    private final DeviceReportRepository repository;

    public ReplaceDeviceReportService(DeviceReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeviceReport replace(DeviceReportCommand command) {
        SurveillanceDevice device = SurveillanceDevice.from(command.device());
        Distance distance = Distance.of(command.distance());
        MessageFragment message = MessageFragment.of(command.message());
        return repository.update(device, current -> current.replaceWith(distance, message))
                .orElseThrow(() -> ConsultDeviceReportsService.notFound(device));
    }
}
