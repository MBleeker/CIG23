//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.controller.extras.IExtra;

public class AutoRecover implements IExtra {
    private static final long serialVersionUID = -2521301170636027868L;
    final int stuckTime = 25;
    final float stuckAngle = 0.5235988F;
    final float steerLock = 0.785398F;
    private int stuck = 0;
    private int stuckstill = 0;

    public int getStuck() {
        return stuck;
    }

    public AutoRecover() {
    }

    public void process(Action action, SensorModel sensors) {
        if(sensors.getSpeed() < 5.0D && sensors.getDistanceFromStartLine() > 0.0D) {
            ++this.stuckstill;
        }

        if(Math.abs(sensors.getAngleToTrackAxis()) > 0.5235987901687622D) {
            if(this.stuck > 0 || Math.abs(sensors.getTrackPosition()) > 0.85D) {
                ++this.stuck;
            }
        } else if(this.stuck > 0 && Math.abs(sensors.getAngleToTrackAxis()) < 0.3D) {
            this.stuck = 0;
            this.stuckstill = 0;
        }

        if(this.stuckstill > 50) {
            this.stuck = 11;
        }

        if(this.stuck > 10) {
            float steer = (float)(sensors.getAngleToTrackAxis() / 0.785398006439209D);
            byte gear = -1;
            if(sensors.getAngleToTrackAxis() * sensors.getTrackPosition() < 0.0D && (sensors.getGear() > 0 || Math.abs(sensors.getTrackPosition()) > 0.6D)) {
                gear = 1;
                steer = -steer;
            }

            if(gear < 0 && sensors.getSpeed() > 2.0D) {
                steer = -steer;
                boolean var5 = true;
                action.accelerate = 0.0D;
                action.brake = 1.0D;
            } else {
                action.gear = gear;
                action.steering = (double)steer;
                action.accelerate = 1.0D;
                action.brake = 0.0D;
            }
        }

    }

    public void reset() {
        this.stuck = 0;
    }
}
