package cicontest.algorithm.abstracts.map;

public class Corner {
    public double location;
    public double length;

    public Corner(double start, double length, double sharpness, double distance, int index) {
        this.location = start;
        this.distance = distance;
        this.length = length;
        this.sharpness = sharpness;
        this.index = index;
    }

    public double sharpness;
    public double distance;
    public int index;
}