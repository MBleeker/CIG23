package cicontest.torcs.controller.extras;

import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;

public class ABS
        implements IExtra {
    private static final long serialVersionUID = -1785223092317542722L;
    final float[] wheelRadius = {0.3179F, 0.3179F, 0.3276F, 0.3276F};

    final float absSlip = 2.0F;
    final float absRange = 3.0F;
    final float absMinSpeed = 3.0F;


    public void process(Action action, SensorModel sensors) {
        float speed = (float) (sensors.getSpeed() / 3.6D);

        if (speed < 3.0F) {
            return;
        }

        float slip = 0.0F;
        for (int i = 0; i < 4; i++) {
            slip = (float) (slip + sensors.getWheelSpinVelocity()[i] * this.wheelRadius[i]);
        }


        slip = speed - slip / 4.0F;

        if (slip > 2.0F) {
            action.brake -= (slip - 2.0F) / 3.0F;
        }


        if (action.brake < 0.0D) {
            action.brake = 0.0D;
        }
    }

    public void reset() {
    }
}




