package alquimia.adapters.rest.dto;

import java.util.List;

/** Body of PATCH: send only what changes. */
public record PartialDeviceReportRequest(Double distance, List<String> message) {
}
