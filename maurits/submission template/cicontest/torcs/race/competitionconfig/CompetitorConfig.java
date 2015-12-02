package cicontest.torcs.race.competitionconfig;


public class CompetitorConfig {

    private String driverId;
    private Integer position;

    public CompetitorConfig(String driverId, Integer position) {
        this.driverId = driverId;
        this.position = position;
    }

    public String getDriverId() {
        return driverId;
    }

    public Integer getPosition() {
        return position;
    }
}
