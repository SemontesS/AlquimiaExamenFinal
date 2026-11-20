package alquimia.domain.ports.in;

import alquimia.domain.models.DeviceReport;
import alquimia.domain.models.Distance;
import alquimia.domain.models.MessageFragment;
import alquimia.domain.models.SurveillanceDevice;
import java.util.List;

/** Raw data of a device report. A null distance or message means "not provided". */
public record DeviceReportCommand(String device, Double distance, List<String> message) {

    /** Builds a complete report: distance and message are mandatory. */
    public DeviceReport toReport() {
        return new DeviceReport(SurveillanceDevice.from(device), Distance.of(distance), MessageFragment.of(message));
    }
}
