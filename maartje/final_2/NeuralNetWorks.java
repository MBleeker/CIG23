import java.io.*;

import java.util.Arrays;
import Jama.Matrix;

public class NeuralNetWorks {

	BufferedReader train_data;
	String infile = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/trainNN/train_nn_data_all.dat";
	String mem_steering_nn = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/out/steering_nn.mem";
	String mem_acc_nn = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/out/acc_nn.mem";
	String mem_break_nn = "C:/Users/Maartje/Documents/Studie/master/ci/project/files/out/break_nn.mem";

	NeuralNetwork MyNN1;
	NeuralNetwork myNNAcc;
	NeuralNetwork myNNBreak;
	int epochs = 0;
	int epochsAcc = 0;
	int epochsBreak = 0;

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

		double[] targetValueSteering = {Double.parseDouble(splits[23])};
		return new Matrix(new double [][] {targetValueSteering}).transpose();
	}

	public Matrix createTargetVectorAcc(String[] splits) {
		double[] targetValueAcc = {Double.parseDouble(splits[24])}; // value for acceleration in the data
		return new Matrix(new double [][] {targetValueAcc}).transpose();
	}

	public Matrix createTargetVectorBreak(String[] splits) {
		double[] targetValueAcc = {Double.parseDouble(splits[25])}; // value for break in the data
		return new Matrix(new double [][] {targetValueAcc}).transpose();
	}

	public Matrix createInputVector(String[] splits) {
		// 6 range values
		String[] inputRangeValues = Arrays.copyOfRange(splits, 5,18);

		double[] training_data = new double[15];
		int i = 0;
		// convert training data to array of doubles
		for (String elem : inputRangeValues) {
			training_data[i] = Double.parseDouble(elem);
			i++;
		}
		// add angle to trackaxis and distance to trackaxis
		training_data[13] = Double.parseDouble(splits[19]); // angle to trackaxis
		training_data[14] = Double.parseDouble(splits[20]); // track position

		return new Matrix(new double [][] {training_data}).transpose();
	}

	public Matrix createInputVectorAcc(String[] splits) {

		String[] inputRangeValuesAcc = Arrays.copyOfRange(splits, 1, 18);

		double[] trainingDataAcc = new double[22];
		int i = 0;

		// these are the 'normal' input values
		for (String elem : inputRangeValuesAcc) {
			trainingDataAcc[i] = Double.parseDouble(elem);
			i++;
		}
		// angle to track axis, distance to track axis, speed, gear
		trainingDataAcc[17] = Double.parseDouble(splits[19]); // angle
		trainingDataAcc[18] = Double.parseDouble(splits[20]); // position
		trainingDataAcc[19] = Double.parseDouble(splits[21]); // speed
		trainingDataAcc[20] = Double.parseDouble(splits[22]); // gear
		trainingDataAcc[21] = Double.parseDouble(splits[23]); // steering

		return new Matrix(new double [][] {trainingDataAcc}).transpose();
	}

	public Matrix createInputVectorBreak(String[] splits) {
		// Now this is still exactly the same as for the acceleration

		String[] inputRangeValuesAcc = Arrays.copyOfRange(splits, 1, 18);

		double[] trainingDataAcc = new double[22];
		int i = 0;

		// these are the 'normal' input values
		for (String elem : inputRangeValuesAcc) {
			trainingDataAcc[i] = Double.parseDouble(elem);
			i++;
		}

		// angle to track axis, distance to track axis, speed, gear
		trainingDataAcc[17] = Double.parseDouble(splits[19]); // angle
		trainingDataAcc[18] = Double.parseDouble(splits[20]); // position
		trainingDataAcc[19] = Double.parseDouble(splits[21]); // speed
		trainingDataAcc[20] = Double.parseDouble(splits[22]); // gear
		trainingDataAcc[21] = Double.parseDouble(splits[23]); // steering

		return new Matrix(new double [][] {trainingDataAcc}).transpose();
	}

	public double testNN(NeuralNetwork nn){

		double mse = 0;
		String sCurrentLine;
		this.epochs =0;
		Matrix targetVector = null;
		try {
			while ((sCurrentLine = this.train_data.readLine()) != null) {
				this.epochs++;
				String[] splits = sCurrentLine.split(";");
				Matrix inputVector = this.createInputVector(splits);
				targetVector = this.createTargetVector(splits);
				Matrix predictedValue = nn.processInput(inputVector);
				mse = mse + Math.pow((targetVector.getArray()[0][0] - predictedValue.getArray()[0][0]),2);

			}

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
				String[] splits = sCurrentLine.split(";");
				inputVector = this.createInputVector(splits);
				targetVector = this.createTargetVector(splits);
				double predictedValue = this.MyNN1.trainNN(inputVector, targetVector);
				error = Math.pow((targetVector.getArray()[0][0] - predictedValue),2);
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
				String[] splits = sCurrentLine.split(";");
				inputVectorAcc = this.createInputVectorAcc(splits);
				targetVectorAcc = this.createTargetVectorAcc(splits);
				double predictedValueAcc = this.myNNAcc.trainNN(inputVectorAcc, targetVectorAcc);
				errorAcc = Math.pow((targetVectorAcc.getArray()[0][0] - predictedValueAcc),2);
				mseAcc += errorAcc;

				this.epochsAcc++;
			}

			System.out.println("inputVectorAcc " + Arrays.deepToString(inputVectorAcc.getArray()));
			System.out.println("targetVectorAcc " + Arrays.deepToString(targetVectorAcc.getArray()));

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("MSE Acc: " + mseAcc/this.epochsAcc);
		return (mseAcc/this.epochsAcc);

	}

	public double trainNeuralNetworkBreak() {
		String sCurrentLine;
		double mseBreak = 0;
		double errorBreak = 0;
		Matrix targetVectorBreak = null;
		Matrix inputVectorBreak = null;
		this.epochsBreak = 0;

		int count = 0;
		try {
			while ((sCurrentLine = this.train_data.readLine()) != null) {
				count += 1;
				String[] splits = sCurrentLine.split(";");
				inputVectorBreak = this.createInputVectorBreak(splits);
				targetVectorBreak = this.createTargetVectorBreak(splits);
				double predictedValueBreak = this.myNNBreak.trainNN(inputVectorBreak, targetVectorBreak);
				errorBreak = Math.pow((targetVectorBreak.getArray()[0][0] - predictedValueBreak),2);
				mseBreak += errorBreak;

				this.epochsBreak++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("MSE Break: " + mseBreak/this.epochsBreak);
		return (mseBreak/this.epochsBreak);

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

	public void storeNNBreak(NeuralNetwork nnBreak, String inFile) {
		ObjectOutputStream out = null;

		try {
			out = new ObjectOutputStream(new FileOutputStream(inFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.writeObject(nnBreak);
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
		NeuralNetWorks helper3 = new NeuralNetWorks();
		helper.train_data = helper.getInputFile(helper.infile);
		helper.MyNN1 = new NeuralNetwork();
		helper2.train_data = helper2.getInputFile(helper2.infile);
		helper2.myNNAcc = new NeuralNetwork();
		helper3.train_data = helper3.getInputFile(helper3.infile);
		helper3.myNNBreak = new NeuralNetwork();

		try {
			helper.MyNN1.buildInputLayer(15, "tanh");
			helper.MyNN1.buildHiddenLayer(34, "tanh");
			helper.MyNN1.buildOutputLayer(1, "tanh");
			helper.MyNN1.setLearningRate(0.01);

			helper2.myNNAcc.buildInputLayer(22, "tanh");
			helper2.myNNAcc.buildHiddenLayer(34, "tanh"); // seems to be the best value (trial and error)
			helper2.myNNAcc.buildOutputLayer(1, "tanh");
			helper2.myNNAcc.setLearningRate(0.01); // seems to be the best value (trial and error)

			helper3.myNNBreak.buildInputLayer(22, "tanh");
			helper3.myNNBreak.buildHiddenLayer(34, "tanh"); // seems to be the best value (trial and error)
			helper3.myNNBreak.buildOutputLayer(1, "tanh");
			helper3.myNNBreak.setLearningRate(0.01);
		}
		catch (NeuralNetwork.WrongBuildSequence e){
			e.printStackTrace();
		}

		double MSE;
		helper.train_data = helper.getInputFile(helper.infile);
		MSE = helper.trainNeuralNetwork();
		System.out.println("MSE " + Double.toString(MSE) + " epochs " + helper.epochs);
		helper.storeNN(helper.MyNN1, helper.mem_steering_nn);

		double MSEAcc;

		helper2.train_data = helper2.getInputFile(helper2.infile);
		MSEAcc = helper2.trainNeuralNetworkAcc();
		System.out.println("MSEAcc " + Double.toString(MSEAcc) + " epochs " + helper2.epochsAcc);
		helper2.storeNNAcc(helper2.myNNAcc, helper2.mem_acc_nn);

		double MSEBreak;

		helper3.train_data = helper3.getInputFile(helper3.infile);
		MSEBreak = helper3.trainNeuralNetworkBreak();
		System.out.println("MSEBreak " + Double.toString(MSEBreak) + " epochs " + helper3.epochsBreak);
		helper3.storeNNAcc(helper3.myNNBreak, helper3.mem_break_nn);

	}

}
