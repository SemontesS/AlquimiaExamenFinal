package alquimia.domain.ports.out;

import alquimia.domain.models.DeviceReport;
import alquimia.domain.models.SurveillanceDevice;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

/** Temporary storage of the device reports. Operations are atomic per device. */
public interface DeviceReportRepository {

    /** Stores the report only if the device has none. Returns false if it already had one. */
    boolean saveIfAbsent(DeviceReport report);

    /** Applies the change to the stored report, if any, and returns the new value. */
    Optional<DeviceReport> update(SurveillanceDevice device, UnaryOperator<DeviceReport> change);

    Optional<DeviceReport> findByDevice(SurveillanceDevice device);

    List<DeviceReport> findAll();

    /** Returns true if there was something to delete. */
    boolean deleteByDevice(SurveillanceDevice device);
}
