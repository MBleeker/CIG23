import java.io.*;

import java.util.Arrays;
import Jama.Matrix;
import race.TorcsConfiguration;

public class NeuralNetWorks {

	BufferedReader train_data;

	String loc_train_files = "c:/temp/";
	// String infile = "train_nn_data_all.dat";
	// String infile = "torcsRace_dirt-13.dat";
	String infile = "Aalborg-full.dat";
	String mem_steering_nn = "steering_nn.mem";
	String mem_acc_nn = "acc_nn.mem";
	String mem_break_nn = "break_nn.mem";

	NeuralNetwork MyNN1;
	NeuralNetwork myNNAcc;
	NeuralNetwork myNNBreak;
	int epochs = 0;
	int epochsAcc = 0;
	int epochsBreak = 0;

	public NeuralNetWorks() {
		String output_dir = TorcsConfiguration.getInstance().getOptionalProperty("training_dir");
		if (output_dir != null) {
			this.loc_train_files = output_dir;
		}
		this.infile = this.loc_train_files + infile;
		this.mem_steering_nn = this.loc_train_files + mem_steering_nn;
		this.mem_acc_nn = this.loc_train_files + mem_acc_nn;
		this.mem_break_nn = this.loc_train_files + mem_break_nn;

	}

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

	public Matrix createTargetVectorBreak(String[] splits) {
		//System.out.println("SPLITS: " + splits[25]);
		double[] targetValueAcc = {Double.parseDouble(splits[25])}; // value for break in the data
		return new Matrix(new double [][] {targetValueAcc}).transpose();
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

		double error = 0;
		double mse = 0;
		String sCurrentLine;
		this.epochs =0;
		Matrix targetVector = null;
		NeuralNetworkUtils utils = new NeuralNetworkUtils();

		try {
			while ((sCurrentLine = this.train_data.readLine()) != null) {
				this.epochs++;
				// System.out.println("Line " + this.epochs);
				String[] splits = sCurrentLine.split(";");
				Matrix inputVector = utils.createInputVector(splits);
				targetVector = utils.createTargetVector(splits, new int[] {59});
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
		NeuralNetworkUtils utils = new NeuralNetworkUtils();
		Matrix targetVector = null;
		Matrix inputVector = null;

		try {
			while ((sCurrentLine = this.train_data.readLine()) != null) {
				// System.out.println("Line " + this.epochs);
				String[] splits = sCurrentLine.split(";");
				inputVector = utils.createInputVector(splits);
				targetVector = utils.createTargetVector(splits, new int[] {59});
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

	public double trainNeuralNetworkAcc() {
		String sCurrentLine;
		double mseAcc = 0;
		double errorAcc = 0;
		Matrix targetVectorAcc = null;
		Matrix inputVectorAcc = null;
		this.epochsAcc = 0;
		NeuralNetworkUtils nn_utils = new NeuralNetworkUtils();

		try {
			while ((sCurrentLine = this.train_data.readLine()) != null) {
				// System.out.println("Acc Line " + this.epochsAcc);
				String[] splits = sCurrentLine.split(";");
				inputVectorAcc = nn_utils.createInputVectorAcc_wOpponents(splits);
				//System.out.println("input vector Acc: " + Arrays.deepToString(inputVectorAcc.getArray()));
				targetVectorAcc = nn_utils.createTargetVectorAcc(splits, new int[] {60, 61});
				//System.out.println("target vector Acc: " + Arrays.deepToString(targetVectorAcc.getArray()));
				double predictedValueAcc = this.myNNAcc.trainNN(inputVectorAcc, targetVectorAcc);
				System.out.println("ACC pred <=> target: " + predictedValueAcc + " <=> " + targetVectorAcc.getArray()[0][0]);
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
				System.out.println("COUNT: " + count);
				//System.out.println("Acc Line " + this.epochsAcc);
				String[] splits = sCurrentLine.split(";");
				inputVectorBreak = this.createInputVectorBreak(splits);
				//System.out.println("input vector Acc: " + Arrays.deepToString(inputVectorAcc.getArray()));
				targetVectorBreak = this.createTargetVectorBreak(splits);
				//System.out.println("target vector Acc: " + Arrays.deepToString(targetVectorAcc.getArray()));
				double predictedValueBreak = this.myNNBreak.trainNN(inputVectorBreak, targetVectorBreak);
				//System.out.println("target prediction Acc: " + predictedValueAcc);
				errorBreak = Math.pow((targetVectorBreak.getArray()[0][0] - predictedValueBreak),2);
				//System.out.println("MSE Acc rep: " + mseAcc);
				mseBreak += errorBreak;

				this.epochsBreak++;
			}

			System.out.println("inputVectorBreak " + Arrays.deepToString(inputVectorBreak.getArray()));
			System.out.println("targetVectorBreak " + Arrays.deepToString(targetVectorBreak.getArray()));

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("MSE Break: " + mseBreak/this.epochsBreak);
		//System.out.println()
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

		TorcsConfiguration.getInstance().initialize(new File("F:\\java\\IdeaProjects\\TorcsController\\out\\production\\torcs.properties"));
		NeuralNetWorks helper = new NeuralNetWorks();
		NeuralNetWorks helper2 = new NeuralNetWorks();
		NeuralNetWorks helper3 = new NeuralNetWorks();
		helper.train_data = helper.getInputFile(helper.infile);
		helper.MyNN1 = new NeuralNetwork();
		helper2.train_data = helper2.getInputFile(helper2.infile);
		helper2.myNNAcc = new NeuralNetwork();
		//helper3.train_data = helper3.getInputFile(helper3.infile);
		//helper3.myNNBreak = new NeuralNetwork();

		try {
			helper.MyNN1.buildInputLayer(15, "tanh");
			helper.MyNN1.buildHiddenLayer(34, "tanh");
			helper.MyNN1.buildOutputLayer(1, "tanh");
			helper.MyNN1.setLearningRate(0.01);

			helper2.myNNAcc.buildInputLayer(22, "tanh");
			helper2.myNNAcc.buildHiddenLayer(50, "tanh"); // seems to be the best value (trial and error)
			helper2.myNNAcc.buildOutputLayer(1, "tanh");
			helper2.myNNAcc.setLearningRate(0.014); // seems to be the best value (trial and error)

			// helper3.myNNBreak.buildInputLayer(22, "sig");
			// helper3.myNNBreak.buildHiddenLayer(34, "sig"); // seems to be the best value (trial and error)
			// helper3.myNNBreak.buildOutputLayer(1, "sig");
			// helper3.myNNBreak.setLearningRate(0.01);
		}
		catch (NeuralNetwork.WrongBuildSequence e){
			e.printStackTrace();
		}

		double MSE;
		// System.out.println(Arrays.deepToString(helper.MyNN1.outputLayer.getWeightMatrix().getArray()));

		helper.train_data = helper.getInputFile(helper.infile);
		MSE = helper.trainNeuralNetwork();
		System.out.println("MSE " + Double.toString(MSE) + " epochs " + helper.epochs);
		helper.storeNN(helper.MyNN1, helper.mem_steering_nn);


		double MSEAcc;

		helper2.train_data = helper2.getInputFile(helper2.infile);
		MSEAcc = helper2.trainNeuralNetworkAcc();
		System.out.println("MSEAcc " + Double.toString(MSEAcc) + " epochs " + helper2.epochsAcc);
		helper2.storeNNAcc(helper2.myNNAcc, helper2.mem_acc_nn);

		// double MSEBreak = 0;

		// helper3.train_data = helper3.getInputFile(helper3.infile);
		// MSEBreak = helper3.trainNeuralNetworkBreak();
		// System.out.println("MSEBreak " + Double.toString(MSEBreak) + " epochs " + helper3.epochsBreak);
		// helper3.storeNNAcc(helper3.myNNBreak, helper3.mem_break_nn);


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
