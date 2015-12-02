import cicontest.torcs.race.RaceResults;
import race.TorcsConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import Jama.Matrix;

/**
 * Created by Jörg on 29-11-2015.
 */

public class EATorcs {

    // constants
    private static int NUM_OF_INPUT_UNITS = 15;
    private static int NUM_OF_INITIAL_HIDDEN_NODES = 10;
    private static int NUM_OF_OUTPUT_UNITS = 1;
    private static String ACTIVATION_FUNCTION = "tanh";
    private static double LEARNING_RATE = 0.01;
    private static String OUTPUT_DIR = "C:\\temp\\";
    private static int MAX_COMPETITORS = 3;

    // class variables
    private double prob_mu_genome = 0.4;
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
        this.output_dir = TorcsConfiguration.getInstance().getOptionalProperty("mem_location");
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
                    this.mem_files[1][i-1] = this.output_dir + i + "_" + inFile;
                    dd.MyNNAcc = DefaultDriver.loadNN(abs_path);
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
        this.mutateGenome();

    }

    public void runTournament(){

        DefaultDriver[] drivers = new DefaultDriver[MAX_COMPETITORS];
        this.survivors = new ArrayList<>();
        this.tt = new Tournament("road", "aalborg", 1);
        Collections.shuffle(this.population);

        int i = 0;
        int total = 0;
        for (DefaultDriver dd: this.population) {
            drivers[i] = dd;
            i++;
            total++;
            if (i == MAX_COMPETITORS || (this.population.size() == total )) {
                tt.run(drivers, true);
                i = 0;
                if ( this.population.size() - total < MAX_COMPETITORS ) {
                    System.out.println("Last tournament with " + (this.population.size() - total) + " drivers.");
                    drivers = new DefaultDriver[this.population.size() - total];
                } else {
                    drivers = new DefaultDriver[MAX_COMPETITORS];
                }
                this.tt.printResults();

            }
        }
    }

    public void selectParents(){

    }

    public void addNodeToHiddenLayer(NeuralNetwork nn) {

        /*
        Basically if a new hidden layer node is added we need to add:
            (1) a row vector to the weight matrix from input->hidden layer
            (2) a column vector to the weight matrix from hidden->output layer
            NOTE: the method ASSUMES that our networks have 1 HIDDEN LAYER!!!!
         */
        int n, m;
        for (NetworkLayer aLayer : nn.allLayers) {
            if (aLayer.getWeightMatrix() != null) {
                n = aLayer.getWeightMatrix().getRowDimension();
                m = aLayer.getWeightMatrix().getColumnDimension();

                if (aLayer.isHiddenLayer()){
                    Matrix row = Matrix.random(1, m);


                } else {
                    if (aLayer.isOutputLayer()) {

                    }
                }
            }
        }
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

    public void mutateGenome(){

        /*
            only mutate weight matrices very slightly by perturbation
         */
        for (int i=0; i< this.population.size() ; i++) {
            if (Math.random() < prob_mu_genome) {
                System.out.println("Mutate weights for driver " + i);
                // population.get(i).MyNNSteer.outputLayer.setWeightMatrix(this.perturbateWeights( population.get(i).MyNNSteer.outputLayer.getWeightMatrix()));
                this.perturbateWeights(population.get(i).MyNNSteer.outputLayer);
            }
        }

    }

    public static void main(String[] args){

        TorcsConfiguration.getInstance().initialize(new File("F:\\java\\IdeaProjects\\TorcsController\\out\\production\\torcs.properties"));
        EATorcs EA = new EATorcs(MAX_COMPETITORS);
        EA.initializePopulation();
        EA.runTournament();
        EA.saveNetworks("steering");
    }

}
