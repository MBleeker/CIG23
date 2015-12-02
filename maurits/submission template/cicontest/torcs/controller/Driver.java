package cicontest.torcs.controller;

import cicontest.torcs.client.Action;
import cicontest.torcs.client.Controller;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.controller.extras.IExtra;

import java.util.ArrayList;

public abstract class Driver extends Controller {
    private ArrayList<IExtra> extras = new ArrayList();

    public void enableExtras(IExtra extra) {
        if (!this.extras.contains(extra)) {
            this.extras.add(extra);
        }
    }

    public abstract void init();

    public abstract void exit();

    public abstract String getDriverName();

    public Action determineAction(SensorModel sensors) {
        sensors.setInitialAngles(initAngles());

        Action action = new Action();
        control(action, sensors);

        for (IExtra extra : this.extras) {
            extra.process(action, sensors);
        }

        return action;
    }

    public abstract void controlWarmUp(Action paramAction, SensorModel paramSensorModel);

    public abstract void controlQualification(Action paramAction, SensorModel paramSensorModel);

    public abstract void controlRace(Action paramAction, SensorModel paramSensorModel);

    public void control(Action action, SensorModel sensors) {
        if (getStage() == Controller.Stage.PRACTICE) {
            controlWarmUp(action, sensors);
        }
        if (getStage() == Controller.Stage.QUALIFICATIONS) {
            controlQualification(action, sensors);
        }
        if (getStage() == Controller.Stage.RACE) {
            controlRace(action, sensors);
        }
    }


    public void shutdown() {
    }


    public float[] initAngles() {
        float[] angles = new float[19];


        angles[0] = -90.0F;
        angles[1] = -60.0F;
        angles[2] = -40.0F;
        angles[3] = -30.0F;
        angles[4] = -25.0F;
        angles[5] = -20.0F;
        angles[6] = -15.0F;
        angles[7] = -10.0F;
        angles[8] = -5.0F;
        angles[9] = 0.0F;

        for (int i = 10; i < 19; i++) {
            angles[i] = (-1.0F * angles[(18 - i)]);
        }

        return angles;
    }

    public void reset() {
        for (IExtra extra : this.extras) {
            extra.reset();
        }
    }
}