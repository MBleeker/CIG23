package cicontest.torcs.race;

import cicontest.torcs.client.CiClient;
import cicontest.torcs.client.Controller;
import cicontest.torcs.client.TorcsUtilities;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.Human;
import cicontest.torcs.controller.RemoteDriver;
import race.TorcsConfiguration;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Race implements Comparator<RaceResult> {
    private final String TORCS_PATH;
    private Controller.Stage stage = Controller.Stage.RACE;
    private Driver currentDriver = null;

    private String track;
    private String tracktype;
    private Termination termination;
    private int terminationvalue;
    private RaceResults results;
    private boolean humanMode = false;
    public boolean closeAfterFinish = true;
    public int crashfixdriver = 0;
    private ArrayList<Driver> competitors = new ArrayList();

    public static enum Termination {
        LAPS, DISTANCE, TICKS;

        private Termination() {
        }
    }

    public Race() {
        String path = TorcsConfiguration.getInstance().getProperty("torcs_path");
        if (!path.endsWith("" + File.separatorChar)) {
            path = path + File.separatorChar;
        }
        if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
            path = path + "Torcs.app/Contents/Resources/";
        }

        this.TORCS_PATH = path;
    }


    public void setStage(Controller.Stage stage) {
        this.stage = stage;
    }

    public void setTrack(String tracktype, String track) {
        this.track = track;
        this.tracktype = tracktype;
    }

    public String getTrackName() {
        return this.track;
    }

    public String getTrackType() {
        return this.tracktype;
    }

    public Controller.Stage getStage() {
        return this.stage;
    }

    public void removeAllCompetitors() {
        this.competitors.clear();
    }

    public int size() {
        return this.competitors.size();
    }

    public void shuffleOrder() {
        Collections.shuffle(this.competitors);
    }

    public void addCompetitor(Driver driver) {
        if ((driver instanceof Human)) {
            if (this.humanMode) {
                return;
            }
            this.humanMode = true;
            this.competitors.add(driver);
        } else if (!this.competitors.contains(driver)) {
            if (this.humanMode) {
                this.competitors.add(this.competitors.size() - 1, driver);
            } else {
                this.competitors.add(driver);
            }
        }
    }

    public void addCompetitor(Driver driver, int index) {
        if ((driver instanceof Human)) {
            if (this.humanMode) {
                return;
            }
            this.humanMode = true;
            this.competitors.add(Math.min(index, competitors.size()), driver);
        } else if (!this.competitors.contains(driver)) {
            if (this.humanMode) {
                this.competitors.add(this.competitors.size() - 1, driver);
            } else {
                this.competitors.add(Math.min(index, competitors.size()), driver);
            }
        }
    }

    public void setTermination(Termination type, int d) {
        this.termination = type;
        this.terminationvalue = d;
    }

    public Termination getTerminationType() {
        return this.termination;
    }

    public int getTerminationValue() {
        return this.terminationvalue;
    }


    public void writeTorcsConfig(boolean withGui) {
        String resMode = "results only";

        if (withGui)
            resMode = "normal";
        String config;
        if (this.termination == Termination.LAPS) {
            config = GenerateConfigFiles.generateRaceConfig(this.track, this.tracktype, this.stage, 0.0D, this.terminationvalue, 1 + (this.competitors.size() - 1) / 2, resMode, true, 0, this.competitors);
        } else {
            if (this.termination == Termination.DISTANCE) {
                config = GenerateConfigFiles.generateRaceConfig(this.track, this.tracktype, this.stage, this.terminationvalue, 0, 1 + (this.competitors.size() - 1) / 2, resMode, true, 0, this.competitors);

            } else {
                config = GenerateConfigFiles.generateRaceConfig(this.track, this.tracktype, this.stage, 0.0D, 100, 1 + (this.competitors.size() - 1) / 2, resMode, true, 0, this.competitors);
            }
        }

        try {
            FileWriter fstream = new FileWriter(this.TORCS_PATH + "config" + File.separatorChar + "raceman" + File.separatorChar + "quickrace.xml");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(config);
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    public void writeDriversConfig(boolean withGui) {
        if (this.stage == Controller.Stage.RACE) {
            try {
                FileWriter fstream = new FileWriter(this.TORCS_PATH + "drivers" + File.separatorChar + "championship2010server" + File.separatorChar + "championship2010server.xml");


                BufferedWriter out = new BufferedWriter(fstream);
                out.write(GenerateConfigFiles.generateDriversConfig(this.competitors));
                out.close();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        } else {
            try {
                FileWriter fstream = new FileWriter(this.TORCS_PATH + "drivers" + File.separatorChar + "championship2010server" + File.separatorChar + "championship2010server.xml");


                BufferedWriter out = new BufferedWriter(fstream);
                out.write(GenerateConfigFiles.generateDriversConfig(this.currentDriver));
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private RaceResults runRace(boolean withGui) {
        this.results = new RaceResults();
        writeTorcsConfig(withGui);
        writeDriversConfig(withGui);

        try {
            Point point = MouseInfo.getPointerInfo().getLocation();
            Robot robot = new Robot();
            robot.mouseMove(point.x + 1, point.y);
            robot.mouseMove(point.x, point.y);
        } catch (AWTException e) {
            e.printStackTrace();
        }


        int drivernumber = 1;
        for (Driver driver : this.competitors) {
            if ((!(driver instanceof Human)) && (!(driver instanceof RemoteDriver))) {
                CiClient driverclient = new CiClient();

                driver.setStage(this.stage);
                driver.setTrackName(this.track);

                driverclient.setup(this, driver, drivernumber);
                driverclient.setName(Math.random() + "-" + driver.getDriverName());
                driverclient.start();
            } else {
                this.results.put(driver, new RaceResult());
            }
            drivernumber++;
        }

        TorcsUtilities.startTORCS(withGui);

        waitforFinish();

        updatePositions();

        return this.results;
    }

    private RaceResults runQualification(boolean withGui) {
        this.results = new RaceResults();
        writeTorcsConfig(withGui);

        for (Driver driver : this.competitors) {
            this.currentDriver = driver;

            writeDriversConfig(withGui);

            if ((!(driver instanceof Human)) && (!(driver instanceof RemoteDriver))) {
                CiClient driverclient = new CiClient();

                driver.setStage(this.stage);
                driver.setTrackName(this.track);

                driverclient.setup(this, driver, 1);
                driverclient.setName(Math.random() + "-" + driver.getDriverName());

                driverclient.start();
            } else {
                this.results.put(driver, new RaceResult());
            }

            TorcsUtilities.startTORCS(withGui);

            waitforFinish();
        }

        updatePositions();

        return this.results;
    }


    public RaceResults run() {
        if (this.stage == Controller.Stage.RACE) {
            runRace(false);
        } else {
            runQualification(false);
        }

        return this.results;
    }

    public void setResults(Driver driver, RaceResult result) {
        synchronized (this.results) {
            this.results.put(driver, result);
            this.results.notifyAll();
        }
    }

    public void waitforFinish() {
        synchronized (this.results) {
            try {
                if (this.stage == Controller.Stage.RACE) {
                    while (this.results.size() != this.competitors.size()) {
                        this.results.wait();
                    }
                }
                if ((this.stage == Controller.Stage.QUALIFICATIONS) || (this.stage == Controller.Stage.PRACTICE)) {
                    while (!this.results.containsKey(this.currentDriver)) {
                        this.results.wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public int compare(RaceResult arg0, RaceResult arg1) {
        if (this.stage == Controller.Stage.RACE) {
            return new Integer(arg0.getPosition()).compareTo(Integer.valueOf(arg1.getPosition()));
        }


        if (arg0.getLaps() > 0) {
            if (arg1.getLaps() > 0) {
                return new Double(arg0.getBestLapTime()).compareTo(Double.valueOf(arg1.getBestLapTime()));
            }

            return -1;
        }

        if (arg1.getLaps() > 0) {
            return 1;
        }
        return new Double(arg1.getDistance()).compareTo(Double.valueOf(arg0.getDistance()));
    }


    public void updatePositions() {
        LinkedList<RaceResult> res = new LinkedList();
        res.addAll(this.results.values());
        Collections.sort(res, this);

        ListIterator<RaceResult> itr = res.listIterator();
        int position = 1;
        while (itr.hasNext()) {
            itr.next().setPosition(position);
            position++;
        }
    }


    public RaceResults runWithGUI() {
        if (this.stage == Controller.Stage.RACE) {
            runRace(true);
        } else {
            runQualification(true);
        }

        return this.results;
    }
}