package alquimia.domain.services;

import alquimia.domain.exceptions.InvalidDeviceDataException;
import alquimia.domain.models.DeviceReport;
import alquimia.domain.models.SurveillanceResult;
import alquimia.domain.ports.in.DeviceReportCommand;
import alquimia.domain.ports.in.ProcessCompleteInformationUseCase;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import alquimia.domain.models.SurveillanceDevice;

/** 2.1 Stateless: nothing is stored. */
public class ProcessCompleteInformationService implements ProcessCompleteInformationUseCase {

    private final LaboratoryAnalysisService analysis;

    public ProcessCompleteInformationService(LaboratoryAnalysisService analysis) {
        this.analysis = analysis;
    }

    @Override
    public SurveillanceResult process(List<DeviceReportCommand> commands) {
        if (commands == null || commands.size() != SurveillanceDevice.values().length) {
            throw new InvalidDeviceDataException("Exactly the information of the 3 devices is required.");
        }
        List<DeviceReport> reports = new ArrayList<>();
        Set<SurveillanceDevice> seen = EnumSet.noneOf(SurveillanceDevice.class);
        for (DeviceReportCommand command : commands) {
            if (command == null) {
                throw new InvalidDeviceDataException("A device entry is empty.");
            }
            DeviceReport report = command.toReport();
            if (!seen.add(report.device())) {
                throw new InvalidDeviceDataException("The device " + report.device().displayName() + " is repeated.");
            }
            reports.add(report);
        }
        return analysis.analyze(reports);
    }
}
