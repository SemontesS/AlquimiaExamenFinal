package alquimia.domain.models;

import alquimia.domain.exceptions.InvalidDeviceDataException;

/** What one device reported: its distance to the laboratory and its message fragment. Immutable. */
public record DeviceReport(SurveillanceDevice device, Distance distance, MessageFragment message) {

    public DeviceReport {
        if (device == null || distance == null || message == null) {
            throw new InvalidDeviceDataException("A device report needs the device, the distance and the message.");
        }
    }

    /** Full replacement: both values are new. */
    public DeviceReport replaceWith(Distance newDistance, MessageFragment newMessage) {
        return new DeviceReport(device, newDistance, newMessage);
    }

    /** Partial update: only the values that are not null change. */
    public DeviceReport updatedWith(Distance newDistance, MessageFragment newMessage) {
        if (newDistance == null && newMessage == null) {
            throw new InvalidDeviceDataException("Provide the distance and/or the message to update.");
        }
        return new DeviceReport(device,
                newDistance != null ? newDistance : distance,
                newMessage != null ? newMessage : message);
    }
}
