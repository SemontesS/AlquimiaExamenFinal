package alquimia.domain.models;

import alquimia.domain.exceptions.UnknownDeviceException;
import java.util.Locale;

/** The three surveillance devices of Central City and their fixed positions. */
public enum SurveillanceDevice {
    STAR_LABS("STAR Labs", new Point(-500, -200)),
    CCPD("CCPD", new Point(100, -100)),
    STAR_SATELLITE("S.T.A.R. Satellite", new Point(500, 100));

    private final String displayName;
    private final Point position;

    SurveillanceDevice(String displayName, Point position) {
        this.displayName = displayName;
        this.position = position;
    }

    public String displayName() {
        return displayName;
    }

    public Point position() {
        return position;
    }

    /**
     * Resolves a device from free text. Case, spaces, dots, dashes and underscores are ignored, so
     * "STAR Labs", "star-labs" and "STAR_LABS" are the same device.
     */
    public static SurveillanceDevice from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new UnknownDeviceException("The device name is required.");
        }
        String key = normalize(raw);
        for (SurveillanceDevice device : values()) {
            if (normalize(device.name()).equals(key) || normalize(device.displayName).equals(key)) {
                return device;
            }
        }
        throw new UnknownDeviceException("Unknown surveillance device: '" + raw + "'.");
    }

    private static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
