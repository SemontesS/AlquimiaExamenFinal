package alquimia.infrastructure.config;

import alquimia.adapters.persistence.InMemoryDeviceReportRepository;
import alquimia.domain.ports.in.ConsultDeviceReportsUseCase;
import alquimia.domain.ports.in.DeleteDeviceReportUseCase;
import alquimia.domain.ports.in.LocateLaboratoryUseCase;
import alquimia.domain.ports.in.ProcessCompleteInformationUseCase;
import alquimia.domain.ports.in.RegisterDeviceReportUseCase;
import alquimia.domain.ports.in.ReplaceDeviceReportUseCase;
import alquimia.domain.ports.in.UpdateDeviceReportUseCase;
import alquimia.domain.ports.out.DeviceReportRepository;
import alquimia.domain.services.ConsultDeviceReportsService;
import alquimia.domain.services.DeleteDeviceReportService;
import alquimia.domain.services.LaboratoryAnalysisService;
import alquimia.domain.services.LocateLaboratoryService;
import alquimia.domain.services.MessageReconstructionService;
import alquimia.domain.services.ProcessCompleteInformationService;
import alquimia.domain.services.RegisterDeviceReportService;
import alquimia.domain.services.ReplaceDeviceReportService;
import alquimia.domain.services.TrilaterationService;
import alquimia.domain.services.UpdateDeviceReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** The only place where the pure domain is wired to Spring. The domain classes carry no annotations. */
@Configuration
public class BeanConfig {

    @Bean
    DeviceReportRepository deviceReportRepository() {
        return new InMemoryDeviceReportRepository();
    }

    @Bean
    LaboratoryAnalysisService laboratoryAnalysisService() {
        return new LaboratoryAnalysisService(new TrilaterationService(), new MessageReconstructionService());
    }

    @Bean
    ProcessCompleteInformationUseCase processCompleteInformationUseCase(LaboratoryAnalysisService analysis) {
        return new ProcessCompleteInformationService(analysis);
    }

    @Bean
    RegisterDeviceReportUseCase registerDeviceReportUseCase(DeviceReportRepository repository) {
        return new RegisterDeviceReportService(repository);
    }

    @Bean
    ConsultDeviceReportsUseCase consultDeviceReportsUseCase(DeviceReportRepository repository) {
        return new ConsultDeviceReportsService(repository);
    }

    @Bean
    LocateLaboratoryUseCase locateLaboratoryUseCase(DeviceReportRepository repository,
                                                    LaboratoryAnalysisService analysis) {
        return new LocateLaboratoryService(repository, analysis);
    }

    @Bean
    ReplaceDeviceReportUseCase replaceDeviceReportUseCase(DeviceReportRepository repository) {
        return new ReplaceDeviceReportService(repository);
    }

    @Bean
    UpdateDeviceReportUseCase updateDeviceReportUseCase(DeviceReportRepository repository) {
        return new UpdateDeviceReportService(repository);
    }

    @Bean
    DeleteDeviceReportUseCase deleteDeviceReportUseCase(DeviceReportRepository repository) {
        return new DeleteDeviceReportService(repository);
    }
}
