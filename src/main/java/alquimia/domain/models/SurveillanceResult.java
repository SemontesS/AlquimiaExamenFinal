package alquimia.domain.models;

/** Outcome of the analysis: where the laboratory is and what the message says. */
public record SurveillanceResult(Point position, String message) {
}
