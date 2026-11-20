package alquimia.adapters.rest.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Body of POST and PUT: both values are required. Lost words are sent as "". */
public record DeviceReportRequest(@NotNull Double distance, @NotNull List<String> message) {
}
