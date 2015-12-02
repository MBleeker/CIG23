package cicontest.algorithm.abstracts;

import cicontest.algorithm.abstracts.map.TrackMap;
import cicontest.torcs.client.Action;
import cicontest.torcs.client.Controller;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.controller.GenomeDriver;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.controller.extras.AutomatedRecovering;

public abstract class AbstractDriver
        extends GenomeDriver {
    public DriversUtils utils = new DriversUtils();
    public TrackMap trackmap;

    public void loadBestGenome() {
        DriversUtils.registerMemory(getClass());
        loadGenome(DriversUtils.getStoredGenome());
    }

    public void init() {
        enableExtras(new AutomatedClutch());
        enableExtras(new AutomatedGearbox());
        enableExtras(new AutomatedRecovering());
        enableExtras(new ABS());
        this.trackmap = TrackMap.getMap(this, getTrackName());

        if (getStage() == Controller.Stage.PRACTICE) {
            this.trackmap.clear();
        }
    }

    public void exit() {
        if (getStage() == Controller.Stage.PRACTICE) {
            this.trackmap.store(this);
        }
    }

    public void controlWarmUp(Action action, SensorModel sensors) {
        this.utils.calm(action, sensors);
        this.utils.recordLap(this.trackmap, action, sensors);
    }

    public void shutdown() {
        if (getStage() == Controller.Stage.PRACTICE) {
            this.utils.recordLap(this.trackmap, null, null);
        }
    }

    public void controlQualification(Action action, SensorModel sensors) {
        defaultControl(action, sensors);
    }

    public void controlRace(Action action, SensorModel sensors) {
        defaultControl(action, sensors);

        this.utils.evadeOpponents(action, sensors, this.trackmap, 0.5D, 0.5D);
    }


    public void defaultControl(Action action, SensorModel sensors) {
        action.brake = 0.0D;
        action.accelerate = getAcceleration(sensors);
        if (action.accelerate < 0.0D) {
            action.brake = (-1.0D * action.accelerate);
            action.accelerate = 0.0D;
        }

        action.steering = getSteering(sensors);
    }

    public abstract double getSteering(SensorModel paramSensorModel);

    public abstract double getAcceleration(SensorModel paramSensorModel);
}