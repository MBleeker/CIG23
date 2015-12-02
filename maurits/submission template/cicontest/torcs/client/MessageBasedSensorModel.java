package cicontest.torcs.client;


public class MessageBasedSensorModel
        implements SensorModel {
    private MessageParser message;

    private float[] initAngles;

    public MessageBasedSensorModel(MessageParser message) {
        this.message = message;
    }

    public MessageBasedSensorModel(String strMessage) {
        this.message = new MessageParser(strMessage);
    }


    public int getMessageType() {
        Object d = this.message.getReading("message");
        if (d.equals("")) {
            return 0;
        }
        return (int) ((Double) d).doubleValue();
    }

    public double getSpeed() {
        return ((Double) this.message.getReading("speedX")).doubleValue();
    }

    public double getAngleToTrackAxis() {
        return ((Double) this.message.getReading("angle")).doubleValue();
    }

    public double[] getTrackEdgeSensors() {
        return (double[]) this.message.getReading("track");
    }

    public double[] getFocusSensors() {
        return (double[]) this.message.getReading("focus");
    }


    public int getGear() {
        return (int) ((Double) this.message.getReading("gear")).doubleValue();
    }

    public double[] getOpponentSensors() {
        return (double[]) this.message.getReading("opponents");
    }

    public int getRacePosition() {
        return (int) ((Double) this.message.getReading("racePos")).doubleValue();
    }

    public double getLateralSpeed() {
        return ((Double) this.message.getReading("speedY")).doubleValue();
    }

    public double getCurrentLapTime() {
        return ((Double) this.message.getReading("curLapTime")).doubleValue();
    }

    public double getDamage() {
        return ((Double) this.message.getReading("damage")).doubleValue();
    }

    public double getDistanceFromStartLine() {
        return ((Double) this.message.getReading("distFromStart")).doubleValue();
    }

    public double getDistanceRaced() {
        return ((Double) this.message.getReading("distRaced")).doubleValue();
    }

    public double getFuelLevel() {
        return ((Double) this.message.getReading("fuel")).doubleValue();
    }

    public double getLastLapTime() {
        return ((Double) this.message.getReading("lastLapTime")).doubleValue();
    }

    public double getRPM() {
        return ((Double) this.message.getReading("rpm")).doubleValue();
    }

    public double getTrackPosition() {
        return -1.0D * ((Double) this.message.getReading("trackPos")).doubleValue();
    }

    public double[] getWheelSpinVelocity() {
        return (double[]) this.message.getReading("wheelSpinVel");
    }

    public String getMessage() {
        return this.message.getMessage();
    }

    public double getZ() {
        return ((Double) this.message.getReading("z")).doubleValue();
    }

    public double getZSpeed() {
        return ((Double) this.message.getReading("speedZ")).doubleValue();
    }

    public float[] getInitialAngles() {
        return this.initAngles;
    }

    public void setInitialAngles(float[] initAngles) {
        this.initAngles = initAngles;
    }

    public int getLaps() {
        return (int) ((Double) this.message.getReading("laps")).doubleValue();
    }

    public double getTime() {
        if (((Double) this.message.getReading("time")).doubleValue() < 0.01D) {
            return Double.POSITIVE_INFINITY;
        }
        return ((Double) this.message.getReading("time")).doubleValue();
    }

    public boolean getIsFinished() {
        return (int) ((Double) this.message.getReading("state")).doubleValue() == 256;
    }

    public int getTicks() {
        return (int) ((Double) this.message.getReading("ticks")).doubleValue();
    }

    public double getBestLapTime() {
        if (((Double) this.message.getReading("bestLapTime")).doubleValue() < 0.01D) {
            return Double.POSITIVE_INFINITY;
        }
        return ((Double) this.message.getReading("bestLapTime")).doubleValue();
    }

    public boolean isValid() {
        return this.message.isValid();
    }
}