package alquimia.domain.models;

import alquimia.domain.exceptions.InvalidDeviceDataException;

/** Distance from a device to the laboratory. Must be a finite number greater than or equal to 0. */
public record Distance(double value) {

    public Distance {
        if (Double.isNaN(value) || Double.isInfinite(value) || value < 0) {
            throw new InvalidDeviceDataException("The distance must be a finite number greater than or equal to 0.");
        }
    }

    public static Distance of(Double value) {
        if (value == null) {
            throw new InvalidDeviceDataException("The distance is required.");
        }
        return new Distance(value);
    }
}
