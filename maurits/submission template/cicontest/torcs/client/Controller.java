package cicontest.torcs.client;

public abstract class Controller {
    private Stage stage;
    private String trackName;

    public static enum Stage {
        PRACTICE, QUALIFICATIONS, RACE;

        private Stage() {
        }

        static Stage fromInt(int value) {
            switch (value) {
                case 0:
                    return PRACTICE;
                case 1:
                    return QUALIFICATIONS;
            }
            return RACE;
        }
    }


    public float[] initAngles() {
        float[] angles = new float[19];
        for (int i = 0; i < 19; i++)
            angles[i] = (-90 + i * 10);
        return angles;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getTrackName() {
        return this.trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public abstract Action determineAction(SensorModel paramSensorModel);

    public abstract void reset();

    public abstract void shutdown();
}