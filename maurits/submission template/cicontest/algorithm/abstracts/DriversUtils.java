package cicontest.algorithm.abstracts;

import cicontest.algorithm.abstracts.map.Corner;
import cicontest.algorithm.abstracts.map.TrackMap;
import cicontest.torcs.client.Action;
import cicontest.torcs.client.MemoryContainer;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.genome.IGenome;

public class DriversUtils {
    private static Class<? extends cicontest.torcs.controller.Driver> driverclass = null;
    public static final String BESTGENOME = "best-genome";
    private double lastOppPos;

    public DriversUtils() {
        this.lastOppPos = 0.0D;


        this.precalmed = false;
        this.calmed = false;
        this.cornering = 0.0D;
        this.startCorner = 0.0D;
        this.stopcounter = 0;
        this.laststop = 0.0D;
    }

    public double getFriction(SensorModel sensors) {
        double minspin = 10000.0D;
        double maxspin = 0.0D;
        double[] spin = sensors.getWheelSpinVelocity();

        for (int i = 0; i < 4; i++) {
            if (spin[i] < minspin) {
                minspin = spin[i];
            }
            if (spin[i] > maxspin) {
                maxspin = spin[i];
            }
        }

        if ((maxspin - minspin > 10.0D) && ((maxspin - minspin) / maxspin > 0.1D)) {
            return Math.pow(Math.max(0.1D, 1.0D - 50.0D * ((maxspin - minspin - 10.0D) / maxspin)), 2.0D);
        }

        return 1.0D;
    }


    public static RelativePosition getOpponentPosition(Action action, SensorModel sensors) {
        double distance = 10000.0D;
        double sdistance = 10000.0D;
        int sensor = 0;


        for (int i = 0; i <= 35; i++) {
            if (sensors.getOpponentSensors()[i] < 60.0D) {
                double d = sensors.getOpponentSensors()[i];

                double y = d * Math.cos(Math.toRadians(180 + i * 10));
                double ds = distance;
                if ((y < -5.0D) || ((y < 0.0D) && (i == 0))) {
                    ds += 20.0D;
                }

                if ((ds < sdistance) && (y > -10.0D)) {
                    distance = d;
                    sdistance = ds;
                    sensor = i;
                }
            }
        }


        if (distance > 60.0D) {
            return new RelativePosition();
        }

        double x = distance * Math.sin(Math.toRadians(180 + sensor * 10));
        double y = distance * Math.cos(Math.toRadians(180 + sensor * 10));

        return new RelativePosition(x, y, distance, (180 + sensor * 10) % 360);
    }

    private boolean precalmed;
    private boolean calmed;

    public void evadeOpponents(Action action, SensorModel sensors, TrackMap trackmap, double force, double evadeforce) {
        double CARWIDTH = 7.0D;
        RelativePosition ad = getOpponentPosition(action, sensors);

        if ((Double.isNaN(ad.distance)) || ((ad.distance > 100.0D) && (sensors.getSpeed() < 100.0D)) || ((ad.distance > 30.0D) && (ad.y < 0.0D))) {
            this.lastOppPos = 0.0D;
            return;
        }
        if (Math.abs(sensors.getTrackPosition()) >= 1.0D) {
            return;
        }
        Corner c = trackmap.getNextCorner(sensors.getDistanceFromStartLine());

        int ATH = 5;
        if ((ad.distance < 15.0D) && (((ad.angle > ATH) && (ad.angle < 180 - ATH)) || ((ad.angle < 360 - ATH) && (ad.angle > 180 + ATH)))) {
            action.steering = dontGoSide(action, sensors, ad.x < 0.0D, force);
            return;
        }
        if (ad.y - 5.0D > this.lastOppPos) {
            this.lastOppPos = ad.y;
            return;
        }
        if (ad.y < this.lastOppPos) {
            this.lastOppPos = ad.y;
        }

        if (((ad.y >= 0.0D) || ((ad.y > -5.0D) && (ad.angle >= 170.0D) && (ad.angle <= 190.0D))) && (ad.distance < 50.0D)) {
            if ((ad.distance < 20.0D) && (Math.abs(ad.x) > CARWIDTH)) {
                return;
            }
            if (c.sharpness < 0.0D) {
                double ttp = 0.0D;
                if ((ad.x > -CARWIDTH) && (ad.x + 0.5D * sensors.getTrackPosition() * trackmap.getWidth() > -0.5D * trackmap.getWidth() + CARWIDTH)) {
                    ttp = (ad.x + 0.5D * sensors.getTrackPosition() * trackmap.getWidth() - CARWIDTH) / (trackmap.getWidth() / 2.0D);
                    if (sensors.getTrackPosition() >= ttp) {
                    }

                } else {
                    ttp = (ad.x + 0.5D * sensors.getTrackPosition() * trackmap.getWidth() + CARWIDTH) / (trackmap.getWidth() / 2.0D);
                    if (((c.distance < 50.0D) && (Math.abs(c.sharpness / c.length) > 0.5D)) || (Math.abs(ttp) > 1.0D)) {
                        if (ad.y > 0.0D) {
                            action.accelerate = -0.5D;
                        }
                        return;
                    }
                    if (sensors.getTrackPosition() > ttp) {
                        return;
                    }
                }

                if (Math.abs(ttp - sensors.getTrackPosition()) > 0.1D) {
                    action.steering = moveTowardsTrackPosition(sensors, force * evadeforce * Math.min(1.0D, Math.abs(ttp - sensors.getTrackPosition())), ttp);
                } else {
                    action.steering = alignToTrackAxis(sensors, force * Math.abs(sensors.getAngleToTrackAxis()));
                }
                return;
            }

            if (c.sharpness > 0.0D) {
                double ttp = 0.0D;
                if ((ad.x < CARWIDTH) && (ad.x + 0.5D * sensors.getTrackPosition() * trackmap.getWidth() > 0.5D * trackmap.getWidth() - CARWIDTH)) {
                    ttp = (ad.x + 0.5D * sensors.getTrackPosition() * trackmap.getWidth() + CARWIDTH) / (trackmap.getWidth() / 2.0D);
                    if (sensors.getTrackPosition() <= ttp) {
                    }

                } else {
                    ttp = (ad.x + 0.5D * sensors.getTrackPosition() * trackmap.getWidth() - CARWIDTH) / (trackmap.getWidth() / 2.0D);
                    if (((c.distance < 50.0D) && (Math.abs(c.sharpness / c.length) > 0.5D)) || (Math.abs(ttp) > 1.0D)) {
                        if (ad.y > 0.0D) {
                            action.accelerate = -0.5D;
                        }
                        return;
                    }
                    if (sensors.getTrackPosition() < ttp) {
                        return;
                    }
                }

                if (Math.abs(ttp - sensors.getTrackPosition()) > 0.1D) {
                    action.steering = moveTowardsTrackPosition(sensors, force * evadeforce * Math.min(1.0D, Math.abs(ttp - sensors.getTrackPosition())), ttp);
                } else {
                    action.steering = alignToTrackAxis(sensors, force * Math.abs(sensors.getAngleToTrackAxis()));
                }
                return;
            }
        }
    }


    public static double dontGoSide(Action action, SensorModel sensors, boolean left, double force) {
        if ((action.steering < 0.0D) && (left) && (sensors.getAngleToTrackAxis() < 0.15D)) {
            return 0.0D;
        }
        if ((action.steering > 0.0D) && (!left) && (sensors.getAngleToTrackAxis() > -0.15D)) {
            return 0.0D;
        }
        if ((sensors.getAngleToTrackAxis() < 0.0D) && (left)) {
            return alignToTrackAxis(sensors, 2.0D * force * Math.abs(sensors.getAngleToTrackAxis()));
        }
        if ((sensors.getAngleToTrackAxis() > 0.0D) && (!left)) {
            return alignToTrackAxis(sensors, 2.0D * force * Math.abs(sensors.getAngleToTrackAxis()));
        }
        return action.steering;
    }

    public static double alignToTrackAxis(SensorModel sensors, double force) {
        double steerspeed = 0.8D + 0.2D * ((400.0D - sensors.getSpeed()) / 400.0D);
        return Math.min(1.0D, Math.max(-1.0D, force * -15.0D * sensors.getAngleToTrackAxis() * steerspeed));
    }

    public static double moveTowardsTrackPosition(SensorModel sensors, double force, double trackpos) {
        double steerspeed = 0.8D + 0.2D * ((400.0D - sensors.getSpeed()) / 400.0D);
        double offset = 0.2D + Math.min(0.6D, Math.abs(sensors.getTrackPosition() - trackpos));

        offset *= (1.0D + 10.0D * Math.pow(1.0D - steerspeed, 2.0D));

        if (sensors.getTrackPosition() < trackpos - 0.05D) {
            return offset * Math.min(1.0D, Math.max(-1.0D, steerspeed * force * Math.pow(sensors.getTrackPosition() - trackpos, 2.0D))) + (1.0D - offset) * alignToTrackAxis(sensors, force);
        }
        if (sensors.getTrackPosition() > trackpos + 0.05D) {
            return offset * Math.min(1.0D, Math.max(-1.0D, steerspeed * -1.0D * force * Math.pow(sensors.getTrackPosition() - trackpos, 2.0D))) + (1.0D - offset) * alignToTrackAxis(sensors, force);
        }

        return alignToTrackAxis(sensors, force * Math.abs(sensors.getAngleToTrackAxis()));
    }

    private double cornering;
    private double startCorner;
    private int stopcounter;
    private double laststop;

    public void calm(Action action, SensorModel sensors) {
        if (sensors.getSpeed() > 60.0D) {
            action.accelerate = 0.0D;
            action.brake = 0.0D;
        }
        if (sensors.getSpeed() > 70.0D) {
            action.accelerate = 0.0D;
            action.brake = -1.0D;
        }
        if (sensors.getSpeed() <= 60.0D) {
            action.accelerate = ((80.0D - sensors.getSpeed()) / 80.0D);
            action.brake = 0.0D;
        }
        if (sensors.getSpeed() < 30.0D) {
            action.accelerate = 1.0D;
            action.brake = 0.0D;
        }

        action.steering = moveTowardsTrackPosition(sensors, 1.0D, 0.0D);
    }


    public TrackMap recordLap(TrackMap trackmap, Action action, SensorModel sensors) {
        if (trackmap == null) {
            trackmap = new TrackMap("unknown");
        }

        if ((sensors == null) && (action == null)) {
            if ((this.startCorner > 10.0D) && (trackmap.getLength() - this.startCorner > 10.0D) && (
                    (Math.abs(180.0D * (this.cornering / 110.0D)) > 45.0D) || ((Math.abs(this.cornering) / (trackmap.getLength() - this.startCorner) > 0.15D) && (trackmap.getLength() - this.startCorner > 5.0D)))) {
                System.out.println("Recorded a corner: [" + (int) this.startCorner + "m , " + (int) (trackmap.getLength() - this.startCorner) + "m , " + (int) (180.0D * (this.cornering / 110.0D)) + " degrees]");
                trackmap.addCorner(this.startCorner, trackmap.getLength() - this.startCorner, 180.0D * (this.cornering / 110.0D));
                this.cornering = 0.0D;
                this.startCorner = 0.0D;
            }

            return trackmap;
        }

        if ((sensors.getAngleToTrackAxis() < 0.01D) && (Math.abs(sensors.getTrackPosition()) < 0.5D)) {
            double width = sensors.getTrackEdgeSensors()[0] + sensors.getTrackEdgeSensors()[18];
            if ((Double.isNaN(trackmap.getWidth())) || (trackmap.getWidth() > width)) {
                trackmap.setWidth(width);
            }
        }

        if ((!this.calmed) &&
                (!this.precalmed)) {
            action.steering = (-0.2D * sensors.getTrackPosition());
        }


        if (Math.abs(action.steering) > 0.001D) {
            if (Math.abs(sensors.getTrackPosition()) < 0.1D) {
                this.precalmed = true;
                if (Math.abs(sensors.getAngleToTrackAxis()) < 0.01D) {
                    this.calmed = true;
                }
            }
            if (this.calmed) {
                if ((Math.signum(action.steering) == Math.signum(this.cornering)) || (this.cornering == 0.0D)) {
                    if (this.cornering == 0.0D) {
                        this.startCorner = sensors.getDistanceFromStartLine();
                    }
                    if (this.startCorner > sensors.getDistanceFromStartLine() + 10.0D) {
                        if ((Math.abs(180.0D * (this.cornering / 110.0D)) > 45.0D) || ((Math.abs(this.cornering) / (sensors.getDistanceFromStartLine() - this.startCorner) > 0.15D) && (sensors.getDistanceFromStartLine() - this.startCorner > 5.0D))) {
                            System.out.println("Recorded a corner: [" + (int) this.startCorner + "m , " + (int) (sensors.getDistanceFromStartLine() - this.startCorner) + "m , " + (int) (180.0D * (this.cornering / 110.0D)) + " degrees]");
                            trackmap.addCorner(this.startCorner, sensors.getDistanceFromStartLine() - this.startCorner, 180.0D * (this.cornering / 110.0D));
                        }


                        this.cornering = action.steering;
                        this.startCorner = sensors.getDistanceFromStartLine();
                    } else {
                        this.cornering += action.steering;
                        this.stopcounter = 0;
                    }
                } else {
                    if ((Math.abs(180.0D * (this.cornering / 110.0D)) > 45.0D) || ((Math.abs(this.cornering) / (sensors.getDistanceFromStartLine() - this.startCorner) > 0.15D) && (sensors.getDistanceFromStartLine() - this.startCorner > 5.0D))) {
                        System.out.println("Recorded a corner: [" + (int) this.startCorner + "m , " + (int) (sensors.getDistanceFromStartLine() - this.startCorner) + "m , " + (int) (180.0D * (this.cornering / 110.0D)) + " degrees]");
                        trackmap.addCorner(this.startCorner, sensors.getDistanceFromStartLine() - this.startCorner, 180.0D * (this.cornering / 110.0D));
                    }

                    this.cornering = action.steering;
                    this.startCorner = sensors.getDistanceFromStartLine();
                    this.stopcounter = 0;
                }
            }
        } else {
            this.calmed = true;
            if (this.startCorner > 0.0D) {
                if (this.stopcounter == 0) {
                    this.laststop = sensors.getDistanceFromStartLine();
                }
                this.stopcounter += 1;

                if (this.stopcounter > 5) {
                    if ((Math.abs(180.0D * (this.cornering / 110.0D)) > 45.0D) || ((Math.abs(this.cornering) / (this.laststop - this.startCorner) > 0.15D) && (this.laststop - this.startCorner > 5.0D))) {
                        System.out.println("Recorded a corner: [" + (int) this.startCorner + "m , " + (int) (this.laststop - this.startCorner) + "m , " + (int) (180.0D * (this.cornering / 110.0D)) + " degrees]");
                        trackmap.addCorner(this.startCorner, this.laststop - this.startCorner, 180.0D * (this.cornering / 110.0D));
                    }


                    this.cornering = 0.0D;
                    this.startCorner = 0.0D;
                    this.stopcounter = 0;
                }
            }
        }

        if ((Double.isNaN(trackmap.getLength())) || (trackmap.getLength() < sensors.getDistanceFromStartLine())) {
            trackmap.setLength(sensors.getDistanceFromStartLine());
        }

        return trackmap;
    }

    public static void storeGenome(IGenome genome) {
        storeGenome(genome, "best-genome");
    }

    public static IGenome getStoredGenome() {
        return getStoredGenome("best-genome");
    }

    public static void storeGenome(IGenome genome, String name) {
        MemoryContainer.getInstance(driverclass).putMemory(name, genome);
        MemoryContainer.getInstance(driverclass).store();
    }

    public static IGenome getStoredGenome(String name) {
        return (IGenome) MemoryContainer.getInstance(driverclass).getMemory(name);
    }

    public static void createCheckpoint(AbstractAlgorithm eainstance) {
        MemoryContainer.getInstance(driverclass).putMemory("checkpoint", eainstance);
        MemoryContainer.getInstance(driverclass).store();
    }

    public static AbstractAlgorithm loadCheckpoint() {
        return (AbstractAlgorithm) MemoryContainer.getInstance(driverclass).getMemory("checkpoint");
    }


    public static boolean hasCheckpoint() {
        return MemoryContainer.getInstance(driverclass).hasMemory("checkpoint");
    }

    public static void clearCheckpoint() {
        MemoryContainer.getInstance(driverclass).clearMemory("checkpoint");
        MemoryContainer.getInstance(driverclass).store();
    }

    public static void registerMemory(Class<? extends cicontest.torcs.controller.Driver> dc) {
        if (driverclass != null) {
            throw new RuntimeException("EA is already running");
        }
        driverclass = dc;
    }

    public static class RelativePosition {
        public double x;
        public double y;
        public double distance;
        public double angle;

        public RelativePosition(double x, double y, double d, double angle) {
            this.x = x;
            this.y = y;
            this.distance = d;
            this.angle = angle;
        }

        /**
         * @TODO Oussama Changed NaN things here....
         */
        public RelativePosition() {
            this.x = 0.0D;
            this.y = 0.0D;
            this.distance = 0.0D;
            this.angle = 0.0D;
        }
    }
}