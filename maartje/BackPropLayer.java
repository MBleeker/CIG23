/*
 * Maartje:
 * What do we want to know in order to be able to implement backprop?
 * - Delta's: ---> MAKE METHOD 
 * - - Output error: output - target
 * - - Hidden units: eq. 5.56 in Bishop --> so we need another method that gives the derivative of the used activation function
 * - We use the delta's to update the weight matrices
 */

import java.util.Arrays;

import Jama.Matrix;

class BackPropLayer {

	NetworkLayer aLayer;
	Matrix deltaMatrix;
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

	public Matrix getDeltaMatrixPrevious() {
		return this.previousDeltas;
	}
	
	/* Computes the error on the output layer */
	public void computeDelta(Matrix target) {
		
		if (this.aLayer.getLayerType() == 3) {
			this.deltaMatrix = this.aLayer.getOutputVector().minus(target);
			
		}
		
		else if (this.aLayer.getLayerType() == 2) {
			Matrix derivative = computeDerivativeActFunction();
			this.previousDeltas = this.deltaMatrix; //so this really only works if you assume that you go through the network layer by layer, from output to input
			
			this.deltaMatrix = derivative.times(this.aLayer.getWeightMatrix().times(this.previousDeltas));
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
		
		/* double[][] hx = this.activationVector.getArray();
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
	    this.outputVector = new Matrix(hx); */
		
		Matrix activationMatrix = this.aLayer.getActivationVector();
		double[][] activation = activationMatrix.getArray();
		int n = activationMatrix.getRowDimension();
		int m = activationMatrix.getColumnDimension();
		
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j<m; j++) {
	        	switch (this.aLayer.getTypeOfActFunction()) {
	        		case "sig":
	        			activation[i][j] = derivativeSigm(activation[i][j]);
	        		case "tanh":
	        			//still need to write this
	        		default:
	        			// throw error --> Still need to write this
	        			System.out.println("We don't know this function.");
	        	}
	        }
	    }
	    
	    this.derivation = new Matrix(activation);
	    return this.derivation;
	}
	
	public void derivativeSigm(double activation) {
		// write the derivative function of the sigmoid here and return value (similar to normal function, only difference is derivative)
	}

	
}