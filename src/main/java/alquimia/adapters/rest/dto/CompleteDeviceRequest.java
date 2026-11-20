package alquimia.adapters.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CompleteDeviceRequest(@NotBlank String name, @NotNull Double distance, @NotNull List<String> message) {
}
