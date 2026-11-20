package alquimia.domain.services;

import alquimia.domain.models.SurveillanceResult;
import alquimia.domain.ports.in.LocateLaboratoryUseCase;
import alquimia.domain.ports.out.DeviceReportRepository;

/** 2.3 Processes what has been accumulated so far. */
public class LocateLaboratoryService implements LocateLaboratoryUseCase {

    private final DeviceReportRepository repository;
    private final LaboratoryAnalysisService analysis;

    public LocateLaboratoryService(DeviceReportRepository repository, LaboratoryAnalysisService analysis) {
        this.repository = repository;
        this.analysis = analysis;
    }

    @Override
    public SurveillanceResult locate() {
        return analysis.analyze(repository.findAll());
    }
}
