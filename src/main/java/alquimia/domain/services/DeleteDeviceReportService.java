package alquimia.domain.services;

import alquimia.domain.models.SurveillanceDevice;
import alquimia.domain.ports.in.DeleteDeviceReportUseCase;
import alquimia.domain.ports.out.DeviceReportRepository;

/** 2.6 Deletes only the selected device. */
public class DeleteDeviceReportService implements DeleteDeviceReportUseCase {

    private final DeviceReportRepository repository;

    public DeleteDeviceReportService(DeviceReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public void delete(String device) {
        SurveillanceDevice target = SurveillanceDevice.from(device);
        if (!repository.deleteByDevice(target)) {
            throw ConsultDeviceReportsService.notFound(target);
        }
    }
}
