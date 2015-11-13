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
			this.gradient = this.deltaMatrix.times(this.aLayer.getInputVector().transpose()); // MAARTJE: why do you use the input vector???
			// System.out.println("OutputLayer - gradient " + NetworkLayer.getDimMatrix(this.gradient));
			// System.out.println("OutputLayer - weights " + NetworkLayer.getDimMatrix(this.aLayer.getWeightMatrix()));
			// the gradient matrix must have the same dimensions as the weight matrix of the layer
			// System.out.println(Arrays.deepToString(aLayer.getWeightMatrix().getArray()));

		}
		
		else if (this.aLayer.getLayerType() == 2) {
			/* Computes the error on the HIDDEN layer */
			this.derivation  = computeDerivativeActFunction();
			// Jorg: I don't really like what I am doing here. Using the Backprop object to to
			// get the next backpropLayer object of the next layer.
			// Therefore I am doubting whether it would be better to just add a BackpropLayer property (object)
			// to the NetworkLayer object?
			this.previousDeltas = bp.getBackPropLayer(this.aLayer.getNextLayer()).getDeltaMatrix();
			// System.out.println("OtherLayer - previousDeltas " + NetworkLayer.getDimMatrix(this.previousDeltas));
			// System.out.println("OtherLayer - getNextLayer().getWeightMatrix " + NetworkLayer.getDimMatrix(this.aLayer.getNextLayer().getWeightMatrix()));
			// more for readability: intermediate result, delta_prevLayer * weightM_prevLayer
			Matrix temp = this.previousDeltas.transpose().times(this.aLayer.getNextLayer().getWeightMatrix());
			// System.out.println("OtherLayer - derivation " + NetworkLayer.getDimMatrix(this.derivation));
			// System.out.println("OtherLayer - temp " + NetworkLayer.getDimMatrix(temp));
			this.deltaMatrix = this.derivation.arrayTimes(temp.transpose());
			// System.out.println("OtherLayer - deltaMatrix " + NetworkLayer.getDimMatrix(this.deltaMatrix));
			// System.out.println("OtherLayer - aLayer.getInputVector " + NetworkLayer.getDimMatrix(this.aLayer.getInputVector().transpose()));
			// I am not sure about this last step: I am not sure wheter we should use the inputVector of the current layer --> MAARTJE: Slides say: output current nodes --> Maar Andrew doet het weer anders
			this.gradient = this.deltaMatrix.times(this.aLayer.getInputVector().transpose());
			temp = null;
			// System.out.println("OtherLayer - gradient " + NetworkLayer.getDimMatrix(this.gradient));
			// System.out.println("OtherLayer - weights " + NetworkLayer.getDimMatrix(this.aLayer.getWeightMatrix()));
		}
		
		else {
			// Throw error message: input layer doesn't have delta and any other numbers don't mean anything
			System.out.println("This is not a valid layer type to compute an error on.");
		}
	}

	// this is the actual "learning task", update the weightMatrices for each layer
	// that has a weight matrix...in our model only the inputlayer does not have one
	public void updateWeightMatrix(double learningRate) {

		// just to be sure we have a weight matrix
		if (this.aLayer.getWeightMatrix() != null) {
			// System.out.println("Update weights - derivative " + NetworkLayer.getDimMatrix(this.gradient));
			// System.out.println("Update weights - aLayer.getWeightMatrix " + NetworkLayer.getDimMatrix(this.aLayer.getWeightMatrix()));
			// not sure decent (minus) or ascent (plus)? currently assuming ascent
			// objective function? target - estimate?
			this.aLayer.setWeightMatrix( this.aLayer.getWeightMatrix().minus(this.gradient.times(learningRate)));
		}
	}
	
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
		return NetworkLayer.SigmoidFunction(num)  * (1.0-NetworkLayer.SigmoidFunction(num));  // MAARTJE: if we store the value of the sigmoid function in the forward prop we don't have to calculate it again here
	}
}


