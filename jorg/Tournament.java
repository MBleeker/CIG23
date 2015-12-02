
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
            System.out.println("Driver ID " + driversList[i].driverID);
        }

        if(withGUI) {
            results = race.runWithGUI();
        }  else {
            results = race.run();
        }
        race.waitforFinish();
        race.updatePositions();

    }

    public void setTrack(String type, String track){

        this.race.setTrack(type, track);
    }

    public void setTermination(int laps){

        this.race.setTermination(Race.Termination.LAPS, laps);
    }

    public int[] getResults(){

        for(int i = 0; i < this.drivers.length; ++i) {
            fitness[i] = ((RaceResult)this.results.get(this.drivers[i])).getPosition();
        }

        return fitness;
    }

    public void printResults() {
        System.out.println();
        System.out.println(this.race.getTrackName() + " (" + this.race.getTerminationValue() + " laps)");

        for(int i = 0; i < this.drivers.length; ++i) {
            System.out.println("(" + ((RaceResult)this.results.get(drivers[i])).getPosition() + ") laptime: " + ((RaceResult)this.results.get(drivers[i])).getBestLapTime());
            System.out.println("Last lap-time " + this.results.get(drivers[i]).getLastlap() + " / Is finished " + this.results.get(drivers[i]).isFinished());
        }

    }

}
