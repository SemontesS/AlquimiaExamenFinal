package alquimia.domain.ports.in;

import alquimia.domain.models.SurveillanceResult;
import java.util.List;

/** 2.1 Receives the three devices at once and returns the laboratory position and the message. */
public interface ProcessCompleteInformationUseCase {
    SurveillanceResult process(List<DeviceReportCommand> reports);
}
