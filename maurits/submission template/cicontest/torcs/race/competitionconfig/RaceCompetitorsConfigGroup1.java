package cicontest.torcs.race.competitionconfig;

import java.util.*;

public final class RaceCompetitorsConfigGroup1 {

    private static final List<String> DRIVERS = new ArrayList<>();

    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE1 = new TreeSet<>(new CompetitorConfigComparator());
    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE2 = new TreeSet<>(new CompetitorConfigComparator());
    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE3 = new TreeSet<>(new CompetitorConfigComparator());
    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE4 = new TreeSet<>(new CompetitorConfigComparator());
    private static final Set<CompetitorConfig> DRIVERSPOSITIONSRACE5 = new TreeSet<>(new CompetitorConfigComparator());

    private static final Map<Integer,Set<CompetitorConfig>> DRIVERSPOSITIONSPERRACE = new HashMap<>();

    static {
        DRIVERS.add("driver3");
        DRIVERS.add("driver11");
        DRIVERS.add("driver15");
        DRIVERS.add("driver17");
        DRIVERS.add("driver20");
        DRIVERS.add("driver24");
        DRIVERS.add("driver26");
        DRIVERS.add("driver30");
        DRIVERS.add("driver39");

        DRIVERSPOSITIONSPERRACE.put(1, DRIVERSPOSITIONSRACE1);
        DRIVERSPOSITIONSPERRACE.put(2, DRIVERSPOSITIONSRACE2);
        DRIVERSPOSITIONSPERRACE.put(3, DRIVERSPOSITIONSRACE3);
        DRIVERSPOSITIONSPERRACE.put(4, DRIVERSPOSITIONSRACE4);
        DRIVERSPOSITIONSPERRACE.put(5, DRIVERSPOSITIONSRACE5);


        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver3", 0));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver11", 1));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver15", 2));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver17", 3));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver20", 4));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver24", 5));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver26", 6));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver30", 7));
        DRIVERSPOSITIONSRACE1.add(new CompetitorConfig("driver39", 8));

        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver30", 0));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver39", 1));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver3", 2));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver11", 3));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver15", 4));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver17", 5));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver20", 6));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver24", 7));
        DRIVERSPOSITIONSRACE2.add(new CompetitorConfig("driver26", 8));

        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver24", 0));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver26", 1));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver30", 2));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver39", 3));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver3", 4));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver11", 5));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver15", 6));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver17", 7));
        DRIVERSPOSITIONSRACE3.add(new CompetitorConfig("driver20", 8));

        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver17", 0));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver20", 1));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver24", 2));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver26", 3));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver30", 4));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver39", 5));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver3", 6));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver11", 7));
        DRIVERSPOSITIONSRACE4.add(new CompetitorConfig("driver15", 8));

        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver11", 0));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver15", 1));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver17", 2));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver20", 3));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver24", 4));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver26", 5));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver30", 6));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver39", 7));
        DRIVERSPOSITIONSRACE5.add(new CompetitorConfig("driver3", 8));
    }

    private RaceCompetitorsConfigGroup1() {
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
