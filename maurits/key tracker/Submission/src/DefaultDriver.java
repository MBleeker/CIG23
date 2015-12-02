
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Arrays;
/* Jorg: added some extra classes */

import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.controller.extras.*;
import cicontest.torcs.genome.IGenome;
import race.TorcsConfiguration;



/* Jorg End */

import Jama.Matrix;
import race.TorcsConfiguration;

public class DefaultDriver extends AbstractDriver {

	private NeuralNetwork MyNN;
	private NeuralNetwork MyNNAcc; // This network is to be used for acceleration
	private BufferedWriter logFile;
	private boolean logData = true;
	private AutoRecover recover = null;
	public static String OUTPUT_DIR = "memory/";
	public String output_dir;
	String nn_mem_file_steer = "DefaultDriver.mem";
	String nn_mem_file_acc = "nn_accelerate.mem";
	Boolean useNN;
	Boolean trainNN;
	Boolean keyTracking;
	KeyTracker keyTrack;
	Double currentSteering;
	int epochs = 0;
    
    public static boolean retrainNN = false;

	// SimpleDriver auxDriver = null;

	public DefaultDriver() {

		this.useNN = false;
		this.trainNN = false;
		this.keyTracking = true;
		this.currentSteering = 0.0;
		
		// System.out.println("Use NN " + this.useNN);
		this.initialize();
		if(this.keyTracking){
			this.keyTrack = new KeyTracker("Key Listener Tester");
		}
		this.output_dir = TorcsConfiguration.getInstance().getOptionalProperty("output_dir");
		
		if (this.output_dir == null || output_dir.trim() == "") {
			output_dir = OUTPUT_DIR;
		}
		// this.output_dir = "memory/";
		// add "output_dir" to torcs_properties file
		String filename = output_dir + "train_nn_data.dat";

		// initialize neural networks
		this.getNeuralNetworks();

		// only create log file if we want to collect data
		if (logData) {
			try {
				FileWriter fwriter = new FileWriter(filename, false);
				logFile = new BufferedWriter(fwriter);
				String Header = "9rangeValues;angleToTrackAxis,trackPosition;gear;steering;accelerate;brake;clutch";
				logFile.write(Header);
				logFile.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void getNeuralNetworks(){

		if (this.useNN) {
			// reuse stored networks
			System.out.println("Load NN's from memory....");
			this.MyNN = this.loadNN(this.output_dir + nn_mem_file_steer);
			//this.MyNNAcc = this.loadNN(this.output_dir + nn_mem_file_acc);
			System.out.println(Arrays.deepToString(this.MyNN.outputLayer.getWeightMatrix().getArray()));
		}
		else {
			// if new networks need to be trained
			if (this.trainNN) {
				System.out.println("Create new NN's for training....");
				// build neural network for steering
				this.MyNN = new NeuralNetwork();
				this.buildNN();
				// important: learning is set here for the NN. THIS CAN BE CHANGED
				MyNN.setLearningRate(0.1);
				// build neural network for acceleration and breaking
				this.MyNNAcc =  new NeuralNetwork();
				this.buildNNAcc(); // This is the acceleration network
				MyNNAcc.setLearningRate(0.01);
			}
		}
	}

	// methode stond er al in. Jorg heeft dingen toegevoegd om met ons netwerk te kunnen trainen
	public void loadGenome(IGenome genome) {	}

	// may be overdone, but separate method to build NN
	private void buildNN(){

		try {
			System.out.println("Build layers of steering network");
			this.MyNN.buildInputLayer(8, "tanh"); //
			this.MyNN.buildHiddenLayer(18, "tanh");
			this.MyNN.buildOutputLayer(1, "tanh");
		}
		catch (NeuralNetwork.WrongBuildSequence e){
			e.printStackTrace();
		}
	}

	private void buildNNAcc() {
		try {
			System.out.println("Build layers of acceleration network");
			this.MyNNAcc.buildInputLayer(8, "tanh");
			this.MyNNAcc.buildHiddenLayer(30, "tanh");
			this.MyNNAcc.buildOutputLayer(1, "tanh");
		}
		catch (NeuralNetwork.WrongBuildSequence e){
			e.printStackTrace();
		}

	}

	public double getAcceleration(SensorModel sensors) {
		// Maartje: used exact same structure as for the getSteering

		Matrix VectorAcceleration = createNNInputAccelerate(sensors);
		double[][] pValueAcceleration = MyNNAcc.processInput(VectorAcceleration).getArray();

		return pValueAcceleration[0][0];
		//return 1;
	}
	
	public double getSteering(SensorModel sensors){

		Matrix VectorSteering = createNNInputSteering(sensors);
		double[][] pValueSteering = MyNN.processInput(VectorSteering).getArray();
		// assuming the NN will return "a matrix" 1x1, so one value
		// in case we decide to output 2 values, e.g. steering and accelaration/breaking
		// then we have to change this.

		return pValueSteering[0][0];
	}

	public String getDriverName() {

		return "MMJ-controller-v0.1";
	}

	public void initialize(){

		if (this.useNN) {
			this.recover = new AutoRecover();
			this.enableExtras(new AutomatedClutch());
			this.enableExtras(new AutomatedGearbox());
			this.enableExtras(this.recover);
			this.enableExtras(new ABS());
		}
		else if (this.keyTracking) {
			System.out.println("Key tracking options");
			this.enableExtras(new AutomatedGearbox());
		}
		else {
			System.out.println("Default");
			this.enableExtras(new AutomatedClutch());
			this.enableExtras(new AutomatedGearbox());
			this.enableExtras(new AutomatedRecovering());
			this.enableExtras(new ABS());
		}

	}

	// this is the default controller that Ali built
	public void control(Action action, SensorModel sensors) {
		// System.out.println("I'm in control race now");
		// Example of a bot that drives pretty well; you can use this to generate data
		// Example of a bot that drives pretty well; you can use this to generate data
		if(!this.keyTracking){
			action.steering = DriversUtils.alignToTrackAxis(sensors, 0.5);
			if(sensors.getSpeed() > 60.0D) {
				action.accelerate = 0.0D;
				action.brake = 0.0D;
			}

			if(sensors.getSpeed() > 70.0D) {
				action.accelerate = 0.0D;
				action.brake = -1.0D;
			}

			if(sensors.getSpeed() <= 60.0D) {
				action.accelerate = (80.0D - sensors.getSpeed()) / 80.0D;
				action.brake = 0.0D;
			}

			if(sensors.getSpeed() < 30.0D) {
				action.accelerate = 1.0D;
				action.brake = 0.0D;
			}
		}
		if (logData) {
			logSensorAction(action, sensors);
		}

		if (this.trainNN) {
			this.trainNeuralNetwork(action, sensors);
		}

		if (this.useNN && !this.trainNN) {
			// recovering? don't use NN values
			 if (this.recover.getStuck() > 10) {
				System.out.println("*** Autorecovery in action, not using NN values ***");
			} else {
				useNeuralNetwork(action, sensors);
			}
		}
		if(this.keyTracking){
				if (this.keyTrack.getRightSteering() == 1) {
					this.currentSteering += 0.25;
					action.steering = this.currentSteering  ;
					System.out.println("right");
				}
				else if (this.keyTrack.getLeftSteering() == 1) {
					this.currentSteering -= 0.25;
					System.out.println("left");
					action.steering = this.currentSteering  ;
				} 
				else {
					this.currentSteering = 0.0;
				}
				
				if(this.keyTrack.accelerate() == 1){
					System.out.println("speed");
					action.accelerate = 1.0D;
					action.brake = 0.0D;
				}
				else if(this.keyTrack.doBreake() == 1){
					System.out.println("break");
					action.accelerate = 0.0D;
					action.brake = 1.0D;
					
				}
				else{
					action.accelerate = 0.0D;
					action.brake = 0.0D;
				}
				
			}
		
	}	

	private void trainNeuralNetwork(Action action, SensorModel sensors) {

		this.epochs++;
		Matrix inputVectorSteering = createNNInputSteering(sensors); //
		Matrix target = createNNTarget(action.steering);
		double pValueSteering = MyNN.trainNN(inputVectorSteering, target);
		System.out.println("pValueSteering <=> targetValue = " + pValueSteering + " <=> " + action.steering);

		// accelerating network --> assume that acceleration is based on steering, so we give the steering value as input to the network
		Matrix inputVectorAccelerate = createNNInputAccelerate(sensors);
		// System.out.println("made the input vector");
		Matrix targetAcc = createNNTarget(action.accelerate);
		// System.out.println("made the target vector");
		double pValueAccelerating = MyNNAcc.trainNN(inputVectorAccelerate, targetAcc);
		// System.out.println("found the accelerating value");
		System.out.println("pValueAccelerating <=> targetValue = " + pValueAccelerating + " <=> " + action.accelerate);

	}

	private void useNeuralNetwork(Action action, SensorModel sensors) {

		double psteer = this.getSteering(sensors);
		// double paccelerate = this.getAcceleration(sensors);
		// System.out.println("*** USE VALUE FOR Steering using pValue (pred <=> target) " + psteer + " <=> " + action.steering);
		// System.out.println("*** USE VALUE FOR Acceleration using pValue (pred <=> target) " + paccelerate + " <=> " + action.accelerate);
		System.out.println(psteer);
		action.steering = psteer;
		// action.accelerate = paccelerate;

	}
	// wordt standaard meegegeven
	private Matrix createNNInputSteering(SensorModel sensors){

		// we are passing now all 5 range values plus 1 for track angel
		// pass filtered input to the NN
		double[] limVector = Arrays.copyOfRange(sensors.getTrackEdgeSensors(), 5, 18); // zijn de 19 waarden die je aanroept --> welke sensor waarden staan in de papers. Wat is zinvolle info?
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getAngleToTrackAxis();
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getTrackPosition();
		return new Matrix(new double [][] {limVector}).transpose();
	}

	private Matrix createNNInputAccelerate(SensorModel sensors) {
		/*
		MAARTJE: Now I just used exactly the same values as for the steering, but playing around with this might
		reveal that we can better use different sensor values
		Of course also added the steering value itself
		*/

		double[] limVector = Arrays.copyOfRange(sensors.getTrackEdgeSensors(), 6, 11); // zijn de 19 waarden die je aanroept --> welke sensor waarden staan in de papers. Wat is zinvolle info?
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getAngleToTrackAxis();
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getTrackPosition();
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getSpeed();
		return new Matrix(new double [][] {limVector}).transpose();
	}

	private Matrix createNNTarget(double a){

		return new Matrix(new double[][] {{a}}).transpose();
	}

	/* Jorg: added new method to log sensor data and action data of car */ // --> dit heeft jorg gebouwd om training data te verkrijgen
	private void logSensorAction(Action a, SensorModel sensors){

		// double[] RangeSensors = Arrays.copyOfRange(sensors.getTrackEdgeSensors(), 4, 13); // 9 values
		double[] RangeSensors = sensors.getTrackEdgeSensors();
		String S_RangeSensors = "";
		for (double num : RangeSensors) {
			S_RangeSensors = S_RangeSensors + ((S_RangeSensors == "") ? "" : ";") + Double.toString(num);
		}
		// System.out.println(S_RangeSensors);
		// adding 3 values
		S_RangeSensors = S_RangeSensors + ";" + Double.toString(sensors.getAngleToTrackAxis()) + ";" + Double.toString(sensors.getTrackPosition()) + ";" + Double.toString(sensors.getSpeed());
		// adding 5 target values
		String Action_s = Double.toString(a.gear) + ";" + Double.toString(a.steering) + ";" + Double.toString(a.accelerate) + ";" + Double.toString(a.brake) + ";" + Double.toString(a.clutch);

		String OutPut = S_RangeSensors + ";" + Action_s;

		try {
			logFile.write(OutPut);
			logFile.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void defaultControl(Action action, SensorModel sensors){
		System.out.println("Default control...");
		action.clutch = 1;
		action.steering =  Math.random() * (1 - -1)  -1;
		action.accelerate = 1;
		action.brake = 0;
		System.out.println("defaultControl");
		//super.defaultControl(action, sensors);
	}

	private static double[] extendArraySize(double [] array){
		double [] temp = array.clone();
		array = new double[array.length + 1];
		System.arraycopy(temp, 0, array, 0, temp.length);
		return array;
	}

	public void exit() {
		if (this.trainNN) {
			this.storeNN(this.MyNN, this.output_dir + this.nn_mem_file_steer);
			this.storeNN(this.MyNNAcc, this.output_dir + this.nn_mem_file_acc);
			// System.out.println("Exit...");
		}
	}

	public void shutdown() {
		System.out.println("Shutdown...");
	}

	//Store the state of this neural network
	public void storeNN(NeuralNetwork nn, String inFile) {
		ObjectOutputStream out = null;
		try {
			//create the memory folder manually
			out = new ObjectOutputStream(new FileOutputStream(inFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.writeObject(nn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Load a neural network from memory
	public NeuralNetwork loadNN(String inFile) {

		// Read from disk using FileInputStream
		FileInputStream f_in = null;
		try {
			f_in = new FileInputStream(inFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Read object using ObjectInputStream
		ObjectInputStream obj_in = null;
		try {
			obj_in = new ObjectInputStream(f_in);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Read an object
		try {
			return (NeuralNetwork) obj_in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
