import java.io.File;
import cicontest.algorithm.abstracts.AbstractAlgorithm;
import cicontest.algorithm.abstracts.AbstractRace;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.client.Controller;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.Human;
import cicontest.torcs.race.Race;
import cicontest.torcs.race.RaceResults;
import race.TorcsConfiguration;


public class DefaultDriverAlgorithm extends AbstractAlgorithm {

    private static final long serialVersionUID = 654963126362653L;

    // Jorg: added three global variables
    public static long epochs = 0;
    public static boolean useNN = true;
    public static boolean trainNN = false;
    public static boolean retrainNN = false;

    DefaultDriverGenome[] drivers = new DefaultDriverGenome[1];
    int [] results = new int[1];

    public Class<? extends Driver> getDriverClass(){
        return DefaultDriver.class;
    }

    public void run(boolean continue_from_checkpoint) {
        if(!continue_from_checkpoint){
            //init NN
            System.out.println("Use NN " + useNN + " : trainings: " + trainNN  + " or retrain mode " + retrainNN );
            DefaultDriverGenome genome = new  DefaultDriverGenome();
            drivers[0] = genome;

            //Start a race
            Race race = new Race();
            race.setTrack("road", "aalborg");
            race.setTermination(Race.Termination.LAPS, 1);
            race.setStage(Controller.Stage.RACE);
            race.addCompetitor(new DefaultDriver());
            Boolean withGUI = true;
            RaceResults results;
            if(withGUI) {
                results = race.runWithGUI();
            } else {
                results = race.run();
            }
            // DefaultRace race = new DefaultRace();
            // race.setTrack( AbstractRace.DefaultTracks.getTrack(0));
            // race.laps = 1;

            // Save genome/nn
            // DriversUtils.storeGenome(drivers[0]);

        }
            // create a checkpoint this allows you to continue this run later
            // DriversUtils.createCheckpoint(this);
            // DriversUtils.clearCheckpoint();
    }

	public static void main(String[] args) {

        //Set path to torcs.properties
        TorcsConfiguration.getInstance().initialize(new File("C:/Users/Maartje/Documents/Studie/master/ci/project/files/torcs.properties"));
		/*
		 *
		 * Start without arguments to run the algorithm
		 * Start with -continue to continue a previous run
		 * Start with -show to show the best found
		 * Start with -show-race to show a race with 10 copies of the best found
		 * Start with -human to race against the best found
		 *
		 */

        DefaultDriverAlgorithm algorithm = new DefaultDriverAlgorithm();
        // DriversUtils.registerMemory(algorithm.getDriverClass());
        if(args.length > 0 && args[0].equals("-show")){
            new DefaultRace().showBest();
        } else if(args.length > 0 && args[0].equals("-show-race")){
            new DefaultRace().showBestRace();
        } else if(args.length > 0 && args[0].equals("-human")){
            new DefaultRace().raceBest();
        } else if(args.length > 0 && args[0].equals("-continue")){
            if(DriversUtils.hasCheckpoint()){
                DriversUtils.loadCheckpoint().run(true);
            } else {
                algorithm.run();
            }
        } else {
            algorithm.run();
        }
        System.out.println("# of training epochs " + epochs);
    }

}