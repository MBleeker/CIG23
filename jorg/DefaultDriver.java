
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
import sun.management.Sensor;

public class DefaultDriver extends AbstractDriver {

	private NeuralNetwork MyNN;
	private NeuralNetwork MyNNAcc; // This network is to be used for acceleration
	private BufferedWriter logFile;
	private boolean logData = true;
	private AutoRecover recover;
	String output_dir = "c:\\temp\\";
	String nn_mem_file_steer = "nn_steering.mem";
	String nn_mem_file_acc = "nn_accelerate.mem";

	public DefaultDriver() {

		this.initialize();
		java.util.Date date= new java.util.Date();
		this.output_dir = TorcsConfiguration.getInstance().getOptionalProperty("output_dir");
		// add "output_dir" to torcs_properties file
		String filename = output_dir + this.getTrackName() + ".dat";

		// initialize neural networks
		this.getNeuralNetworks();

		try {
			FileWriter fwriter = new FileWriter(filename, false);
			logFile = new BufferedWriter(fwriter);
			logFile.write(date.toString());
			logFile.newLine();
			String Header = "19rangeValues;gear;steering;accelerate;brake;clutch";
			logFile.write(Header);
			logFile.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated constructor stub
	}

	public void getNeuralNetworks(){

		if (DefaultDriverAlgorithm.useNN || DefaultDriverAlgorithm.retrainNN) {
			// reuse stored networks
			System.out.println("Load NN's from memory....");
			this.MyNN = this.loadNN(this.output_dir + nn_mem_file_steer);
			this.MyNNAcc = this.loadNN(this.output_dir + nn_mem_file_acc);
		}
		else {
			// if new networks need to be trained
			if (DefaultDriverAlgorithm.trainNN) {
				System.out.println("Create new NN's for training....");
				// build neural network for steering
				this.MyNN = new NeuralNetwork();
				this.buildNN();
				// important: learning is set here for the NN. THIS CAN BE CHANGED
				MyNN.setLearningRate(0.001);
				// build neural network for acceleration and breaking
				this.MyNNAcc =  new NeuralNetwork();
				this.buildNNAcc(); // This is the acceleration network
				MyNNAcc.setLearningRate(0.001);
			}
		}
	}

	// methode stond er al in. Jorg heeft dingen toegevoegd om met ons netwerk te kunnen trainen
	public void loadGenome(IGenome genome) {	}

	// may be overdone, but separate method to build NN
	private void buildNN(){

		try {
			System.out.println("Build layers of steering network");
			this.MyNN.buildInputLayer(7, "tanh"); //
			this.MyNN.buildHiddenLayer(40, "tanh");
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
			this.MyNNAcc.buildHiddenLayer(40, "tanh");
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

	public void controlQualification(Action action, SensorModel sensors) {
		action.clutch = 1;
		action.steering =  Math.random() * (1 - -1)  -1;
		action.accelerate = 5;
		action.brake = 0;
		System.out.println("controlQualification...");
		//super.controlQualification(action, sensors);
	}

	public void initialize(){
		this.recover = new AutoRecover();
		this.enableExtras(new AutomatedClutch());
		this.enableExtras(new AutomatedGearbox());
		this.enableExtras(this.recover);
		this.enableExtras(new ABS());
	}

	// this is the default controller that Ali built
	public void control(Action action, SensorModel sensors) {
		System.out.println("I'm in control race now");
		// Example of a bot that drives pretty well; you can use this to generate data
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
		
		if (logData) {
			logSensorAction(action, sensors);
		}
		// System.out.println(action.steering +"steering");
		// System.out.println(action.accelerate + "acceleration");
		// System.out.println(action.brake + "brake");
		// if in training or retrain mode
		if (DefaultDriverAlgorithm.trainNN || DefaultDriverAlgorithm.retrainNN) {
			this.trainNeuralNetwork(action, sensors);
		}

		// recovering? don't use NN values
		if (this.recover.getStuck() > 10) {
			System.out.println("*** Autorecovery in action, not using NN values ***");
		} else {
			if (DefaultDriverAlgorithm.useNN && !DefaultDriverAlgorithm.trainNN && !DefaultDriverAlgorithm.retrainNN) {
				useNeuralNetwork(action, sensors);
			}
		}
	}

	// wordt elke 10 mm seconde aangeroepen --> Action terugsturen naar game server
	public void control_custom(Action action, SensorModel sensors) {

		boolean logData = false;

		// if(getStage() == cicontest.torcs.client.Controller.Stage.PRACTICE){
		this.controlWarmUp(action, sensors); // dit zorgt ervoor dat de auto op de weg blijft --> al een goede chauffeur. Rijdt over de track

		if (logData) {
			logSensorAction(action, sensors);
		}
		// if in training or retrain mode
		if (DefaultDriverAlgorithm.trainNN || DefaultDriverAlgorithm.retrainNN) {
			this.trainNeuralNetwork(action, sensors);
		}

		// recovering? don't use NN values
		if (this.recover.getStuck() > 10) {
			System.out.println("*** Autorecovery in action, not using NN values ***");
		} else {
			if (DefaultDriverAlgorithm.useNN && !DefaultDriverAlgorithm.trainNN && !DefaultDriverAlgorithm.retrainNN) {
				useNeuralNetwork(action, sensors);
			}
		}
	}

	private void trainNeuralNetwork(Action action, SensorModel sensors) {

		DefaultDriverAlgorithm.epochs++;
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
		double paccelerate = this.getAcceleration(sensors);
		System.out.println("Steering using pValue (pred <=> target) " + psteer + " <=> " + action.steering);
		System.out.println("Acceleration using pValue (pred <=> target) " + paccelerate + " <=> " + action.accelerate);
		action.steering = psteer;
		action.accelerate = paccelerate;

	}
	// wordt standaard meegegeven
	private Matrix createNNInputSteering(SensorModel sensors){

		// we are passing now all 5 range values plus 1 for track angel
		// pass filtered input to the NN
		double[] limVector = Arrays.copyOfRange(sensors.getTrackEdgeSensors(), 6, 11); // zijn de 19 waarden die je aanroept --> welke sensor waarden staan in de papers. Wat is zinvolle info?
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

		double[] RangeSensors = sensors.getTrackEdgeSensors();
		String S_RangeSensors = "";
		for (double num : RangeSensors) {
			S_RangeSensors = S_RangeSensors + ((S_RangeSensors == "") ? "" : ";") + num;
		}

		String Action_s = a.gear + ";" + a.steering + ";" + a.accelerate + ";" + a.brake + ";" + a.clutch;

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
		this.storeNN(this.MyNN, this.output_dir + this.nn_mem_file_steer);
		this.storeNN(this.MyNNAcc, this.output_dir + this.nn_mem_file_acc);
		System.out.println("Exit...");
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
