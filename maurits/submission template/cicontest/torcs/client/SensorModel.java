package cicontest.torcs.client;

public interface SensorModel {
    public abstract double getSpeed();

    public abstract double getAngleToTrackAxis();

    public abstract double[] getTrackEdgeSensors();

    public abstract double[] getFocusSensors();

    public abstract double getTrackPosition();

    public abstract int getGear();

    public abstract double[] getOpponentSensors();

    public abstract int getRacePosition();

    public abstract double getLateralSpeed();

    public abstract double getCurrentLapTime();

    public abstract double getDamage();

    public abstract double getDistanceFromStartLine();

    public abstract double getDistanceRaced();

    public abstract double getFuelLevel();

    public abstract double getLastLapTime();

    public abstract double getRPM();

    public abstract double[] getWheelSpinVelocity();

    public abstract double getZSpeed();

    public abstract double getZ();

    public abstract String getMessage();

    public abstract float[] getInitialAngles();

    public abstract void setInitialAngles(float[] paramArrayOfFloat);
}