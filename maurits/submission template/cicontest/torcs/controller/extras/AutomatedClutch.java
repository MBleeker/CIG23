package cicontest.torcs.controller.extras;

import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;


public class AutomatedClutch
        implements IExtra {
    private static final long serialVersionUID = 3389167093377022894L;
    final float clutchMax = 0.5F;
    final float clutchDelta = 0.05F;
    final float clutchRange = 0.82F;
    final float clutchDeltaTime = 0.02F;
    final float clutchDeltaRaced = 10.0F;
    final float clutchDec = 0.01F;
    final float clutchMaxModifier = 1.3F;
    final float clutchMaxTime = 1.5F;

    public void process(Action action, SensorModel sensors) {
        double clutch = 0.0D;

        float maxClutch = 0.5F;


        if (sensors.getDistanceRaced() < 10.0D) {
            clutch = maxClutch;
        }

        if (clutch > 0.0D) {
            double delta = 0.05000000074505806D;
            if (sensors.getGear() < 2) {

                delta /= 2.0D;
                maxClutch *= 1.3F;
                if (sensors.getCurrentLapTime() < 1.5D) {
                    clutch = maxClutch;
                }
            }

            clutch = Math.min(maxClutch, clutch);


            if (clutch != maxClutch) {
                clutch -= delta;
                clutch = Math.max(0.0D, clutch);
            } else {
                clutch -= 0.009999999776482582D;
            }
        }
        action.clutch = clutch;
    }

    public void reset() {
    }
}





