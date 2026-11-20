package alquimia.domain.services;

import alquimia.domain.exceptions.DeviceReportNotFoundException;
import alquimia.domain.models.DeviceReport;
import alquimia.domain.models.SurveillanceDevice;
import alquimia.domain.ports.in.ConsultDeviceReportsUseCase;
import alquimia.domain.ports.out.DeviceReportRepository;
import java.util.Comparator;
import java.util.List;

public class ConsultDeviceReportsService implements ConsultDeviceReportsUseCase {

    private final DeviceReportRepository repository;

    public ConsultDeviceReportsService(DeviceReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DeviceReport> findAll() {
        return repository.findAll().stream().sorted(Comparator.comparing(DeviceReport::device)).toList();
    }

    @Override
    public DeviceReport findByDevice(String device) {
        SurveillanceDevice target = SurveillanceDevice.from(device);
        return repository.findByDevice(target).orElseThrow(() -> notFound(target));
    }

    static DeviceReportNotFoundException notFound(SurveillanceDevice device) {
        return new DeviceReportNotFoundException("There is no stored information for " + device.displayName() + ".");
    }
}
