package alquimia.domain.ports.in;

import alquimia.domain.models.SurveillanceResult;

/** 2.3 Processes the accumulated information. Fails with InsufficientDataException if it is not enough. */
public interface LocateLaboratoryUseCase {
    SurveillanceResult locate();
}
