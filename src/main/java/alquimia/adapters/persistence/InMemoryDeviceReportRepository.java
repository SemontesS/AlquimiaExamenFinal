package alquimia.adapters.persistence;

import alquimia.domain.models.DeviceReport;
import alquimia.domain.models.SurveillanceDevice;
import alquimia.domain.ports.out.DeviceReportRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

/** Output adapter: keeps the reports in memory only. Nothing survives a restart, as required. */
public class InMemoryDeviceReportRepository implements DeviceReportRepository {

    private final Map<SurveillanceDevice, DeviceReport> store = new ConcurrentHashMap<>();

    @Override
    public boolean saveIfAbsent(DeviceReport report) {
        return store.putIfAbsent(report.device(), report) == null;
    }

    @Override
    public Optional<DeviceReport> update(SurveillanceDevice device, UnaryOperator<DeviceReport> change) {
        return Optional.ofNullable(store.computeIfPresent(device, (key, current) -> change.apply(current)));
    }

    @Override
    public Optional<DeviceReport> findByDevice(SurveillanceDevice device) {
        return Optional.ofNullable(store.get(device));
    }

    @Override
    public List<DeviceReport> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public boolean deleteByDevice(SurveillanceDevice device) {
        return store.remove(device) != null;
    }
}
