import java.io.*;

import java.util.Arrays;
import Jama.Matrix;

public class NeuralNetWorks {

	BufferedReader train_data;
	String infile = "F:/temp/trainNN/train_nn_data.dat";
	String infileTest = "F:/temp/trainNN/torcsRace_Aalborg.dat";
	String mem_steering_nn = "F:/temp/trainNN/steering_nn.mem";

	NeuralNetwork MyNN1;
	int epochs = 0;

	public BufferedReader getInputFile(String inFile) {

		BufferedReader freader = null;
		try {
			freader = new BufferedReader((new FileReader(inFile)));


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return freader;
	}

	public static String getDimMatrix(Matrix a) {

		int M = a.getColumnDimension();
		int N = a.getRowDimension();
		return "(" + N + ", " + M + ")";
	}

	public Matrix createTargetVector(String[] splits) {

		// double[] targetValueSteering = {Double.parseDouble(splits[23])};
		// System.out.println("Target " + Double.parseDouble(splits[20]));
		double[] targetValueSteering = {Double.parseDouble(splits[23])};
		return new Matrix(new double [][] {targetValueSteering}).transpose();
	}

	public Matrix createInputVector(String[] splits) {

		// 6 range values
		String[] inputRangeValues = Arrays.copyOfRange(splits, 5,18);

		double[] training_data = new double[15];
		// double[] training_data = new double[6];
		int i = 0;
		// convert training data to array of doubles
		for (String elem : inputRangeValues) {
			training_data[i] = Double.parseDouble(elem);
			i++;
		}
		// add angle to trackaxis and distance to trackaxis
		training_data[13] = Double.parseDouble(splits[19]); // angle to trackaxis
		training_data[14] = Double.parseDouble(splits[20]); // track position
		// speed training_data[7] = Double.parseDouble(splits[21]);

		return new Matrix(new double [][] {training_data}).transpose();
	}

	public double testNN(NeuralNetwork nn){

		double error = 0;
		double mse = 0;
		String sCurrentLine;
		this.epochs =0;
		Matrix targetVector = null;
		try {
			while ((sCurrentLine = this.train_data.readLine()) != null) {
				this.epochs++;
				// System.out.println("Line " + this.epochs);
				String[] splits = sCurrentLine.split(";");
				Matrix inputVector = this.createInputVector(splits);
				targetVector = this.createTargetVector(splits);
				Matrix predictedValue = nn.processInput(inputVector);
				error = targetVector.getArray()[0][0] - predictedValue.getArray()[0][0];
				System.out.println("Error = " + error);
				mse = mse + Math.pow((targetVector.getArray()[0][0] - predictedValue.getArray()[0][0]),2);

			}
			// System.out.println("TargetV " + targetVector.getArray()[0][0]);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return (mse/this.epochs);

	}

	public double trainNeuralNetwork(){

		String sCurrentLine;
		double mse = 0;
		double error = 0;
		this.epochs = 0;
		Matrix targetVector = null;
		Matrix inputVector = null;

		try {
			while ((sCurrentLine = this.train_data.readLine()) != null) {
				// System.out.println("Line " + this.epochs);
				String[] splits = sCurrentLine.split(";");
				inputVector = this.createInputVector(splits);
				targetVector = this.createTargetVector(splits);
				// System.out.println("Dim inputVector: " + getDimMatrix(inputVector) + " Dim targetVector: " + getDimMatrix(targetVector));
				double predictedValue = this.MyNN1.trainNN(inputVector, targetVector);
				error = Math.pow((targetVector.getArray()[0][0] - predictedValue),2);
				// System.out.println("Error = " + error);
				mse = mse + error;
				this.epochs++;
            }
			System.out.println("targetVector " + Arrays.deepToString(inputVector.getArray()));
			System.out.println("targetVector " + Arrays.deepToString(targetVector.getArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return (mse/this.epochs);

	}

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

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		NeuralNetWorks helper = new NeuralNetWorks();
		helper.train_data = helper.getInputFile(helper.infile);
		helper.MyNN1 = new NeuralNetwork();

		try {
			helper.MyNN1.buildInputLayer(15, "tanh");
			helper.MyNN1.buildHiddenLayer(34, "tanh");
			helper.MyNN1.buildOutputLayer(1, "tanh");
			helper.MyNN1.setLearningRate(0.01);
		}
		catch (NeuralNetwork.WrongBuildSequence e){
			e.printStackTrace();
		}

		double MSE = 0;
		// System.out.println(Arrays.deepToString(helper.MyNN1.outputLayer.getWeightMatrix().getArray()));

		helper.train_data = helper.getInputFile(helper.infile);
		MSE = helper.trainNeuralNetwork();
		System.out.println("MSE " + Double.toString(MSE) + " epochs " + helper.epochs);

		helper.storeNN(helper.MyNN1, helper.mem_steering_nn);

		NeuralNetwork tt = helper.loadNN(helper.mem_steering_nn);
		//System.out.println(Arrays.deepToString(tt.outputLayer.getWeightMatrix().getArray()));
		//System.out.println("test the network");

		helper.train_data = helper.getInputFile(helper.infileTest);
		MSE = helper.testNN(helper.MyNN1);
		System.out.println("MSE " + Double.toString(MSE) + " epochs " + helper.epochs);
		System.out.println(Arrays.deepToString(helper.MyNN1.outputLayer.getWeightMatrix().getArray()));

	}

}
