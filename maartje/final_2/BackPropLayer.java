
import Jama.Matrix;

class BackPropLayer {

	NetworkLayer aLayer;
	Matrix deltaMatrix;
	Matrix gradient;
	Matrix previousDeltas;
	Matrix derivation;

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
			this.gradient = this.deltaMatrix.times(this.aLayer.getInputVector().transpose());
		}

		else if (this.aLayer.getLayerType() == 2) {
			/* Computes the error on the HIDDEN layer */
			this.derivation  = computeDerivativeActFunction();
			this.previousDeltas = bp.getBackPropLayer(this.aLayer.getNextLayer()).getDeltaMatrix();
			Matrix temp = this.previousDeltas.transpose().times(this.aLayer.getNextLayer().getWeightMatrix());
			this.deltaMatrix = this.derivation.arrayTimes(temp.transpose());

			this.gradient = this.deltaMatrix.times(this.aLayer.getInputVector().transpose());
			temp = null;
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
	        			activation[i][j] = derivativeSign(activation[i][j]);
	        			break;
	        		case "tanh":
						activation[i][j] = derivativeTanh(activation[i][j]);
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


	public double derivativeSign(double num) {
		// write the derivative function of the sigmoid here and return value (similar to normal function, only difference is derivative)
		return NetworkLayer.SigmoidFunction(num)  * (1.0-NetworkLayer.SigmoidFunction(num));
	}

	public double derivativeTanh(double num) {
		//
		return ( 1.0-Math.pow(NetworkLayer.Tanh(num), 2.0) );


	}
}


