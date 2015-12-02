package cicontest.torcs.race.competitionconfig;

import java.util.Comparator;

public class CompetitorConfigComparator implements Comparator<CompetitorConfig> {

    @Override
    public int compare(CompetitorConfig o1, CompetitorConfig o2) {
        return -o1.getPosition().compareTo(o2.getPosition());
    }
}
