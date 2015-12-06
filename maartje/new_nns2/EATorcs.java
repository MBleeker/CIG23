import cicontest.torcs.race.RaceResults;
import com.sun.xml.internal.ws.api.pipe.SyncStartForAsyncFeature;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import race.TorcsConfiguration;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.*;

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
    private static int MIN_FITNESS = 2; // this is the fitness a driver minally needs to have to pass to the next round. This can change if you want to take damage into account for example
    private static int TOTAL_GENERATIONS = 3;
    private static int GENERATION_SIZE = 4; //CHANGE!!
    private static String ACTIVATION_FUNCTION = "tanh";
    private static double LEARNING_RATE = 0.01;
    private static String OUTPUT_DIR = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/out/";
    private static int MAX_COMPETITORS = 4;
    private static String TRAININGS_FILE = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/trainNN/train_nn_data_all.dat";

    // class variables
    private double prob_mu_genome = 0.6;
    private double prob_mu_weight = 0.9;
    private double prob_new_weight = 1 - prob_mu_weight;
    private int initial_pop_size;
    private double perc_survivors = 0.5;
    private String output_dir;
    private ArrayList<DefaultDriver> population;
    private ArrayList<DefaultDriver> survivors;
    private Tournament tt;
    private String[][] mem_files;


    // constructor
    public EATorcs(int pop_size){

        this.setInitial_pop_size(pop_size);
        population = new ArrayList<>();
        this.output_dir = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/out/";
        if (output_dir.trim() == "") {
            output_dir = OUTPUT_DIR;
        }
        this.mem_files = new String[2][pop_size];
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

    public void loadNetworks(String inFile, String typeNN, Boolean uniSource){

        int i = 1;
        String abs_path=null;

        for (DefaultDriver dd: this.population){

            abs_path = this.output_dir + (uniSource ? "" : (i + "_"))  + inFile;

            switch (typeNN) {
                case "steering":
                    this.mem_files[0][i-1] = this.output_dir + i + "_" + inFile;
                    dd.MyNNSteer = DefaultDriver.loadNN(abs_path);
                    break;
                case "accelerate":
                    System.out.println("loading accelerating network");
                    this.mem_files[1][i-1] = this.output_dir + i + "_" + inFile;
                    dd.MyNNAcc = DefaultDriver.loadNN(abs_path);
                    break;
                case "break":
                    System.out.println("loading braking network");
                    this.mem_files[1][i-1] = this.output_dir + i + "_" + inFile;
                    dd.MyNNBreak = DefaultDriver.loadNN(abs_path);
                    System.out.println("yooo "+ dd.MyNNBreak);
                    break;
            }
            System.out.println(i + " mem file " + abs_path);
            i++;
        }
    }

    public void saveNetworks(String typeNN){

        for (int i=0; i< this.population.size() ; i++){
            switch (typeNN) {
                case "steering":
                    System.out.println("Save network to " + this.mem_files[0][i]);
                    population.get(i).storeNN(population.get(i).MyNNSteer, this.mem_files[0][i]);
                    break;
                case "accelerate":
                    population.get(i).storeNN(population.get(i).MyNNAcc, this.mem_files[1][i]);
                    break;
                case "break":
                    population.get(i).storeNN(population.get(i).MyNNBreak, this.mem_files[1][i]);
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

    public void loadAndMutate() {
        System.out.println("Loading networks...");
        this.loadNetworks("steering_nn.mem", "steering", true);
        //this.loadNetworks("acc_nn.mem", "accelerate", true);
        //this.loadNetworks("break_nn.mem", "break", true);
        System.out.println("Mutating Genome...");
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
                drivers[i].fitness = i+1; // for testing!!!!

                i++;
                total++;
                if (i == MAX_COMPETITORS || (this.population.size() == total )) {
                    System.out.println("Starting new tournament...");
                    //tt.run(drivers, true);
                    //int[] fitness = this.tt.getResults();

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
        for (int i=0; i<survivors.size(); i++) {
            if (family_members < GENERATION_SIZE) {
                System.out.println("number family members: " + family_members);

                family_members++;
                NeuralNetwork oldNNSteer = survivors.get(i).MyNNSteer;
                NetworkLayer input = oldNNSteer.getAllLayers().get(0); // ik hoop heel erg dat dit een deep copy is
                NetworkLayer hidden = oldNNSteer.getAllLayers().get(1);
                NetworkLayer output = oldNNSteer.getAllLayers().get(2);
                NeuralNetwork newNNSteer = new NeuralNetwork(input, hidden, output);
                //newGeneration.add(oldNNSteer);
                //newGeneration.add(newNNSteer);

                //population.get(i).MyNNSteer = oldNNSteer;
                //population.get(i+2).MyNNSteer = newNNSteer; // like this you fill the entire array again with new and old networks

                driver = new DefaultDriver(Integer.toString(i));
                driver.MyNNSteer = oldNNSteer;
                population.add(driver);
                driver2 = new DefaultDriver(Integer.toString(i+1));
                driver2.MyNNSteer = newNNSteer;
                population.add(driver2);

                System.out.println("population size: " + population.size());
                // All testing:
                /*System.out.println("old nw lr: " + survivors.get(i).MyNNSteer.getLearningRate());
                System.out.println("new nw lr: " + newNNSteer.getLearningRate());

                newNNSteer.setLearningRate(5);
                System.out.println("old nw lr after adaptation: " + survivors.get(i).MyNNSteer.getLearningRate());
                System.out.println("new nw lr after adaptation: " + newNNSteer.getLearningRate()); */
            }
        }
        //this.population.clear();
        this.population = population;
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
                population.get(i).MyNNSteer = this.trainNetwork(population.get(i).MyNNSteer, learning_rate_steer);

                /*System.out.println("Mutating weights for accelerating network...");
                population.get(i).MyNNAcc = this.addNodeToHiddenLayer(population.get(i).MyNNAcc);
                this.perturbateWeights(population.get(i).MyNNAcc.outputLayer);
                double learning_rate_acc = this.mutateLearningRate();
                population.get(i).MyNNAcc = this.trainNetworkAcc(population.get(i).MyNNAcc, learning_rate_acc);

                System.out.println("Mutating weights for braking network...");
                System.out.println(population.get(i).MyNNBreak);
                population.get(i).MyNNBreak = this.addNodeToHiddenLayer(population.get(i).MyNNBreak);
                this.perturbateWeights(population.get(i).MyNNBreak.outputLayer);
                double learning_rate_break = this.mutateLearningRate();
                population.get(i).MyNNBreak = this.trainNetworkBreak(population.get(i).MyNNBreak, learning_rate_break);*/
            }
        }

    }

    public static void main(String[] args){

        TorcsConfiguration.getInstance().initialize(new File("C:/Users/Maartje/Documents/Studie/master/ci/project/files/torcs.properties"));
        EATorcs EA = new EATorcs(MAX_COMPETITORS);
        EA.initializePopulation();
        for (int i=0; i<=TOTAL_GENERATIONS; i++) {
            System.out.println("generation: " + i);
            EA.runTournament();
            EA.saveNetworks("steering");
            EA.loadAndMutate();
        }
        //EA.saveNetworks("accelerate");
        //EA.saveNetworks("break");
    }

}
