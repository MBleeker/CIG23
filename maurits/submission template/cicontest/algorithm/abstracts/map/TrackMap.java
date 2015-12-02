package cicontest.algorithm.abstracts.map;

import cicontest.torcs.client.MemoryContainer;
import cicontest.torcs.controller.Driver;

import java.io.Serializable;
import java.util.ArrayList;

public class TrackMap
        implements Serializable {
    private static final long serialVersionUID = -8402954839547699633L;
    private String trackName;
    private double length;
    private double width;
    private ArrayList<Double> corner_start;
    private ArrayList<Double> corner_length;
    private ArrayList<Double> corner_sharpness;


    /**
     * @param trackName
     * @TODO oussama Changed Nan Things here.
     */
    public TrackMap(String trackName) {
        this.trackName = trackName;
        this.corner_start = new ArrayList();
        this.corner_length = new ArrayList();
        this.corner_sharpness = new ArrayList();
        this.length = Double.NaN;
        this.width = Double.NaN;
    }

    public String getTrackName() {
        return this.trackName;
    }

    public double getLength() {
        return this.length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void addCorner(double start, double length, double sharpness) {
        if ((this.corner_start.size() > 0) &&
                (((Double) this.corner_start.get(this.corner_start.size() - 1)).doubleValue() + ((Double) this.corner_length.get(this.corner_start.size() - 1)).doubleValue() + 30.0D > start) &&
                (Math.signum(((Double) this.corner_sharpness.get(this.corner_start.size() - 1)).doubleValue()) == Math.signum(sharpness))) {
            this.corner_length.set(this.corner_start.size() - 1, Double.valueOf(start + length - ((Double) this.corner_start.get(this.corner_start.size() - 1)).doubleValue()));
            this.corner_sharpness.set(this.corner_start.size() - 1, Double.valueOf(((Double) this.corner_sharpness.get(this.corner_start.size() - 1)).doubleValue() + sharpness));
            System.out.println("Combined corners to a single corner");
            return;
        }


        this.corner_start.add(Double.valueOf(start));
        this.corner_length.add(Double.valueOf(length));
        this.corner_sharpness.add(Double.valueOf(sharpness));
    }

    /**
     * @TODO oussama Changed Nan Things here.
     */
    public Corner getCorner(int index) {
        return new Corner(((Double) this.corner_start.get(index)).doubleValue(), ((Double) this.corner_length.get(index)).doubleValue(), ((Double) this.corner_sharpness.get(index)).doubleValue(), Double.NaN, index);
    }

    public Corner getCorner(int index, double position) {
        double distance = ((Double) this.corner_start.get(index)).doubleValue() - position;
        if (distance < 0.0D) {
            if (position < ((Double) this.corner_start.get(index)).doubleValue() + ((Double) this.corner_length.get(index)).doubleValue()) {
                distance = 0.0D;
            } else {
                distance = this.length - position + ((Double) this.corner_start.get(index)).doubleValue();
            }
        }


        return new Corner(((Double) this.corner_start.get(index)).doubleValue(), ((Double) this.corner_length.get(index)).doubleValue(), ((Double) this.corner_sharpness.get(index)).doubleValue(), distance, index);
    }

    public Corner getNextCorner(double position) {
        return getCorner(getNextCornerIndex(position), position);
    }

    public int getCornerCount() {
        return this.corner_start.size();
    }

    public int getNextCornerIndex(double position) {
        int index = 0;

        while ((index < this.corner_start.size()) && (((Double) this.corner_start.get(index)).doubleValue() < position) &&
                (position >= ((Double) this.corner_start.get(index)).doubleValue() + ((Double) this.corner_length.get(index)).doubleValue())) {

            index++;
        }

        if (index == this.corner_start.size()) {
            return 0;
        }

        return index;
    }

    public String toString() {
        String tostr = "Trackmap: " + getTrackName() + " (" + Math.ceil(this.length) + "m)" + System.getProperty("line.separator");
        for (int i = 0; i < getCornerCount(); i++) {
            tostr = tostr + "[" + this.corner_start.get(i) + ", " + this.corner_length.get(i) + ", " + this.corner_sharpness.get(i) + "]" + System.getProperty("line.separator");
        }
        return tostr;
    }

    public void store(Driver driver) {
        MemoryContainer.getInstance(driver.getClass()).putMemory("TrackMap_" + getTrackName(), this);
        MemoryContainer.getInstance(driver.getClass()).store();
    }

    public static TrackMap getMap(Driver driver, String track) {
        Serializable s = MemoryContainer.getInstance(driver.getClass()).getMemory("TrackMap_" + track);
        if (s != null) {
            return (TrackMap) s;
        }
        return new TrackMap(track);
    }

    public static boolean hasMap(Driver driver, String track) {
        Serializable s = MemoryContainer.getInstance(driver.getClass()).getMemory("TrackMap_" + track);
        if (s != null) {
            return true;
        }
        return false;
    }

    public static void removeMap(Driver driver, String track) {
        Serializable s = MemoryContainer.getInstance(driver.getClass()).getMemory("TrackMap_" + track);
        if (s == null) {
            return;
        }
        MemoryContainer.getInstance(driver.getClass()).clearMemory("TrackMap_" + track);
    }

    public void clear() {
        this.corner_start.clear();
        this.corner_sharpness.clear();
        this.corner_length.clear();
    }
}