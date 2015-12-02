package cicontest.torcs.controller;

import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;


public class RemoteDriver
        extends Driver {
    public void controlQualification(Action action, SensorModel sensors) {
    }

    public void controlRace(Action action, SensorModel sensors) {
    }

    public void controlWarmUp(Action action, SensorModel sensors) {
    }

    public String getDriverName() {
        return "%% Remotely Controlled %%";
    }

    public void init() {
    }

    public void exit() {
    }
}