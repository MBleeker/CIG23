package cicontest.torcs.client;

import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.race.Race;
import cicontest.torcs.race.RaceResult;
import race.TorcsConfiguration;

import java.awt.*;


public class CiClient extends Thread {

    private static int UDP_TIMEOUT = 10000;
    private static int port = 3001;
    private String host = "localhost";

    private Driver driver;
    private int drivernumber;
    private SocketHandler mySocket;
    private Race race;
    private RaceResult result;
    private boolean isRemoteClient = false;
    private boolean isOverruled = false;

    public static void main(String[] args) {
        if (args[0].equals("-dummy")) {
            startDummy();
            return;
        }
        if (args[0].split("-").length > 1) {
            int start = Integer.parseInt(args[0].split("-")[0]);
            int end = Integer.parseInt(args[0].split("-")[1]);

            for (int i = start; i <= end; i++) {
                args[0] = ("" + i);
                startRemoteClient(args);
            }
        } else {
            startRemoteClient(args);
        }
    }

    public static void startDummy() {
        CiClient driverclient = new CiClient();
        driverclient.isRemoteClient = true;
        driverclient.drivernumber = 1;
        driverclient.host = "localhost";

        Driver driver = new DummyDriver();

        driver.setStage(Controller.Stage.PRACTICE);

        driverclient.setup(null, driver, 1);
        driverclient.connect();
        driverclient.start();
    }

    public static void startRemoteClient(String[] args) {
        CiClient driverclient = new CiClient();
        driverclient.isRemoteClient = true;
        driverclient.drivernumber = Integer.parseInt(args[0]);
        driverclient.host = args[1];

        Driver driver = null;

        try {
            driver = (Driver) driverclient.getContextClassLoader().loadClass(args[2]).newInstance();

            System.out.println("Loaded " + args[2]);
        } catch (Throwable t) {
            System.out.println("Error in " + args[2]);
            t.printStackTrace();
        }

        MemoryContainer.getInstance(driver.getClass());

        driver.setStage(Controller.Stage.PRACTICE);

        driverclient.setup(null, driver, Integer.parseInt(args[0]));
        driverclient.connect();
        driverclient.start();
    }

    public void setup(Race race, Driver driver, int drivernumber) {
        MemoryContainer.getInstance(driver.getClass());
        this.driver = driver;
        this.drivernumber = drivernumber;
        this.race = race;
        this.result = new RaceResult();

        driver.init();
    }

    private void connect() {
        this.mySocket = new SocketHandler(this.host, port + this.drivernumber - 1, false);


        float[] angles = this.driver.initAngles();
        String initStr = "championship2010 " + this.drivernumber + "(init";
        for (int i = 0; i < angles.length; i++) {
            initStr = initStr + " " + angles[i];
        }
        initStr = initStr + ")";


        String inMsg;

        do {
            System.out.println(getName() + ": Sending init String via port: " + port + "...");
            this.mySocket.send(initStr);
            inMsg = this.mySocket.receive(UDP_TIMEOUT);
        } while ((inMsg == null) || (inMsg.indexOf("***identified***") < 0));
        System.out.println(getName() + ": Init String acknowledged by Torcs.");

        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        connect();
        String lastMessage = "";
        boolean errorfound = false;
        int timeout = 5;
        if ((TorcsConfiguration.getInstance().getOptionalProperty("timeout") != null)
                && (!TorcsConfiguration.getInstance().getOptionalProperty("timeout").equals("")))
        {
            timeout = Integer.parseInt(TorcsConfiguration.getInstance().getProperty("timeout"));
        }


        this.result.setDriver(driver);


        int counter = 0;
        int counter2 = 0;


        for (; ; ) {
            String inMsg = this.mySocket.receive(UDP_TIMEOUT);

            if (inMsg != null) {
                counter = 0;
                counter2 = 0;
                this.race.crashfixdriver = ((this.drivernumber + 1) % this.race.size());


                if (inMsg.indexOf("***shutdown***") >= 0) {
                    System.out.println("Recieved Shutdown message");
                    break;
                }


                if (inMsg.indexOf("***restart***") >= 0) {
                    break;
                }


                Action action = null;

                MessageBasedSensorModel sensors = new MessageBasedSensorModel(inMsg);


                if (!sensors.isValid()) {
                    this.mySocket.send(lastMessage);
                } else {
                    if (sensors.getMessageType() == 1) {
                        gatherStatistics(sensors);
                        this.driver.shutdown();


                        for (int ee = 0; ee < 3; ee++) {
                            this.mySocket.send("***recieved stats***");
                            synchronized (this) {
                                try {
                                    wait(1000L);
                                } catch (Exception e) {
                                }
                            }
                        }
                        break;
                    }
                    try {
                        if (!this.isOverruled) {
                            action = this.driver.determineAction(sensors);
                        } else {
                            action = new Action();
                            new DriversUtils().calm(action, sensors);
                            new AutomatedClutch().process(action, sensors);
                            new AutomatedGearbox().process(action, sensors);
                        }
                    } catch (Throwable t) {
                        if (!errorfound) {
                            t.printStackTrace();
                        }
                        action = new Action();
                        action.brake = 0.0D;
                        action.steering = 0.0D;
                        action.accelerate = 1.0D;
                        t.printStackTrace();
                        errorfound = true;
                    }

                    if ((this.race != null) && (this.race.getTerminationType() == Race.Termination.TICKS) &&
                            (this.race.getTerminationValue() <= sensors.getTicks())) {
                        action.abandonRace = true;
                    }
                    try {
                        if ((this.race != null) && (sensors.getTicks() > 1000) && (sensors.getDistanceRaced() < 100.0D) && (this.race.getStage() != Controller.Stage.PRACTICE)) {
                            action = new Action();
                            new DriversUtils().calm(action, sensors);
                            new AutomatedClutch().process(action, sensors);
                            new AutomatedGearbox().process(action, sensors);
                        }
                        if ((this.race != null) && (sensors.getCurrentLapTime() > 600.0D) && (this.race.getStage() != Controller.Stage.PRACTICE)) {
                            this.isOverruled = true;
                        }
                    } catch (Throwable t) {
                    }

                    lastMessage = action.toString();
                    this.mySocket.send(action.toString());
                }

            } else if (this.race.crashfixdriver == this.drivernumber) {
                counter++;
                if ((counter > timeout) && (!lastMessage.equals(""))) {
                    counter2++;
                    counter = 0;
                    this.mySocket.send(lastMessage);
                    if ((System.getProperty("os.name").toLowerCase().startsWith("win")) &&
                            (counter2 >= 1)) {
                        try {
                            System.out.println("TORCS hangs, now sending sendkeys");
                            Robot robot = new Robot();
                            robot.keyPress(80);
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }


        if (!this.isRemoteClient) {
            this.race.setResults(this.driver, this.result);
        }

        this.driver.exit();

        this.driver.shutdown();
        this.mySocket.close();
    }

    private void gatherStatistics(MessageBasedSensorModel model) {
        result.setBestLapTime(model.getBestLapTime());
        result.setDistance(model.getDistanceRaced());
        result.setFinished(model.getIsFinished());
        result.setLaps(model.getLaps());
        result.setTime(model.getTime());
        result.setPosition(model.getRacePosition());
        result.setLastlap(model.getLastLapTime());
    }
}