package alquimia.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import alquimia.domain.models.Distance;
import alquimia.domain.models.Point;
import alquimia.domain.models.SurveillanceDevice;
import alquimia.domain.services.TrilaterationService;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TrilaterationServiceTest {

    private final TrilaterationService service = new TrilaterationService();

    private static Map<SurveillanceDevice, Distance> distancesTo(double x, double y) {
        Map<SurveillanceDevice, Distance> map = new EnumMap<>(SurveillanceDevice.class);
        for (SurveillanceDevice device : SurveillanceDevice.values()) {
            map.put(device, new Distance(Math.hypot(device.position().x() - x, device.position().y() - y)));
        }
        return map;
    }

    @Test
    void findsTheLaboratoryFromExactDistances() {
        assertEquals(new Point(-58.5, 112.25), service.locate(distancesTo(-58.5, 112.25)));
        assertEquals(new Point(0, 0), service.locate(distancesTo(0, 0)));
        assertEquals(new Point(300, -400), service.locate(distancesTo(300, -400)));
    }

    @Test
    void worksWhenTheLaboratoryIsOnTopOfADevice() {
        assertEquals(new Point(100, -100), service.locate(distancesTo(100, -100)));
    }
}
