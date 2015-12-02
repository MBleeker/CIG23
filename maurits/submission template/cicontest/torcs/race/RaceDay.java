package cicontest.torcs.race;

import cicontest.algorithm.abstracts.IsolatedAbstractsLoader;
import cicontest.torcs.client.Controller;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.GenomeDriver;

import java.io.*;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Manifest;

public class RaceDay extends Race {
    public Driver loadDriver(URL jarFile) {
        URLClassLoader classloader = new IsolatedAbstractsLoader(new URL[]{jarFile}, getClass().getClassLoader());

        try {
            JarURLConnection jarConnection = (JarURLConnection) jarFile.openConnection();
            Manifest manifest = jarConnection.getManifest();
            String drivername = manifest.getMainAttributes().getValue("Driver");
            if (drivername == null) {
                System.out.println("Driver is missing from " + jarFile.toString());
                return null;
            }
            Class<?> driverclass = classloader.loadClass(drivername);
            GenomeDriver d = (GenomeDriver) driverclass.newInstance();
            d.loadBestGenome();
            return d;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadDriversFromDirectory(File dir) {
        File[] files = dir.listFiles();
        for (File f : files) {
            if ((f.getName().endsWith(".jar")) && (!f.getName().endsWith("ECPRAC-TORCS.jar"))) {
                try {
                    Driver d = loadDriver(new URL("jar:file:" + f.toURI().getPath() + "!/"));
                    if (d != null) {
                        addCompetitor(d);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            } else if (f.isDirectory()) {
                loadDriversFromDirectory(f);
            }
        }
    }

    public static void writeResults(Set<Driver> drivers, String track, RaceResults qresults, RaceResults rresults) {
        Properties p = new Properties();
        if (new File("results.txt").exists()) {
            try {
                p.load(new FileInputStream("results.txt"));
            } catch (InvalidPropertiesFormatException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new File("results.txt").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 1; i <= drivers.size(); i++) {
            for (Driver d : drivers) {
                if (qresults.get(d).getPosition() == i) {
                    p.setProperty("qualification." + track + "" + i + ".driver", d.getDriverName());
                    p.setProperty("qualification." + track + "" + i + ".time", "" + qresults.get(d).getBestLapTime());
                }
            }
        }
        for (int i = 1; i <= Math.min(drivers.size(), 10); i++) {
            for (Driver d : drivers) {
                if ((qresults.containsKey(d)) && (qresults.get(d).getPosition() == i)) {
                    p.setProperty("race." + track + "" + i + ".driver", d.getDriverName());
                    p.setProperty("race." + track + "" + i + ".time", "" + rresults.get(d).getBestLapTime());
                }
            }
        }
        try {
            p.store(new FileOutputStream("results.txt"), "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String tracktype = "dirt";
        String track = "dirt-1";
        int qlaps = 3;
        int rlaps = 10;
        boolean QwithGUI = true;
        boolean RwithGUI = true;


        if (args.length >= 2) {
            tracktype = args[0];
            track = args[1];
        }
        if (args.length >= 4) {
            qlaps = Integer.parseInt(args[2]);
            rlaps = Integer.parseInt(args[3]);
            if (args.length == 7) {
                QwithGUI = Boolean.parseBoolean(args[4]);
                RwithGUI = Boolean.parseBoolean(args[5]);
            }
        }

        RaceDay prace = new RaceDay();
        prace.setTrack(tracktype, track);
        prace.setTermination(Race.Termination.LAPS, 1);
        prace.loadDriversFromDirectory(new File(""));
        prace.setStage(Controller.Stage.PRACTICE);
        RaceResults presults;
        if (QwithGUI) {
            presults = prace.runWithGUI();
        } else {
            presults = prace.run();
        }

        RaceDay qrace = new RaceDay();
        qrace.setTrack(tracktype, track);
        qrace.setTermination(Race.Termination.LAPS, qlaps);
        qrace.setStage(Controller.Stage.QUALIFICATIONS);
        Set<Driver> drivers = presults.keySet();
        for (Driver d : drivers)
            qrace.addCompetitor(d);
        RaceResults qresults;
        if (QwithGUI) {
            qresults = qrace.runWithGUI();
        } else {
            qresults = qrace.run();
        }

        RaceDay rrace = new RaceDay();
        rrace.setTrack(tracktype, track);
        rrace.setTermination(Race.Termination.LAPS, rlaps);
        rrace.setStage(Controller.Stage.RACE);

        for (int i = 1; i <= Math.min(drivers.size(), 10); i++) {
            for (Driver d : drivers) {
                if (qresults.get(d).getPosition() == i)
                    rrace.addCompetitor(d);
            }
        }
        RaceResults rresults;
        if (RwithGUI) {
            rresults = rrace.runWithGUI();
        } else {
            rresults = rrace.run();
        }
        writeResults(drivers, track, qresults, rresults);
    }
}
