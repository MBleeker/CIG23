import java.io.Serializable;

import Jama.Matrix;

/* Jorg: we need to make sure that we can store the whole NN as a binary object
		 which means together with the learned parameter matrix as a whole
		 classes IGenome and DefaultDriverGenome are also serializable
		 and our NN will be a property of the class DefaultDriverGenome	 
 */
class NetworkLayer implements Serializable {
	
	private static final long serialVersionUID = 100000000000001L;
	
	private int numberOfNeurons;
	private int layerType;
	private Matrix weightMatrix;
	private Matrix inputVector;
	private Matrix activationVector;
	private Matrix outputVector;
	private NetworkLayer previousLayer;
	private NetworkLayer nextLayer;
	private String typeOfActFunction;
	
	
	/*
	 * Constructor
	 * 
	 * @param numberOfNeurons 
	 * @param layerType 1: input, 2: hidden, 3: output
	 */
	public NetworkLayer(int numberOfNeurons, int layerType, String typeActFunc) {
		this.numberOfNeurons = numberOfNeurons;
		this.layerType = layerType;
		this.previousLayer = null;
		this.nextLayer = null;
		this.inputVector = new Matrix(new double[numberOfNeurons], 1); 
		this.activationVector = new Matrix(new double[numberOfNeurons], 1); 
		this.outputVector = new Matrix(new double[numberOfNeurons], 1);
		this.typeOfActFunction = typeActFunc;
		
	}
	
	public Matrix getInputVector() {
		return inputVector;
	}

	public void setInputVector(Matrix inputVector) {
		this.inputVector = inputVector;
	}

	public String getTypeOfActFunction() {
		return typeOfActFunction;
	}

	public int getNumberOfNeurons() {
		return numberOfNeurons;
	}

	public int getLayerType() {
		return layerType;
	}

	public NetworkLayer getPreviousLayer() {
		return previousLayer;
	}

	public NetworkLayer getNextLayer() {
		return nextLayer;
	}
	
	public Matrix getWeightMatrix() {
		return weightMatrix;
	}

	public void setWeightMatrix(Matrix s) {
		this.weightMatrix = s;
	}

	public Matrix getActivationVector() {
		return activationVector;
	}

	public Matrix getOutputVector() {
		return outputVector;
	}

	public void setOutputVector(Matrix outputVector) {
		this.outputVector = outputVector;
	}

		
	/*
	 * Method to set the previous layer of the current layer.
	 * You want to know the previous layer and the next layer, in order to be
	 * able to know which layers are connected.
	 * 
	 * @param previousLayer The layer of nodes before the current layer
	 */
	public void setPreviousLayer(NetworkLayer previousLayer) {
		this.previousLayer = previousLayer;
	}
	
	/*
	 * Method to set the next layers of the current layer.
	 * 
	 * @param nextLayer The layer of nodes after the current layer
	 */
	public void setNextLayer (NetworkLayer nextLayer) {
		this.nextLayer = nextLayer;
	}
	
	public void initializeWeightMatrix() {
	
		// if there is no previous layer we can't initialize
		// e.g. the InputLayer has no WeightMatrix
		if (this.previousLayer != null) {
			// we are going to construct an N x M matrix for this layer
			// M = number of units previous layer
			// M = number of units this layer
			// so we go from M-dim to N-dim 
			int M = this.getPreviousLayer().getNumberOfNeurons();
			int N = this.getNumberOfNeurons();
			this.weightMatrix = Matrix.random(N, M);
			
		}
	}
	
	public void calculateActivation(Matrix input) {
		
		// System.out.println("Weight dim " + NetworkLayer.getDimMatrix(this.getWeightMatrix()) );
		// System.out.println("Input dim " + NetworkLayer.getDimMatrix(input) );
		this.activationVector = this.getWeightMatrix().times(input);
		// System.out.println("Output dim " + NetworkLayer.getDimMatrix(this.activationVector) );
	}
	
	public void calculateOutput() {
		
		double[][] hx = this.activationVector.getArray();
	    int n = this.activationVector.getRowDimension();
	    int m = this.activationVector.getColumnDimension();

	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j<m; j++) {
	        	 switch (this.typeOfActFunction) {
	        	 	case "tanh":
	        	 		hx[i][j] = NetworkLayer.Tanh(hx[i][j]);
	        	 		break;
	        	 	case "sig":
	        	 		hx[i][j] = NetworkLayer.SigmoidFunction(hx[i][j]);
	        	 	default: 
	        	 		hx[i][j] = NetworkLayer.Tanh(hx[i][j]);
	        	 }
	             
	        }
	    }
	    this.outputVector = new Matrix(hx);
	}
	
	public static double SigmoidFunction(double num){
		
		return 1.0 / (1.0 + BoundNumbers.exp(-1.0 * num));
	}
	
	public static double Tanh(double num) {
		final double result = (BoundNumbers.exp(num*2.0)-1.0)/(BoundNumbers.exp(num*2.0)+1.0);
		return result;
	}

	public static String getDimMatrix(Matrix a) {
		
		int M = a.getColumnDimension();
		int N = a.getRowDimension();
		return "(" + N + ", " + M + ")";
	}
}