import race.TorcsConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import Jama.Matrix;
import edu.umbc.cs.maple.utils.JamaUtils;

import javax.security.auth.DestroyFailedException;

/**
 * Created by Jörg on 29-11-2015.
 */

public class EATorcs {

    // constants
    private static int NUM_OF_INPUT_UNITS = 15;
    private static int NUM_OF_INITIAL_HIDDEN_NODES = 10;
    private static int NUM_OF_OUTPUT_UNITS = 1;
    private static int MIN_FITNESS = 1; // this is the fitness a driver minally needs to have to pass to the next round. This can change if you want to take damage into account for example
    private static int TOTAL_GENERATIONS = 4;
    private static String ACTIVATION_FUNCTION = "tanh";
    private static double LEARNING_RATE = 0.01;
    private static String OUTPUT_DIR = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/out/";
    private static int MAX_COMPETITORS = 2;
    private static String TRAININGS_FILE = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/trainNN/train_nn_data_all.dat";

    // class variables
    private double prob_mu_genome = 0.6;
    private double prob_mu_weight = 0.9;
    private double prob_new_weight = 1 - prob_mu_weight;
    private int initial_pop_size;
    private double perc_survivors = 0.5;
    private String output_dir;
    private String mem_location;
    private ArrayList<DefaultDriver> population;
    private ArrayList<DefaultDriver> survivors;
    private Tournament tt;
    private String[][] mem_files;


    // constructor
    public EATorcs(int pop_size){

        this.setInitial_pop_size(pop_size);
        population = new ArrayList<>();
        this.output_dir = TorcsConfiguration.getInstance().getOptionalProperty("training_dir");
        this.mem_location = TorcsConfiguration.getInstance().getOptionalProperty("mem_location");

        if (output_dir == null) {
            output_dir = OUTPUT_DIR;
            TRAININGS_FILE = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/trainNN/train_nn_data_all.dat";
        }
        else {
            TRAININGS_FILE = this.output_dir + "train_nn_data_all.dat";
        }
        this.mem_files = new String[3][pop_size];
    }

    public int getInitial_pop_size() {
        return initial_pop_size;
    }

    public void setInitial_pop_size(int initial_pop_size) {
        this.initial_pop_size = initial_pop_size;
    }

    public NeuralNetwork buildNetwork(int n_input, int n_hidden, int n_out, String act_func, double lr){

        if (act_func == ""){
            act_func = ACTIVATION_FUNCTION;
        }
        NeuralNetwork nn;
        nn = new NeuralNetwork();

        try {
            nn.buildInputLayer(n_input, act_func);
            nn.buildHiddenLayer(n_hidden, act_func);
            nn.buildOutputLayer(n_out, act_func);
        } catch (NeuralNetwork.WrongBuildSequence wrongBuildSequence) {
            wrongBuildSequence.printStackTrace();
        }

        nn.setLearningRate(lr);
        return nn;

    }

    public NeuralNetwork cloneNN(String inputFile){

        NeuralNetwork new_nn;
        new_nn = DefaultDriver.loadNN(inputFile);
        return new_nn;
    }

    public void unloadNetwork(DefaultDriver dd, String inFile, String typeNN){

        String abs_filepath;
        abs_filepath = this.output_dir + dd.hashCode() + "_" + inFile;

        switch (typeNN) {
            case "steering":
                this.mem_files[0][Integer.parseInt(dd.driverID) - 1] = abs_filepath;
                dd.storeNN(dd.MyNNSteer, abs_filepath);
                break;
            case "accelerate":
                this.mem_files[1][Integer.parseInt(dd.driverID) - 1] = abs_filepath;
                dd.storeNN(dd.MyNNAcc, abs_filepath);
                break;
        }
    }

    public void loadNetworks(String inFile, String typeNN, Boolean uniSource){
        /*
        Method that is useful to use when initializing the population for the first time;
         */
        int i = 0;
        String abs_filepath;

        for (DefaultDriver dd: this.population){

            abs_filepath = this.output_dir + (uniSource ? "" : (dd.hashCode() + "_"))  + inFile;

            switch (typeNN) {
                case "steering":
                    this.mem_files[0][i] = this.output_dir + dd.hashCode() + "_" + inFile;
                    dd.MyNNSteer = DefaultDriver.loadNN(abs_filepath);
                    break;
                case "accelerate":
                    System.out.println("loading accelerating network");
                    this.mem_files[1][i] = this.output_dir + dd.hashCode() + "_" + inFile;
                    dd.MyNNAcc = DefaultDriver.loadNN(abs_filepath);
                    break;
                case "break":
                    this.mem_files[1][i] = this.output_dir + dd.hashCode() + "_" + inFile;
                    dd.MyNNBreak = DefaultDriver.loadNN(abs_filepath);
                    break;

            }
            System.out.println((i+1) + " mem file " + abs_filepath);
            i++;
        }
    }

    public void saveNetworks(String typeNN, String inFile){

        for (int i=0; i< this.population.size() ; i++){

            String abs_filepath = this.output_dir + i + "_" + inFile;
            switch (typeNN) {
                case "steering":
                    population.get(i).storeNN(population.get(i).MyNNSteer, abs_filepath);
                    break;
                case "accelerate":
                    population.get(i).storeNN(population.get(i).MyNNAcc, abs_filepath);
                    break;
                case "break":
                    population.get(i).storeNN(population.get(i).MyNNBreak, abs_filepath);
                    break;
            }
        }
    }

    public void initializePopulation(){

        NeuralNetwork nn;
        DefaultDriver driver;

        for (int i = 0; i < initial_pop_size; i++){
            driver = new DefaultDriver(Integer.toString(i+1));
            // driver.MyNNSteer = this.buildNetwork(NUM_OF_INPUT_UNITS, NUM_OF_INITIAL_HIDDEN_NODES, NUM_OF_OUTPUT_UNITS, ACTIVATION_FUNCTION, LEARNING_RATE);
            // driver.MyNNAcc = this.buildNetwork(NUM_OF_INPUT_UNITS, NUM_OF_INITIAL_HIDDEN_NODES, NUM_OF_OUTPUT_UNITS, ACTIVATION_FUNCTION, LEARNING_RATE);
            this.population.add(driver);
        }
        this.loadNetworks("steering_nn.mem", "steering", true);
        //this.loadNetworks("acc_nn.mem", "accelerate", true);
        //this.loadNetworks("break_nn.mem", "break", true);
        this.mutateGenome();

    }

    public void registerFitness(){


    }

    public void runTournament(){

            DefaultDriver[] drivers = new DefaultDriver[MAX_COMPETITORS];
            this.survivors = new ArrayList<>();
            // this.tt = new Tournament("road", "aalborg", 1);
            this.tt = new Tournament("dirt-1", "dirt", 1);
            Collections.shuffle(this.population);

            int i = 0;
            int total = 0;
            for (int j = 0; j < this.population.size(); j++) {
                drivers[i] = this.population.get(j);
                //drivers[i].fitness = i+1; // for testing!!!!
                i++;
                total++;
                System.out.println("i = " + i + " - total " + total);
                if (i == MAX_COMPETITORS || (this.population.size() == total )) {
                    System.out.println("Starting new tournament...");
                    tt.run(drivers, true);
                    int[] fitness = this.tt.getResults();

                    //this.tt.printResults(); // why are we doing this here??
                    for (int jj=0; jj < drivers.length; jj++){
                        System.out.println(drivers[jj].fitness);

                        // parent selection
                        if (drivers[jj].fitness <= MIN_FITNESS) {
                            this.survivors.add(drivers[jj]);
                        }

                       // System.out.println("Driver: " + drivers[jj].getDriverName() + " fitness: " + drivers[jj].fitness);
                       // System.out.println("Driver: " + drivers[jj].getDriverName() + " bestLap: " + drivers[jj].bestLap);
                    }
                    i = 0;
                    if ( this.population.size() - total < MAX_COMPETITORS ) {

                        System.out.println("Last tournament with " + (this.population.size() - total) + " drivers.");
                        drivers = new DefaultDriver[this.population.size() - total];
                    } else {
                        System.out.println("Next tournament with " + (this.population.size() - total) + " drivers.");
                        drivers = new DefaultDriver[MAX_COMPETITORS];
                    }
                }

                //this.survivors = selectParents(drivers, this.survivors);
            }

            makeNewGeneration(this.survivors);
    }

    public void makeNewGeneration(ArrayList<DefaultDriver> survivors) {
        System.out.println("Making new generation...");
        System.out.println("Length survivors: " + survivors.size());
        DefaultDriver driver;
        DefaultDriver driver2;
        //ArrayList<NeuralNetwork>  newGeneration = new ArrayList<>();
        ArrayList<DefaultDriver> population = new ArrayList<>();
        int family_members = 0;
        int child_count = survivors.size();
        for (int i=0; i<survivors.size(); i++) {
            String [] survivor_genome = new String[3];
            // hold nn-steering file-name of this driver in order to clone the network
            survivor_genome[0] = this.mem_files[0][Integer.parseInt(survivors.get(i).driverID) - 1];
            // transfer survivor to new population and change driver ID!
            survivors.get(i).driverID = Integer.toString(i+1);
            survivors.get(i).MyNNSteer = this.cloneNN(survivor_genome[0]);
            population.add(survivors.get(i));

            if (family_members < TOTAL_GENERATIONS) {  // eigenlijk zou die if niet moeten, als we 50% van de parents selcteren
                // als survivors dan moet iedereen één child krijgen, toch?
                System.out.println("number family members: " + family_members);
                family_members++;
                // create new driver
                DefaultDriver dd = new DefaultDriver(Integer.toString(child_count+1));
                child_count++;
                // clone the NN of the parent, remember we stored that above in the String-array survivor_genome
                // String array: [0] = Steering NN; [1] = accelerate/brake NN
                dd.MyNNSteer = this.cloneNN(survivor_genome[0]);
                population.add(dd);
                System.out.println("population size: " + population.size());
                // All testing:
                /*System.out.println("old nw lr: " + survivors.get(i).MyNNSteer.getLearningRate());
                System.out.println("new nw lr: " + newNNSteer.getLearningRate());
                System.out.println("old nw lr after adaptation: " + survivors.get(i).MyNNSteer.getLearningRate());
                System.out.println("new nw lr after adaptation: " + newNNSteer.getLearningRate()); */
            } else {
                System.out.println("*** Can't create anymore children for survivors ***");
            }
        }

        this.population = population;
        // we re-initialize our double String array that stores the file-names of the steering and acc/brake network
        // files names and locations
        this.mem_files = new String[3][this.getInitial_pop_size()];
        System.out.println("population size at the end: "+ this.population.size());

    }

    /*
     * Input:   all drivers of the tournament
     *          After one round tournament this method is called. It selects the best parents, based on the outcome of one round
     */
    /*public ArrayList selectParents(DefaultDriver[] drivers, ArrayList survivors) {

        System.out.println("drivers: " + drivers);
        System.out.println("survivors: " + survivors);
        int no_drivers = drivers.length;
        for (int driver=0; driver<no_drivers; driver++) {
            if (drivers[driver].fitness <= MIN_FITNESS) {
                survivors.add(drivers[driver]);
            }
        }
        return survivors;
    } */

    public NeuralNetwork trainNetwork(NeuralNetwork nn, double learning_rate){

        nn.setLearningRate(learning_rate);
        NeuralNetworkUtils helper = new NeuralNetworkUtils(nn);
        helper.train_data = helper.getInputFile(TRAININGS_FILE);
        double MSE = helper.trainNeuralNetwork();
        System.out.println("Trained network with LR " + learning_rate + " Architecture: " + nn.toString() + " / MSE = " + MSE);
        return helper.MyNN;
    }

    public NeuralNetwork trainNetworkAcc(NeuralNetwork nn, double learning_rate){

        nn.setLearningRate(learning_rate);
        NeuralNetworkUtils helper = new NeuralNetworkUtils(nn);
        helper.train_data = helper.getInputFile(TRAININGS_FILE);
        double MSE = helper.trainNeuralNetworkAcc();
        System.out.println("Trained network with LR " + learning_rate + " Architecture: " + nn.toString() + " / MSE = " + MSE);
        return helper.MyNN;
    }

    public NeuralNetwork trainNetworkBreak(NeuralNetwork nn, double learning_rate){

        nn.setLearningRate(learning_rate);
        NeuralNetworkUtils helper = new NeuralNetworkUtils(nn);
        helper.train_data = helper.getInputFile(TRAININGS_FILE);
        double MSE = helper.trainNeuralNetworkBreak();
        System.out.println("Trained network with LR " + learning_rate + " Architecture: " + nn.toString() + " / MSE = " + MSE);
        return helper.MyNN;
    }

    public NeuralNetwork addNodeToHiddenLayer(NeuralNetwork nn) {

        /*
        Basically if a new hidden layer node is added we need to add:
            (1) a row vector to the weight matrix from input->hidden layer
            (2) a column vector to the weight matrix from hidden->output layer
            NOTE: the method ASSUMES that our networks have 1 HIDDEN LAYER!!!!
         */
        int n, m;
        JamaUtils helper = new JamaUtils();
        for (int i=0; i < nn.allLayers.size(); i++) {
            if (nn.allLayers.get(i).getWeightMatrix() != null) {
                n = nn.allLayers.get(i).getWeightMatrix().getRowDimension();
                m = nn.allLayers.get(i).getWeightMatrix().getColumnDimension();

                if (nn.allLayers.get(i).isHiddenLayer()){
                    Matrix row = Matrix.random(1, m);
                    //System.out.println("HL: Dim old matrix " + NeuralNetworkUtils.getDimMatrix(aLayer.getWeightMatrix()));
                    //System.out.println("HL: Dim row vector " + NeuralNetworkUtils.getDimMatrix(row));
                    nn.allLayers.get(i).setWeightMatrix(helper.rowAppend(nn.allLayers.get(i).getWeightMatrix() , row));
                    nn.allLayers.get(i).setNumberOfNeurons(n+1);
                    //System.out.println("HL: Dim new matrix " + NeuralNetworkUtils.getDimMatrix(aLayer.getWeightMatrix()));

                } else {
                    if (nn.allLayers.get(i).isOutputLayer()) {
                        Matrix column = Matrix.random(n, 1);
                        //System.out.println("OL: Dim old matrix " + NeuralNetworkUtils.getDimMatrix(aLayer.getWeightMatrix()));
                        //System.out.println("OL: Dim row vector " + NeuralNetworkUtils.getDimMatrix(column));
                        nn.allLayers.get(i).setWeightMatrix(helper.columnAppend(nn.allLayers.get(i).getWeightMatrix(), column));
                        System.out.println("OL: Dim new matrix " + NeuralNetworkUtils.getDimMatrix(nn.allLayers.get(i).getWeightMatrix()));

                    }
                }
            }
        }
        return nn;
    }

    public void perturbateWeights(NetworkLayer nnLayer){

        double[][] hx = nnLayer.getWeightMatrix().getArray();
        int n = nnLayer.getWeightMatrix().getRowDimension();
        int m = nnLayer.getWeightMatrix().getColumnDimension();
        double tick ;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (Math.random() < prob_mu_weight){
                    if (Math.random() < 0.5) { tick = -0.1;} else {tick = 0.1;}
                    hx[i][j] = hx[i][j] + (Math.random() * tick);
                }
                else {
                    // new weight value between 0-1
                    hx[i][j] = Math.random();
                }
            }
        }

        nnLayer.setWeightMatrix(new Matrix(hx));
    }

    public double mutateLearningRate(){

        double lr, pert;
        if (Math.random() < 0.5) { pert = -0.001;} else {pert = 0.001;}
        lr = LEARNING_RATE + (pert * Math.random());
        return lr;
    }

    public void mutateGenome(){

        /*
            only mutate weight matrices very slightly by perturbation
         */
        for (int i=0; i< this.population.size() ; i++) {
            if (Math.random() < prob_mu_genome) {
                System.out.println("Mutate weights for driver " + population.get(i).getDriverName());
                // population.get(i).MyNNSteer.outputLayer.setWeightMatrix(this.perturbateWeights( population.get(i).MyNNSteer.outputLayer.getWeightMatrix()));
                System.out.println("Mutating weights for steering network...");

                population.get(i).MyNNSteer = this.addNodeToHiddenLayer(population.get(i).MyNNSteer);
                this.perturbateWeights(population.get(i).MyNNSteer.outputLayer);
                double learning_rate_steer = this.mutateLearningRate();
                this.unloadNetwork(population.get(i), "steering_nn.mem", "steering");
                population.get(i).MyNNSteer = this.trainNetwork(population.get(i).MyNNSteer, learning_rate_steer);

                /*System.out.println("Mutating weights for accelerating network...");
                population.get(i).MyNNAcc = this.addNodeToHiddenLayer(population.get(i).MyNNAcc);
                this.perturbateWeights(population.get(i).MyNNAcc.outputLayer);
                double learning_rate_acc = this.mutateLearningRate();
                population.get(i).MyNNAcc = this.trainNetworkAcc(population.get(i).MyNNAcc, learning_rate_acc); */
            } else {
                this.unloadNetwork(population.get(i), "steering_nn.mem", "steering");
            }
            // in any case, save network to file, so that we can clone the original genome later when this is a survivor

        }

    }

    public static void main(String[] args){

        TorcsConfiguration.getInstance().initialize(new File("F:\\java\\IdeaProjects\\TorcsController\\out\\production\\torcs.properties"));
        EATorcs EA = new EATorcs(TOTAL_GENERATIONS);
        EA.initializePopulation();
        for (int i=0; i<=TOTAL_GENERATIONS; i++) {
            System.out.println("generation: " + i);
            EA.runTournament();
            EA.mutateGenome();
        }
        EA.saveNetworks("steering", "steering_nn.mem");
        //EA.saveNetworks("accelerate");
        //EA.saveNetworks("break");
    }

}
