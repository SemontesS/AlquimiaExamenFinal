package alquimia.domain.services;

import alquimia.domain.exceptions.InsufficientDataException;
import alquimia.domain.exceptions.InvalidDeviceDataException;
import alquimia.domain.models.Distance;
import alquimia.domain.models.Point;
import alquimia.domain.models.SurveillanceDevice;
import java.util.Map;

/**
 * Finds the laboratory from its distance to the three devices.
 *
 * <p>Subtracting the circle equations pairwise gives two linear equations in (x, y):
 * <pre>
 *   A x + B y = C      A = 2(x2-x1)  B = 2(y2-y1)  C = r1²-r2²-x1²+x2²-y1²+y2²
 *   D x + E y = F      D = 2(x3-x2)  E = 2(y3-y2)  F = r2²-r3²-x2²+x3²-y2²+y3²
 * </pre>
 * The devices are not collinear, so the determinant is never 0.
 */
public class TrilaterationService {

    public Point locate(Map<SurveillanceDevice, Distance> distances) {
        for (SurveillanceDevice device : SurveillanceDevice.values()) {
            if (distances.get(device) == null) {
                throw new InsufficientDataException(
                        "There is not enough information: the distance of " + device.displayName() + " is missing.");
            }
        }
        Point p1 = SurveillanceDevice.STAR_LABS.position();
        Point p2 = SurveillanceDevice.CCPD.position();
        Point p3 = SurveillanceDevice.STAR_SATELLITE.position();
        double r1 = distances.get(SurveillanceDevice.STAR_LABS).value();
        double r2 = distances.get(SurveillanceDevice.CCPD).value();
        double r3 = distances.get(SurveillanceDevice.STAR_SATELLITE).value();

        double a = 2 * (p2.x() - p1.x());
        double b = 2 * (p2.y() - p1.y());
        double c = r1 * r1 - r2 * r2 - p1.x() * p1.x() + p2.x() * p2.x() - p1.y() * p1.y() + p2.y() * p2.y();
        double d = 2 * (p3.x() - p2.x());
        double e = 2 * (p3.y() - p2.y());
        double f = r2 * r2 - r3 * r3 - p2.x() * p2.x() + p3.x() * p3.x() - p2.y() * p2.y() + p3.y() * p3.y();

        double determinant = e * a - b * d;
        if (Math.abs(determinant) < 1e-9) {
            throw new InvalidDeviceDataException("The devices are aligned: the position cannot be calculated.");
        }
        double x = (c * e - f * b) / determinant;
        double y = (a * f - d * c) / determinant;
        return new Point(x, y).rounded();
    }
}
