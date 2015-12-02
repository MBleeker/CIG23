package cicontest.torcs.race;

import cicontest.torcs.client.Controller;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.Human;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class GenerateConfigFiles {
    static {
        Locale.setDefault(Locale.ENGLISH);
    }

    public static String generateDriversConfig(ArrayList<Driver> drivers) {
        String configformatstring = "";

        InputStream configStream = GenerateConfigFiles.class.getResourceAsStream("/championship2010server.xml");

        BufferedReader buf = new BufferedReader(new InputStreamReader(configStream));

        String line = "";
        try {
            while ((line = buf.readLine()) != null) {
                configformatstring = configformatstring + line + "\n\r";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] drivernames = new String[10];
        for (int i = 0; i < drivers.size(); i++) {
            drivernames[i] = ((Driver) drivers.get(i)).getDriverName();
        }
        for (int i = drivers.size(); i < 10; i++) {
            drivernames[i] = "NOT_IN_USE";
        }

        return String.format(configformatstring, new Object[]{drivernames[0], drivernames[1], drivernames[2], drivernames[3], drivernames[4], drivernames[5], drivernames[6], drivernames[7], drivernames[8], drivernames[9]});
    }


    public static String generateDriversConfig(Driver currentDriver) {
        String configformatstring = "";

        InputStream configStream = GenerateConfigFiles.class.getResourceAsStream("/championship2010server.xml");

        BufferedReader buf = new BufferedReader(new InputStreamReader(configStream));

        String line = "";
        try {
            while ((line = buf.readLine()) != null) {
                configformatstring = configformatstring + line + "\n\r";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] drivernames = new String[10];
        drivernames[0] = currentDriver.getDriverName();

        for (int i = 1; i < 10; i++) {
            drivernames[i] = "NOT_IN_USE";
        }

        return String.format(configformatstring, new Object[]{drivernames[0], drivernames[1], drivernames[2], drivernames[3], drivernames[4], drivernames[5], drivernames[6], drivernames[7], drivernames[8], drivernames[9]});
    }


    public static String generateRaceConfig(String trackName, String trackType, Controller.Stage raceType, double terminationDistance, int terminationLaps, int rows, String diplayModus, boolean displayResults, int driverID, ArrayList<Driver> drivers) {
        int humanIndex = -1;

        String driversformatstring = "<section name=\"%1$s\"> \n\r  <attnum name=\"idx\" val=\"%2$s\"/>  \n\r  <attstr name=\"module\" val=\"%3$s\"/> \n\r </section>";


        String configformatstring = "";

        InputStream configStream = GenerateConfigFiles.class.getClassLoader().getResourceAsStream("quickrace.xml");

        BufferedReader buf = new BufferedReader(new InputStreamReader(configStream));

        String line = "";
        try {
            while ((line = buf.readLine()) != null) {
                configformatstring = configformatstring + line + "\n\r";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        String result = "";
        String driversstring = "";

        if (raceType == Controller.Stage.RACE) {
            for (int i = 0; i < drivers.size(); i++) {
                if ((drivers.get(i) instanceof Human)) {
                    humanIndex = i;
                    driversstring = driversstring + String.format(driversformatstring, new Object[]{Integer.valueOf(i + 1), Integer.valueOf(1), "human"});
                } else {
                    driversstring = driversstring + String.format(driversformatstring, new Object[]{Integer.valueOf(i + 1), Integer.valueOf(i), "championship2010server"});
                }
            }
        }
        if ((raceType == Controller.Stage.QUALIFICATIONS) || (raceType == Controller.Stage.PRACTICE)) {
            if ((drivers.get(driverID) instanceof Human)) {
                humanIndex = driverID;
                driversstring = driversstring + String.format(driversformatstring, new Object[]{Integer.valueOf(1), Integer.valueOf(0), "human"});
            } else {
                driversstring = driversstring + String.format(driversformatstring, new Object[]{Integer.valueOf(1), Integer.valueOf(0), "championship2010server"});
            }
        }


        if (humanIndex == -1) {
            result = result + String.format(configformatstring, new Object[]{trackName, trackType, getRaceTypeName(raceType), "" + terminationDistance, "" + terminationLaps, "" + rows, diplayModus, "" + displayResults, "1", "championship2010server", driversstring});

        } else {
            result = result + String.format(configformatstring, new Object[]{trackName, trackType, getRaceTypeName(raceType), "" + terminationDistance, "" + terminationLaps, "" + rows, diplayModus, "" + displayResults, "1", "human", driversstring});
        }


        return result;
    }

    private static String getRaceTypeName(Controller.Stage raceType) {
        if (raceType == Controller.Stage.QUALIFICATIONS) {
            return "race";
        }
        return raceType.name().toLowerCase();
    }
}