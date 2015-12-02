package cicontest.torcs.race.competitionconfig;


import java.util.*;

public final class RaceCompetitorsConfigGroup2 {

    private static final List<String> DRIVERS = new ArrayList<>();

    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE1 = new TreeSet<>(new CompetitorConfigComparator());
    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE2 = new TreeSet<>(new CompetitorConfigComparator());
    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE3 = new TreeSet<>(new CompetitorConfigComparator());
    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE4 = new TreeSet<>(new CompetitorConfigComparator());
    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE5 = new TreeSet<>(new CompetitorConfigComparator());

    private static final Map<Integer,Set<CompetitorConfig>> DRIVERSPOSITIONSPERRACE = new HashMap<>();

    static {
        DRIVERS.add("driver1");
        DRIVERS.add("driver7");
        DRIVERS.add("driver13");
        DRIVERS.add("driver16");
        DRIVERS.add("driver22");
        DRIVERS.add("driver25");
        DRIVERS.add("driver28");
        DRIVERS.add("driver32");
        DRIVERS.add("driver40");

        DRIVERSPOSITIONSPERRACE.put(1, DRIVERSPOSITIONSRACE1);
        DRIVERSPOSITIONSPERRACE.put(2, DRIVERSPOSITIONSRACE2);
        DRIVERSPOSITIONSPERRACE.put(3, DRIVERSPOSITIONSRACE3);
        DRIVERSPOSITIONSPERRACE.put(4, DRIVERSPOSITIONSRACE4);
        DRIVERSPOSITIONSPERRACE.put(5, DRIVERSPOSITIONSRACE5);

        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver1", 0));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver7", 1));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver13", 2));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver16", 3));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver22", 4));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver25", 5));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver28", 6));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver32", 7));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver40", 8));

        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver32", 0));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver40", 1));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver1", 2));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver7", 3));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver13", 4));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver16", 5));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver22", 6));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver25", 7));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver28", 8));

        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver25", 0));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver28", 1));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver32", 2));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver40", 3));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver1", 4));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver7", 5));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver13", 6));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver16", 7));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver22", 8));

        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver16", 0));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver22", 1));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver25", 2));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver28", 3));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver32", 4));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver40", 5));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver1", 6));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver7", 7));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver13", 8));

        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver7", 0));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver13", 1));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver16", 2));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver22", 3));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver25", 4));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver28", 5));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver32", 6));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver40", 7));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver1", 8));
    }

    private RaceCompetitorsConfigGroup2() {

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
