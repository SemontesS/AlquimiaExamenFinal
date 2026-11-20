package alquimia.domain.models;

/** A coordinate on the Central City map. */
public record Point(double x, double y) {

    /** Rounds both coordinates to two decimals (the precision of the distances received). */
    public Point rounded() {
        return new Point(round2(x), round2(y));
    }

    private static double round2(double value) {
        double r = Math.round(value * 100.0) / 100.0;
        return r == 0.0 ? 0.0 : r; // avoid "-0.0"
    }
}
