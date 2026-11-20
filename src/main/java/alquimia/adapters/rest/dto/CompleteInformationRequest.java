package alquimia.adapters.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CompleteInformationRequest(@NotNull List<@NotNull @Valid CompleteDeviceRequest> devices) {
}
