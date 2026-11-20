package alquimia.adapters.rest;

import alquimia.adapters.rest.dto.CompleteInformationRequest;
import alquimia.adapters.rest.dto.DeviceReportResponse;
import alquimia.adapters.rest.dto.LaboratoryResponse;
import alquimia.adapters.rest.dto.PositionResponse;
import alquimia.domain.models.DeviceReport;
import alquimia.domain.models.SurveillanceResult;
import alquimia.domain.ports.in.DeviceReportCommand;
import java.util.List;

/** Translates between the HTTP DTOs and the domain, so the domain never sees HTTP. */
final class RestMapper {

    private RestMapper() {
    }

    static List<DeviceReportCommand> toCommands(CompleteInformationRequest request) {
        return request.devices().stream()
                .map(d -> new DeviceReportCommand(d.name(), d.distance(), d.message()))
                .toList();
    }

    static DeviceReportResponse toResponse(DeviceReport report) {
        return new DeviceReportResponse(report.device().displayName(), report.distance().value(),
                report.message().words());
    }

    static LaboratoryResponse toResponse(SurveillanceResult result) {
        return new LaboratoryResponse(new PositionResponse(result.position().x(), result.position().y()),
                result.message());
    }
}
