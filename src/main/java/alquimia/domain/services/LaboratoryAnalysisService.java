package alquimia.domain.services;

import alquimia.domain.exceptions.InsufficientDataException;
import alquimia.domain.models.DeviceReport;
import alquimia.domain.models.Distance;
import alquimia.domain.models.MessageFragment;
import alquimia.domain.models.Point;
import alquimia.domain.models.SurveillanceDevice;
import alquimia.domain.models.SurveillanceResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Combines the three reports: trilateration for the position and merge for the message. */
public class LaboratoryAnalysisService {

    private final TrilaterationService trilateration;
    private final MessageReconstructionService reconstruction;

    public LaboratoryAnalysisService(TrilaterationService trilateration, MessageReconstructionService reconstruction) {
        this.trilateration = trilateration;
        this.reconstruction = reconstruction;
    }

    public SurveillanceResult analyze(Collection<DeviceReport> reports) {
        Map<SurveillanceDevice, Distance> distances = new EnumMap<>(SurveillanceDevice.class);
        List<MessageFragment> fragments = new ArrayList<>();
        for (DeviceReport report : reports) {
            distances.put(report.device(), report.distance());
            fragments.add(report.message());
        }
        if (distances.size() < SurveillanceDevice.values().length) {
            throw new InsufficientDataException(
                    "There is not enough information: " + distances.size() + " of 3 devices have reported.");
        }
        Point position = trilateration.locate(distances);
        String message = reconstruction.reconstruct(fragments);
        return new SurveillanceResult(position, message);
    }
}
