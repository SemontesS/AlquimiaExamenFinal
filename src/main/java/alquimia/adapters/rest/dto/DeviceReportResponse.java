package alquimia.adapters.rest.dto;

import java.util.List;

public record DeviceReportResponse(String device, double distance, List<String> message) {
}
