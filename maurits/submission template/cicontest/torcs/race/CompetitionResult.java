package cicontest.torcs.race;


import java.util.ArrayList;
import java.util.List;

public class CompetitionResult {

    private List<String> tracks;
    private List<Integer> positions;
    private List<Double> times;

    public CompetitionResult() {
        tracks = new ArrayList<>();
        positions = new ArrayList<>();
        times = new ArrayList<>();
    }

    public List<String> getTracks() {
        return tracks;
    }

    public List<Integer> getPositions() {
        return positions;
    }

    public List<Double> getTimes() {
        return times;
    }
}
