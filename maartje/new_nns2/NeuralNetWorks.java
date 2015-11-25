import java.io.*;

import java.util.Arrays;
import Jama.Matrix;

public class NeuralNetWorks {

	BufferedReader train_data;
	String infile = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/trainNN/train_nn_data_all.dat";
	String infileTest = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/trainNN/train_nn_data_all.dat";
	String mem_steering_nn = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/out/steering_nn.mem";
	String mem_acc_nn = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/out/acc_nn.mem";

	NeuralNetwork MyNN1;
	NeuralNetwork myNNAcc;
	int epochs = 0;
	int epochsAcc = 0;

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

	public Matrix createTargetVectorAcc(String[] splits) {
		double[] targetValueAcc = {Double.parseDouble(splits[24])}; // value for speed in the data
		return new Matrix(new double [][] {targetValueAcc}).transpose();
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

	public Matrix createInputVectorAcc(String[] splits) {

		String[] inputRangeValuesAcc = Arrays.copyOfRange(splits, 5, 18);

		double[] trainingDataAcc = new double[17];
		int i = 0;

		// these are the 'normal' input values
		for (String elem : inputRangeValuesAcc) {
			trainingDataAcc[i] = Double.parseDouble(elem);
			i++;
		}

		// angle to track axis, distance to track axis, speed, gear
		trainingDataAcc[13] = Double.parseDouble(splits[19]); // angle
		trainingDataAcc[14] = Double.parseDouble(splits[20]); // position
		trainingDataAcc[15] = Double.parseDouble(splits[21]); // speed
		trainingDataAcc[16] = Double.parseDouble(splits[22]); // gear

		return new Matrix(new double [][] {trainingDataAcc}).transpose();
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
				//System.out.println("Error = " + error);
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
				//System.out.println("Steering Line " + this.epochs);
				String[] splits = sCurrentLine.split(";");
				inputVector = this.createInputVector(splits);
				targetVector = this.createTargetVector(splits);
				//System.out.println("target vector steering: " + Arrays.deepToString(targetVector.getArray()));
				// System.out.println("Dim inputVector: " + getDimMatrix(inputVector) + " Dim targetVector: " + getDimMatrix(targetVector));
				double predictedValue = this.MyNN1.trainNN(inputVector, targetVector);
				error = Math.pow((targetVector.getArray()[0][0] - predictedValue),2);
				//System.out.println("Error = " + error);
				mse = mse + error;


				this.epochs++;


            }
			System.out.println("intputVectorSteering " + Arrays.deepToString(inputVector.getArray()));
			System.out.println("targetVectorSteering " + Arrays.deepToString(targetVector.getArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return (mse/this.epochs);

	}

	public double trainNeuralNetworkAcc() {
		String sCurrentLine;
		double mseAcc = 0;
		double errorAcc = 0;
		Matrix targetVectorAcc = null;
		Matrix inputVectorAcc = null;
		this.epochsAcc = 0;

		try {
			while ((sCurrentLine = this.train_data.readLine()) != null) {
				//System.out.println("Acc Line " + this.epochsAcc);
				String[] splits = sCurrentLine.split(";");
				inputVectorAcc = this.createInputVectorAcc(splits);
				//System.out.println("input vector Acc: " + Arrays.deepToString(inputVectorAcc.getArray()));
				targetVectorAcc = this.createTargetVectorAcc(splits);
				//System.out.println("target vector Acc: " + Arrays.deepToString(targetVectorAcc.getArray()));
				double predictedValueAcc = this.myNNAcc.trainNN(inputVectorAcc, targetVectorAcc);
				//System.out.println("target prediction Acc: " + predictedValueAcc);
				errorAcc = Math.pow((targetVectorAcc.getArray()[0][0] - predictedValueAcc),2);
				//System.out.println("MSE Acc rep: " + mseAcc);
				mseAcc += errorAcc;

				this.epochsAcc++;
			}

			System.out.println("inputVectorAcc " + Arrays.deepToString(inputVectorAcc.getArray()));
			System.out.println("targetVectorAcc " + Arrays.deepToString(targetVectorAcc.getArray()));

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("MSE Acc: " + mseAcc/this.epochsAcc);
		//System.out.println()
		return (mseAcc/this.epochsAcc);

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

	public void storeNNAcc(NeuralNetwork nnAcc, String inFile) {
		ObjectOutputStream out = null;

		try {
			out = new ObjectOutputStream(new FileOutputStream(inFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.writeObject(nnAcc);
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

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		NeuralNetWorks helper = new NeuralNetWorks();
		NeuralNetWorks helper2 = new NeuralNetWorks();
		helper.train_data = helper.getInputFile(helper.infile);
		helper.MyNN1 = new NeuralNetwork();
		helper2.train_data = helper2.getInputFile(helper2.infile);
		helper2.myNNAcc = new NeuralNetwork();

		try {
			helper.MyNN1.buildInputLayer(15, "tanh");
			helper.MyNN1.buildHiddenLayer(34, "tanh");
			helper.MyNN1.buildOutputLayer(1, "tanh");
			helper.MyNN1.setLearningRate(0.01);

			helper2.myNNAcc.buildInputLayer(17, "tanh");
			helper2.myNNAcc.buildHiddenLayer(34, "tanh"); // seems to be the best value (trial and error)
			helper2.myNNAcc.buildOutputLayer(1, "tanh");
			helper2.myNNAcc.setLearningRate(0.01); // seems to be the best value (trial and error)
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

		double MSEAcc = 0;

		helper2.train_data = helper2.getInputFile(helper2.infile);
		MSEAcc = helper2.trainNeuralNetworkAcc();
		System.out.println("MSEAcc " + Double.toString(MSEAcc) + " epochs " + helper2.epochsAcc);
		helper2.storeNNAcc(helper2.myNNAcc, helper2.mem_acc_nn);


		//NeuralNetwork tt = helper.loadNN(helper.mem_steering_nn);
		//System.out.println(Arrays.deepToString(tt.outputLayer.getWeightMatrix().getArray()));
		//System.out.println("test the network");

		/*helper.train_data = helper.getInputFile(helper.infileTest);
		MSE = helper.testNN(helper.MyNN1);
		System.out.println("MSE " + Double.toString(MSE) + " epochs " + helper.epochs);
		System.out.println(Arrays.deepToString(helper.MyNN1.outputLayer.getWeightMatrix().getArray())); */

		/*helper2.train_data = helper2.getInputFile(helper2.infileTest);
		MSEAcc = helper2.testNN(helper2.myNNAcc);
		System.out.println("MSEAcc " + Double.toString(MSEAcc) + " epochs " + helper2.epochsAcc);
		System.out.println(Arrays.deepToString(helper2.myNNAcc.outputLayer.getWeightMatrix().getArray()));*/
	}

}
