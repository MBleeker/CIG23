package cicontest.torcs.race.competitionconfig;

import java.util.*;

public final class RaceCompetitorsConfigGroup3 {

    private static final List<String> DRIVERS = new ArrayList<>();

    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE1 = new TreeSet<>(new CompetitorConfigComparator());
    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE2 = new TreeSet<>(new CompetitorConfigComparator());
    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE3 = new TreeSet<>(new CompetitorConfigComparator());
    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE4 = new TreeSet<>(new CompetitorConfigComparator());
    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE5 = new TreeSet<>(new CompetitorConfigComparator());

    private static final Map<Integer,Set<CompetitorConfig>> DRIVERSPOSITIONSPERRACE = new HashMap<>();

    static {

        DRIVERS.add("driver2");
        DRIVERS.add("driver5");
        DRIVERS.add("driver9");
        DRIVERS.add("driver19");
        DRIVERS.add("driver21");
        DRIVERS.add("driver23");
        DRIVERS.add("driver27");
        DRIVERS.add("driver33");

        DRIVERSPOSITIONSPERRACE.put(1, DRIVERSPOSITIONSRACE1);
        DRIVERSPOSITIONSPERRACE.put(2, DRIVERSPOSITIONSRACE2);
        DRIVERSPOSITIONSPERRACE.put(3, DRIVERSPOSITIONSRACE3);
        DRIVERSPOSITIONSPERRACE.put(4, DRIVERSPOSITIONSRACE4);
        DRIVERSPOSITIONSPERRACE.put(5, DRIVERSPOSITIONSRACE5);

        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver2", 0));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver5", 1));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver9", 2));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver19", 3));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver21", 4));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver23", 5));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver27", 6));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver33", 7));

        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver27", 0));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver33", 1));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver2", 2));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver5", 3));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver9", 4));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver19", 5));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver21", 6));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver23", 7));

        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver21", 0));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver23", 1));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver27", 2));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver33", 3));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver2", 4));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver5", 5));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver9", 6));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver19", 7));

        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver9", 0));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver19", 1));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver21", 2));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver23", 3));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver27", 4));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver33", 5));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver2", 6));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver5", 7));

        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver5", 0));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver9", 1));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver19", 2));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver21", 3));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver23", 4));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver27", 5));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver33", 6));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver2", 7));
    }

    private RaceCompetitorsConfigGroup3() {

    }

    public static Set<CompetitorConfig> getDriversPositionsForRace(int raceNumber) {
        if (raceNumber < 1 || raceNumber > 5) {
            throw new IllegalArgumentException("invalid racenumber!");
        }
        return DRIVERSPOSITIONSPERRACE.get(raceNumber);
    }

    public static List<String> getDrivers() {
        return DRIVERS;
    }
}
