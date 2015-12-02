package cicontest.torcs.controller.extras;

import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;


public class AutomatedGearbox
        implements IExtra {
    private static final long serialVersionUID = -567204826257927254L;
    final int[] gearUp = {5000, 6000, 6000, 6500, 7000, 0};
    final int[] gearDown = {0, 2500, 3000, 3000, 3500, 3500};

    public void process(Action action, SensorModel sensors) {
        int gear = sensors.getGear();
        double rpm = sensors.getRPM();

        action.gear = gear;


        if (gear < 1) {
            action.gear = 1;
            return;
        }


        if ((gear < 6) && (rpm >= this.gearUp[(gear - 1)])) {
            action.gear = (gear + 1);
            return;
        }


        if ((gear > 1) && (rpm <= this.gearDown[(gear - 1)])) {
            action.gear = (gear - 1);
        }
    }

    public void reset() {
    }
}