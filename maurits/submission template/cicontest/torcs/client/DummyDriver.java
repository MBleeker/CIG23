package cicontest.torcs.client;

import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.genome.IGenome;

public class DummyDriver extends AbstractDriver {
    public double getAcceleration(SensorModel sensors) {
        return 1.0D;
    }


    public double getSteering(SensorModel sensors) {
        return 0.0D;
    }


    public void loadGenome(IGenome genome) {
    }


    public String getDriverName() {
        return "Dummy";
    }
}