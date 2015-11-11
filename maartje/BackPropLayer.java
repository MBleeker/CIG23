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
	Matrix deltaOutput;
	Matrix deltaHidden;
	
	public BackPropLayer(NetworkLayer aLayer) {
		this.aLayer = aLayer;
	}
	
	public Matrix getDeltaMatrix() {
		return this.
	}

		// works differently, guess also better to make this array Jorg did
	public Matrix getDeltaMatrixPrevious() {
		return this.aLayer.getNextLayer().getDeltaMatrix();
	}
	
	/* Computes the error on the output layer */
	public void computeDelta(Matrix target) {
		
		if (this.aLayer.getLayerType() == 3) {
			this.deltaMatrix = this.aLayer.getOutputVector().minus(target);
		}
		
		else if (this.aLayer.getLayerType() == 2) {
			Matrix derivative = computeDerivativeActFunction();
			this.deltaMatrix = derivative.times(this.aLayer.getWeightMatrix().times(getDeltaMatrixPrevious())) ;
		}
		
		else {
			// Throw error message: input layer doesn't have delta and any other number don't mean anything
		}
	}
	
	public Matrix computeDerivativeActFunction() {
		if (this.aLayer.getTypeOfActFunction().equals("sig")) {
			Matrix activation = this.aLayer.getActivationVector();
			
			return xxxxx;
		}
		else if (this.aLayer.getTypeOfActFunction().equals("tanh")) {
			Matrix activation = this.aLayer.getActivationVector();
			
			return xxxxxxxxxxxx;
		}
		
		else {
			// throw error --> Still need to write this
			System.out.println("We don't know this function.");
		}
	}
}