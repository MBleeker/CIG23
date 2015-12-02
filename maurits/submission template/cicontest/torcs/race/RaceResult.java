package cicontest.torcs.race;

import cicontest.torcs.controller.Driver;


public class RaceResult {

    private Driver driver;
    private int position = 0;
    private double time = 0.0D;
    private double distance = 0.0D;
    private int laps = 0;
    private double bestLapTime = Double.POSITIVE_INFINITY;
    private boolean finished = false;
    private int ticks = 0;
    private double lastlap;

    public Driver getDriver() {
        return driver;
    }

    public int getPosition() {
        return position;
    }

    public double getTime() {
        return time;
    }

    public double getDistance() {
        return distance;
    }

    public int getLaps() {
        return laps;
    }

    public double getBestLapTime() {
        return bestLapTime;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getTicks() {
        return ticks;
    }

    public double getLastlap() {
        return lastlap;
    }

    public String toString() {
        return null;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setLaps(int laps) {
        this.laps = laps;
    }

    public void setBestLapTime(double bestLapTime) {
        this.bestLapTime = bestLapTime;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public void setLastlap(double lastlap) {
        this.lastlap = lastlap;
    }
}