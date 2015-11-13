/*
 * Maartje:
 * What do we want to know in order to be able to implement backprop?
 * - Delta's: ---> MAKE METHOD 
 * - - Output error: output - target
 * - - Hidden units: eq. 5.56 in Bishop --> so we need another method that gives the derivative of the used activation function
 * - We use the delta's to update the weight matrices
 */

import Jama.Matrix;

class BackPropLayer {

	NetworkLayer aLayer;
	Matrix deltaMatrix;
	Matrix gradient;
	Matrix previousDeltas;
	Matrix derivation;
	// Matrix deltaOutput;
	// Matrix deltaHidden;
	
	public BackPropLayer(NetworkLayer aLayer) {
		this.aLayer = aLayer;
	}
	
	public Matrix getDeltaMatrix() {
		return this.deltaMatrix;
	}

	
	public void computeDelta(BackProp bp, Matrix target) {
		
		if (this.aLayer.getLayerType() == 3) {
			/* Computes the error on the OUTPUT layer */
			this.deltaMatrix = this.aLayer.getOutputVector().minus(target);
			// System.out.println("OutputLayer - DeltaMatrix " + NetworkLayer.getDimMatrix(this.deltaMatrix) + " ActivationVec " + NetworkLayer.getDimMatrix(this.aLayer.getInputVector()));
			this.gradient = this.deltaMatrix.times(this.aLayer.getInputVector().transpose());
			// System.out.println("OutputLayer - gradient " + NetworkLayer.getDimMatrix(this.gradient));
			// System.out.println("OutputLayer - weights " + NetworkLayer.getDimMatrix(this.aLayer.getWeightMatrix()));
			// the gradient matrix must have the same dimensions as the weight matrix of the layer
			// System.out.println(Arrays.deepToString(aLayer.getWeightMatrix().getArray()));
		}
		
		else if (this.aLayer.getLayerType() == 2) {
			/* Computes the error on the HIDDEN layer */
			this.derivation  = computeDerivativeActFunction();
			this.previousDeltas = bp.getBackPropLayer(this.aLayer.getNextLayer()).getDeltaMatrix();
			// Jorg: we could also just get the deltaMatrix from the previous layer-> backpropLayer
			//so this really only works if you assume that you go through 
			// System.out.println("OtherLayer - derivative " + NetworkLayer.getDimMatrix(derivative));
			// System.out.println("OtherLayer - previousDeltas " + NetworkLayer.getDimMatrix(this.previousDeltas));
			// System.out.println("OtherLayer - getNextLayer().getWeightMatrix " + NetworkLayer.getDimMatrix(this.aLayer.getNextLayer().getWeightMatrix()));
			// more for readability: intermediate result, delta_prevLayer * weightM_prevLayer
			Matrix temp = this.previousDeltas.transpose().times(this.aLayer.getNextLayer().getWeightMatrix());
			// System.out.println("OtherLayer - temp " + NetworkLayer.getDimMatrix(temp));
			this.deltaMatrix = this.derivation.times(temp);
			// I am not sure about this last step: I am not sure wheter we should use the inputVector of the current layer
			this.gradient = this.deltaMatrix.times(this.aLayer.getActivationVector());
			temp = null;
			// System.out.println("OtherLayer - gradient " + NetworkLayer.getDimMatrix(this.gradient));
			// System.out.println("OtherLayer - weights " + NetworkLayer.getDimMatrix(this.aLayer.getWeightMatrix()));
		}
		
		else {
			// Throw error message: input layer doesn't have delta and any other numbers don't mean anything
			System.out.println("This is not a valid layer type to compute an error on.");
		}
	}
	
	/* CHEAT SHEET:
	
	protected double SigmoidFunction(double num){
		
		return 1.0 / (1.0 + Math.exp(-1.0 * num));
	}
	
	public void calculateOutput() {
		
		double[][] hx = this.activationVector.getArray();
	    int n = this.activationVector.getRowDimension();
	    int m = this.activationVector.getColumnDimension();
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j<m; j++) {
	        	 switch (this.typeOfActFunction) {
	        	 	case "tanh":
	        	 		hx[i][j] = this.Tanh(hx[i][j]);
	        	 		break;
	        	 	case "sig":
	        	 		hx[i][j] = this.SigmoidFunction(hx[i][j]);
	        	 	default: 
	        	 		hx[i][j] = this.Tanh(hx[i][j]);
	        	 }
	             
	        }
	    }
	    this.outputVector = new Matrix(hx);
	    System.out.println("outputVector");
	    System.out.println(Arrays.deepToString(outputVector.getArray()));
	} */
	
	public Matrix computeDerivativeActFunction() {
		
		Matrix activationMatrix = this.aLayer.getActivationVector();
		double[][] activation = activationMatrix.getArray();
		int n = activationMatrix.getRowDimension();
		int m = activationMatrix.getColumnDimension();
		
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j<m; j++) {
	        	switch (this.aLayer.getTypeOfActFunction()) {
	        		case "sig":
	        			activation[i][j] = derivativeSigm(activation[i][j]);
	        			break;
	        		case "tanh":
	        			break;
	        			//still need to write this
	        		default:
	        			// throw error --> Still need to write this
	        			System.out.println("We don't know this function.");
	        	}
	        }
	    }
	    
	    return new Matrix(activation);
	}
	
	public double derivativeSigm(double num) {
		// write the derivative function of the sigmoid here and return value (similar to normal function, only difference is derivative)
			return NetworkLayer.SigmoidFunction(num)  * (1.0-NetworkLayer.SigmoidFunction(num));
	}
}


