
import java.io.FileWriter;
import java.util.Arrays;
/* Jorg: added some extra classes */
import java.io.BufferedWriter;
import java.io.IOException;

import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.genome.IGenome;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.controller.extras.AutomatedRecovering;

/* Jorg End */

import Jama.Matrix;
import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;
import cicontest.torcs.genome.IGenome;

public class DefaultDriver extends AbstractDriver {

    private NeuralNetwork MyNN;
    private BufferedWriter logFile;

    /* Steering constants*/
	final float  stuckAngle = (float) 0.523598775;

	
    public DefaultDriver() {
		super();
		
		
		java.util.Date date= new java.util.Date();
		String filename = "F:/temp/torcsRace_" + this.getTrackName() + ".dat";
		
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

	public void loadGenome(IGenome genome) {
        if (genome instanceof DefaultDriverGenome) {
            DefaultDriverGenome MyGenome = (DefaultDriverGenome) genome;
			if (DefaultDriverAlgorithm.useNN) {
				System.out.println("Load NN from memory......");
				NeuralNetwork NN_temp = MyGenome.getMyNN();
				this.MyNN = NN_temp.loadGenome();
			}
			else {
				this.MyNN = MyGenome.getMyNN();
				this.buildNN();
				// important: learning is set here for the NN
				MyNN.setLearningRate(0.001);
			}
        } else {
            System.err.println("Invalid Genome assigned");
        }
    }

	// may be overdone, but separate method to build NN
	private void buildNN(){

		try {
			System.out.println("Build layers of NN");
			this.MyNN.buildInputLayer(6, "tanh");
			this.MyNN.buildHiddenLayer(3, "tanh");
			this.MyNN.buildOutputLayer(1, "tanh");
		}
		catch (NeuralNetwork.WrongBuildSequence e){
			e.printStackTrace();
		}
	}

    public double getAcceleration(SensorModel sensors) {

    return 1;
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
		this.enableExtras(new AutomatedClutch());
		this.enableExtras(new AutomatedGearbox());
		this.enableExtras(new AutomatedRecovering());
		this.enableExtras(new ABS());
	}

	public void control_ali(Action action, SensorModel sensors) {
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
		if (DefaultDriverAlgorithm.trainNN) {
			// if in trainings mode
			DefaultDriverAlgorithm.epochs++;
			Matrix inputVectorSteering = createNNInputSteering(sensors);
			Matrix target = createNNTarget(action.steering);
			double pValueSteering = MyNN.trainNN(inputVectorSteering, target);
			System.out.println("pValueSteering <=> targetValue = " + pValueSteering +  " <=> " + action.steering);
		}
		// only use NN if not in trainings mode and useNN is enabled
		if (DefaultDriverAlgorithm.useNN && !DefaultDriverAlgorithm.trainNN) {
			double psteer = this.getSteering(sensors);
			System.out.println("Using pValue (pred <=> target) " + psteer +  " <=> " + action.steering);
			action.steering = psteer;
		}
		// System.out.println(action.steering +"steering");
		// System.out.println(action.accelerate + "acceleration");
		// System.out.println(action.brake + "brake");
	}

    public void controlRace(Action action, SensorModel sensors) {

		boolean logData = false;

		// if(getStage() == cicontest.torcs.client.Controller.Stage.PRACTICE){
		this.controlWarmUp(action, sensors);

		if (logData) {
			logSensorAction(action, sensors);
		}
    	if (DefaultDriverAlgorithm.trainNN) {
			// if in trainings mode
			DefaultDriverAlgorithm.epochs++;
			Matrix inputVectorSteering = createNNInputSteering(sensors);
			Matrix target = createNNTarget(action.steering);
			double pValueSteering = MyNN.trainNN(inputVectorSteering, target);
			System.out.println("pValueSteering <=> targetValue = " + pValueSteering +  " <=> " + action.steering);

		}
		// only use NN if not in trainings mode and useNN is enabled
		if (DefaultDriverAlgorithm.useNN && !DefaultDriverAlgorithm.trainNN) {
			double psteer = this.getSteering(sensors);
			System.out.println("Using pValue (pred <=> target) " + psteer +  " <=> " + action.steering);
			action.steering = psteer;
		}
        //super.ControlRace(action, sensors);
    }

	private Matrix createNNInputSteering(SensorModel sensors){

		// we are passing now all 5 range values plus 1 for track angel
		// pass filtered input to the NN
		double[] limVector = Arrays.copyOfRange(sensors.getTrackEdgeSensors(), 6, 11);
		limVector = extendArraySize(limVector);
		limVector[limVector.length-1] = sensors.getAngleToTrackAxis();
		return new Matrix(new double [][] {limVector}).transpose();
	}

	private Matrix createNNTarget(double a){

		return new Matrix(new double[][] {{a}}).transpose();
	}

	/* Jorg: added new method to log sensor data and action data of car */
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
}
