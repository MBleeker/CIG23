
import cicontest.torcs.client.Controller;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.race.Race;
import cicontest.torcs.race.RaceResult;
import cicontest.torcs.race.RaceResults;

/**
 * Created by Jörg on 29-11-2015.
 */
public class Tournament  {

    private Race race;
    private int[] fitness;
    private RaceResults results;
    private DefaultDriver[] drivers;

    public Tournament(String type, String track, int laps){

        race = new Race();
        race.setTrack(type, track);
        race.setTermination(Race.Termination.LAPS, laps);
        race.setStage(Controller.Stage.RACE);
    }

    public Tournament(){

        race = new Race();
        race.setTrack("road", "aalborg");
        race.setTermination(Race.Termination.LAPS, 1);
        race.setStage(Controller.Stage.RACE);
    }

    public void run(DefaultDriver[] driversList, boolean withGUI){

        this.drivers = driversList;
        if (race.size() != 0 ) race.removeAllCompetitors();

        for (int i=0; i < driversList.length; i++) {
            race.addCompetitor(driversList[i]);
            race.closeAfterFinish = true;
            System.out.println("Driver " + driversList[i].getDriverName());
        }

        if(withGUI) {
            results = race.runWithGUI();
        }  else {
            results = race.run();
        }

    }

    public void setTrack(String type, String track){

        this.race.setTrack(type, track);
    }

    public int[] getResults(){

        fitness = new int[this.drivers.length];
        for(int i = 0; i < this.drivers.length; ++i) {
            fitness[i] = ((RaceResult)this.results.get(this.drivers[i])).getPosition();
            System.out.println(drivers[i].getDriverName() + " = " + fitness[i] + " distance: " + ((RaceResult)this.results.get(this.drivers[i])).getDistance());
        }

        return fitness;
    }

}
