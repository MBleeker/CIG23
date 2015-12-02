package cicontest.algorithm.abstracts;

import cicontest.torcs.race.Race;

public abstract class AbstractRace {
    public String tracktype;
    public String track;
    public int laps;

    public AbstractRace() {
        this.tracktype = "road";
        this.track = "aalborg";
        this.laps = 1;
    }

    public static enum DefaultTracks {
        SEOUL("a-speedway", "oval"),
        KATMANDU("e-track-5", "oval"),
        SANTIAGO("dirt-1", "dirt"),
        MANILLA("e-track-4", "road");

        private String name;
        private String type;

        private DefaultTracks(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getTrackName() {
            return this.name;
        }

        public String getTrackType() {
            return this.type;
        }

        public static DefaultTracks getTrack(int index) {
            switch (index) {
                case 0:
                    return SEOUL;
                case 1:
                    return KATMANDU;
                case 2:
                    return SANTIAGO;
            }
            return MANILLA;
        }
    }


    public void setTrack(DefaultTracks t) {
        this.track = t.getTrackName();
        this.tracktype = t.getTrackType();
    }

    public void setTrack(String track, String tracktype) {
        this.track = track;
        this.tracktype = tracktype;
    }

    public int[] runQualification(cicontest.torcs.controller.Driver[] drivers, boolean withGUI) {
        int[] fitness = new int[drivers.length];

        if (!cicontest.algorithm.abstracts.map.TrackMap.hasMap(drivers[0], this.track)) {
            getTrackmap(drivers[0], withGUI);
        }

        Race race = new Race();

        race.setTrack(this.tracktype, this.track);
        race.setTermination(cicontest.torcs.race.Race.Termination.LAPS, this.laps);
        race.setStage(cicontest.torcs.client.Controller.Stage.QUALIFICATIONS);

        for (int i = 0; i < drivers.length; i++) {
            race.addCompetitor(drivers[i]);
        }
        cicontest.torcs.race.RaceResults results;
        if (withGUI) {
            results = race.runWithGUI();
        } else {
            results = race.run();
        }

        for (int i = 0; i < drivers.length; i++) {
            fitness[i] = ((cicontest.torcs.race.RaceResult) results.get(drivers[i])).getPosition();
        }
        printResults(drivers, results);
        return fitness;
    }

    public double[] runQualification2(cicontest.torcs.controller.Driver[] drivers, boolean withGUI) {
        double[] fitness = new double[drivers.length];

        if (!cicontest.algorithm.abstracts.map.TrackMap.hasMap(drivers[0], this.track)) {
            getTrackmap(drivers[0], withGUI);
        }

        Race race = new Race();

        race.setTrack(this.tracktype, this.track);
        race.setTermination(cicontest.torcs.race.Race.Termination.LAPS, this.laps);
        race.setStage(cicontest.torcs.client.Controller.Stage.QUALIFICATIONS);

        for (int i = 0; i < drivers.length; i++) {
            race.addCompetitor(drivers[i]);
        }
        cicontest.torcs.race.RaceResults results;
        if (withGUI) {
            results = race.runWithGUI();
        } else {
            results = race.run();
        }

        for (int i = 0; i < drivers.length; i++) {
            fitness[i] = ((cicontest.torcs.race.RaceResult) results.get(drivers[i])).getBestLapTime();
        }
        printResults(drivers, results);
        return fitness;
    }

    public double[] runQualification3(cicontest.torcs.controller.Driver[] drivers, boolean withGUI) {
        double[] fitness = new double[drivers.length];

        if (!cicontest.algorithm.abstracts.map.TrackMap.hasMap(drivers[0], this.track)) {
            getTrackmap(drivers[0], withGUI);
        }

        Race race = new Race();

        race.setTrack(this.tracktype, this.track);
        race.setTermination(cicontest.torcs.race.Race.Termination.LAPS, this.laps);
        race.setStage(cicontest.torcs.client.Controller.Stage.QUALIFICATIONS);

        for (int i = 0; i < drivers.length; i++) {
            race.addCompetitor(drivers[i]);
        }
        cicontest.torcs.race.RaceResults results;
        if (withGUI) {
            results = race.runWithGUI();
        } else {
            results = race.run();
        }

        for (int i = 0; i < drivers.length; i++) {
            fitness[i] = ((cicontest.torcs.race.RaceResult) results.get(drivers[i])).getDistance();
        }
        printResults(drivers, results);

        return fitness;
    }

    public double[] runQualification4(cicontest.torcs.controller.Driver[] drivers, boolean withGUI) {
        double[] fitness = new double[drivers.length];

        if (!cicontest.algorithm.abstracts.map.TrackMap.hasMap(drivers[0], this.track)) {
            getTrackmap(drivers[0], withGUI);
        }

        Race race = new Race();

        race.setTrack(this.tracktype, this.track);
        race.setTermination(cicontest.torcs.race.Race.Termination.LAPS, this.laps);
        race.setStage(cicontest.torcs.client.Controller.Stage.QUALIFICATIONS);

        for (int i = 0; i < drivers.length; i++) {
            race.addCompetitor(drivers[i]);
        }
        cicontest.torcs.race.RaceResults results;
        if (withGUI) {
            results = race.runWithGUI();
        } else {
            results = race.run();
        }

        for (int i = 0; i < drivers.length; i++) {
            fitness[i] = ((cicontest.torcs.race.RaceResult) results.get(drivers[i])).getTime();
        }
        printResults(drivers, results);

        return fitness;
    }


    public int[] runRace(cicontest.torcs.controller.Driver[] drivers, boolean withGUI, boolean randomOrder) {
        int[] fitness = new int[drivers.length];

        if (!cicontest.algorithm.abstracts.map.TrackMap.hasMap(drivers[0], this.track)) {
            getTrackmap(drivers[0], withGUI);
        }

        if (drivers.length > 10) {
            throw new RuntimeException("Only 10 drivers are allowed in a RACE");
        }

        Race race = new Race();

        race.setTrack(this.tracktype, this.track);
        race.setTermination(cicontest.torcs.race.Race.Termination.LAPS, this.laps);
        race.setStage(cicontest.torcs.client.Controller.Stage.RACE);

        for (int i = 0; i < drivers.length; i++) {
            race.addCompetitor(drivers[i]);
        }
        if (randomOrder) {
            race.shuffleOrder();
        }
        cicontest.torcs.race.RaceResults results;
        if (withGUI) {
            results = race.runWithGUI();
        } else {
            results = race.run();
        }


        for (int i = 0; i < drivers.length; i++) {
            fitness[i] = ((cicontest.torcs.race.RaceResult) results.get(drivers[i])).getPosition();
        }

        printResults(drivers, results);
        return fitness;
    }

    public double[] runRace2(cicontest.torcs.controller.Driver[] drivers, boolean withGUI, boolean randomOrder) {
        double[] fitness = new double[drivers.length];

        if (!cicontest.algorithm.abstracts.map.TrackMap.hasMap(drivers[0], this.track)) {
            getTrackmap(drivers[0], withGUI);
        }

        if (drivers.length > 10) {
            throw new RuntimeException("Only 10 drivers are allowed in a RACE");
        }

        Race race = new Race();

        race.setTrack(this.tracktype, this.track);
        race.setTermination(cicontest.torcs.race.Race.Termination.LAPS, this.laps);
        race.setStage(cicontest.torcs.client.Controller.Stage.RACE);

        for (int i = 0; i < drivers.length; i++) {
            race.addCompetitor(drivers[i]);
        }
        if (randomOrder) {
            race.shuffleOrder();
        }
        cicontest.torcs.race.RaceResults results;
        if (withGUI) {
            results = race.runWithGUI();
        } else {
            results = race.run();
        }


        for (int i = 0; i < drivers.length; i++) {
            if (results.get(drivers[i]).isFinished()) {
                fitness[i] = results.get(drivers[i]).getTime();
            } else {
                fitness[i] = Double.POSITIVE_INFINITY;
            }
        }

        printResults(drivers, results);
        return fitness;
    }

    public double[] runRace3(cicontest.torcs.controller.Driver[] drivers, boolean withGUI, boolean randomOrder) {
        double[] fitness = new double[drivers.length];

        if (!cicontest.algorithm.abstracts.map.TrackMap.hasMap(drivers[0], this.track)) {
            getTrackmap(drivers[0], withGUI);
        }

        if (drivers.length > 10) {
            throw new RuntimeException("Only 10 drivers are allowed in a RACE");
        }

        Race race = new Race();

        race.setTrack(this.tracktype, this.track);
        race.setTermination(cicontest.torcs.race.Race.Termination.LAPS, this.laps);
        race.setStage(cicontest.torcs.client.Controller.Stage.RACE);

        for (int i = 0; i < drivers.length; i++) {
            race.addCompetitor(drivers[i]);
        }
        if (randomOrder) {
            race.shuffleOrder();
        }
        cicontest.torcs.race.RaceResults results;
        if (withGUI) {
            results = race.runWithGUI();
        } else {
            results = race.run();
        }


        for (int i = 0; i < drivers.length; i++) {
            fitness[i] = results.get(drivers[i]).getDistance();
        }
        printResults(drivers, results);
        return fitness;
    }

    public double[] runRace4(cicontest.torcs.controller.Driver[] drivers, boolean withGUI, boolean randomOrder) {
        double[] fitness = new double[drivers.length];

        if (!cicontest.algorithm.abstracts.map.TrackMap.hasMap(drivers[0], this.track)) {
            getTrackmap(drivers[0], withGUI);
        }

        if (drivers.length > 10) {
            throw new RuntimeException("Only 10 drivers are allowed in a RACE");
        }

        Race race = new Race();

        race.setTrack(this.tracktype, this.track);
        race.setTermination(cicontest.torcs.race.Race.Termination.LAPS, this.laps);
        race.setStage(cicontest.torcs.client.Controller.Stage.RACE);

        for (int i = 0; i < drivers.length; i++) {
            race.addCompetitor(drivers[i]);
        }
        if (randomOrder) {
            race.shuffleOrder();
        }
        cicontest.torcs.race.RaceResults results;
        if (withGUI) {
            results = race.runWithGUI();
        } else {
            results = race.run();
        }


        for (int i = 0; i < drivers.length; i++) {
            fitness[i] = results.get(drivers[i]).getBestLapTime();
        }
        printResults(drivers, results);
        return fitness;
    }

    private void getTrackmap(cicontest.torcs.controller.Driver driver, boolean withGUI) {
        double laptime = Double.POSITIVE_INFINITY;
        while (laptime > 10000.0D) {
            Race race = new Race();
            race.setTrack(this.tracktype, this.track);
            race.setTermination(cicontest.torcs.race.Race.Termination.LAPS, 1);
            race.setStage(cicontest.torcs.client.Controller.Stage.PRACTICE);

            race.addCompetitor(driver);

            if (withGUI) {
                laptime = race.runWithGUI().get(driver).getBestLapTime();
            } else {
                laptime = race.run().get(driver).getBestLapTime();
            }
        }
    }

    public void printResults(cicontest.torcs.controller.Driver[] drivers, cicontest.torcs.race.RaceResults r) {
        System.out.println();
        System.out.println(this.track + " (" + this.laps + " laps)");
        for (int i = 0; i < drivers.length; i++) {
            System.out.println("(" + r.get(drivers[i]).getPosition() + ") laptime: " + r.get(drivers[i]).getBestLapTime());
        }
    }
}