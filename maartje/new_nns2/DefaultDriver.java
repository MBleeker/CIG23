
import java.io.*;
import java.util.Arrays;
/* Jorg: added some extra classes */

import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.algorithm.abstracts.map.TrackMap;
import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.controller.extras.*;
import cicontest.torcs.genome.IGenome;
import race.TorcsConfiguration;


/* Jorg End */

import Jama.Matrix;
import race.TorcsConfiguration;

public class DefaultDriver extends AbstractDriver {

	public NeuralNetwork MyNNSteer;
	public NeuralNetwork MyNNAcc; // This network is to be used for acceleration
	private NeuralNetwork MyNNBreak;
	private BufferedWriter logFile;
	private boolean logData = false;
	private AutoRecover recover = null;
	public static String OUTPUT_DIR = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/out";
	public String output_dir;
	String nn_mem_file_steer = "steering_nn.mem";
	String nn_mem_file_acc = "acc_nn.mem";
	String nn_mem_file_break = "break_nn.mem";
	Boolean useNN;
	Boolean trainNN;
	int epochs = 0;
	String driverID = "default";
	int fittness = 0;

	public DefaultDriver(String ID){

		this.driverID = ID;
		this.useNN = true;
		this.trainNN = false;
		this.initialize();
		this.output_dir = TorcsConfiguration.getInstance().getOptionalProperty("output_dir");
		if (output_dir.trim() == "") {
			output_dir = OUTPUT_DIR;
		}
	}

	public DefaultDriver() {

		this.useNN = true;
		this.trainNN = false;
		// System.out.println("Use NN " + this.useNN);
		this.initialize();
		this.output_dir = TorcsConfiguration.getInstance().getOptionalProperty("output_dir");
		if (output_dir.trim() == "") {
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
			this.MyNNSteer = this.loadNN(this.output_dir + nn_mem_file_steer);
			this.MyNNAcc = this.loadNN(this.output_dir + nn_mem_file_acc);
			this.MyNNBreak = this.loadNN(this.output_dir + nn_mem_file_break);
			System.out.println(Arrays.deepToString(this.MyNNSteer.outputLayer.getWeightMatrix().getArray()));
		}
		else {
			// if new networks need to be trained
			if (this.trainNN) {
				System.out.println("Create new NN's for training....");
				// build neural network for steering
				this.MyNNSteer = new NeuralNetwork();
				this.buildNN();
				// important: learning is set here for the NN. THIS CAN BE CHANGED
				MyNNSteer.setLearningRate(0.1);
				// build neural network for acceleration and breaking
				this.MyNNAcc =  new NeuralNetwork();
				this.buildNNAcc(); // This is the acceleration network
				MyNNAcc.setLearningRate(0.01);
				this.MyNNBreak = new NeuralNetwork();
				this.buildNNBreak();
				MyNNBreak.setLearningRate(0.01);
			}
		}
	}

	// methode stond er al in. Jorg heeft dingen toegevoegd om met ons netwerk te kunnen trainen
	public void loadGenome(IGenome genome) {	}

	// may be overdone, but separate method to build NN
	private void buildNN(){

		try {
			System.out.println("Build layers of steering network");
			this.MyNNSteer.buildInputLayer(8, "tanh"); //
			this.MyNNSteer.buildHiddenLayer(18, "tanh");
			this.MyNNSteer.buildOutputLayer(1, "tanh");
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

	private void buildNNBreak() {
		try {
			System.out.println("Build layers of brake network");
			this.MyNNBreak.buildInputLayer(8, "tanh");
			this.MyNNBreak.buildHiddenLayer(30, "tanh");
			this.MyNNBreak.buildOutputLayer(1, "tanh");
		}
		catch (NeuralNetwork.WrongBuildSequence e){
			e.printStackTrace();
		}

	}


	public double getBreak(SensorModel sensors) {
		// Maartje: used exact same structure as for the getSteering

		// here you can write an if statement --> if you can look ahead for such and such then --> go

		Matrix VectorBreak = createNNInputBreak(sensors);
		double[][] pValueBreak = MyNNBreak.processInput(VectorBreak).getArray();

		return pValueBreak[0][0];

	}

	public double getAcceleration(SensorModel sensors) {
		// Maartje: used exact same structure as for the getSteering

		// here you can write an if statement --> if you can look ahead for such and such then --> go

		Matrix VectorAcceleration = createNNInputAccelerate(sensors);
		double[][] pValueAcceleration = MyNNAcc.processInput(VectorAcceleration).getArray();

		return pValueAcceleration[0][0];
		//return 1;
	}
	public double getSteering(SensorModel sensors){

		Matrix VectorSteering = createNNInputSteering(sensors);
		double[][] pValueSteering = MyNNSteer.processInput(VectorSteering).getArray();
		// assuming the NN will return "a matrix" 1x1, so one value
		// in case we decide to output 2 values, e.g. steering and accelaration/breaking
		// then we have to change this.

		return pValueSteering[0][0];
	}

	public String getDriverName() {

		return driverID + "_MMJ-controller-v1.0";
	}

	public void initialize(){

		if (this.useNN) {
			this.recover = new AutoRecover();
			this.enableExtras(new AutomatedClutch());
			this.enableExtras(new AutomatedGearbox());
			this.enableExtras(this.recover);
			this.enableExtras(new ABS());
		} else {
			this.enableExtras(new AutomatedClutch());
			this.enableExtras(new AutomatedGearbox());
			this.enableExtras(new AutomatedRecovering());
			this.enableExtras(new ABS());
		}

	}

	// this is the default controller that Ali built
	public void control(Action action, SensorModel sensors) {
		System.out.println("I'm in control race now");
		// Example of a bot that drives pretty well; you can use this to generate data
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

		// action = this.auxDriver.aux_control(sensors);
		// System.out.println(action.steering +"steering");
		// System.out.println(action.accelerate + "acceleration");
		// System.out.println(action.brake + "brake");
		// if in training or retrain mode
		if (this.trainNN) {
			this.trainNeuralNetwork(action, sensors);
		}

		if (this.useNN && !this.trainNN) {
			// recovering? don't use NN values
			if (this.recover.getStuck() > 5) {
				System.out.println("*** Autorecovery in action, not using NN values ***");
			} else {

				useNeuralNetwork(action, sensors);


				//double force = 0.5;
				//double evadeforce = 0.5;

				//double[] sensor = sensors.getOpponentSensors();
				//System.out.println("Sensor to oppponents: " + sensor.toString());

				 /*//if (....) {p
					 DriversUtils du = new DriversUtils();
					 du.evadeOpponents(action, sensors, this.trackmap, force, evadeforce);
				 //} */

				//System.out.println("Opponents position: " + DriversUtils.getOpponentPosition(action, sensors));
				double[] rangeValues = Arrays.copyOfRange(sensors.getTrackEdgeSensors(), 7, 10);
				System.out.println("range1: " + rangeValues[0] + "range2: " + rangeValues[1] + "range3: " + rangeValues[2]);

				if (rangeValues[2] > 60)  { //this is only the real front??
					System.out.println("large empty part ahead of me");
					action.accelerate = 1.0D;
				}

				double[] rangeValuesBrake = Arrays.copyOfRange(sensors.getTrackEdgeSensors(), 1, 19);
				System.out.println("range1: " + rangeValuesBrake[0] + "range2: " + rangeValuesBrake[1] + "range3: " + rangeValuesBrake[16] + "range4: " + (rangeValuesBrake[17]));
				if ((rangeValuesBrake[0] < 2 && rangeValuesBrake[1] < 2) || (rangeValuesBrake[16] < 2 && rangeValuesBrake[17] < 2) && (rangeValuesBrake[0] > 0 && rangeValuesBrake[1] > 0) && (rangeValuesBrake[16] > 0 && rangeValuesBrake[17] > 0)) {
					System.out.println("BREAK!!!");
					action.brake = 0.1D;
				}

			}
		}
	}

	private void trainNeuralNetwork(Action action, SensorModel sensors) {

		this.epochs++;
		Matrix inputVectorSteering = createNNInputSteering(sensors); //
		Matrix target = createNNTarget(action.steering);
		double pValueSteering = MyNNSteer.trainNN(inputVectorSteering, target);
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
		double pbreak = this.getBreak(sensors);
		System.out.println("*** USE VALUE FOR Steering using pValue (pred <=> target) " + psteer + " <=> " + action.steering);
		System.out.println("*** USE VALUE FOR Acceleration using pValue (pred <=> target) " + paccelerate + " <=> " + action.accelerate);
		System.out.println("*** USE VALUE FOR Brake using pValue (pred <=> target) " + pbreak + " <=> " + action.brake);
		action.steering = psteer;
		action.accelerate = paccelerate;
		action.brake = pbreak;

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

		double[] limVector = Arrays.copyOfRange(sensors.getTrackEdgeSensors(), 1, 18); // zijn de 19 waarden die je aanroept --> welke sensor waarden staan in de papers. Wat is zinvolle info?
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getAngleToTrackAxis();
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getTrackPosition();
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getSpeed();
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getGear();
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = getSteering(sensors);
		return new Matrix(new double [][] {limVector}).transpose();
	}

	private Matrix createNNInputBreak(SensorModel sensors) {
		/*
		MAARTJE: Now I just used exactly the same values as for the steering, but playing around with this might
		reveal that we can better use different sensor values
		Of course also added the steering value itself
		*/

		double[] limVector = Arrays.copyOfRange(sensors.getTrackEdgeSensors(), 1, 18); // zijn de 19 waarden die je aanroept --> welke sensor waarden staan in de papers. Wat is zinvolle info?
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getAngleToTrackAxis();
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getTrackPosition();
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getSpeed();
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getGear();
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = getSteering(sensors);
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
			this.storeNN(this.MyNNSteer, this.output_dir + this.nn_mem_file_steer);
			this.storeNN(this.MyNNAcc, this.output_dir + this.nn_mem_file_acc);
			System.out.println("Exit...");
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
	public static NeuralNetwork loadNN(String inFile) {

		// Read from disk using FileInputStream
		InputStream f_in = DefaultDriver.class.getResourceAsStream(inFile);

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
