import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import Jama.Matrix;
import cicontest.torcs.client.SensorModel;

/*
 * Class to implement the feed forward neural network that is 
 * used by the bot to drive the track. The network consists
 * of an input layer, followed by (a number of) hidden layer(s)
 * and one output node. The network is trained using
 * backpropagation. 
 */
public class NeuralNetwork implements Serializable {
	
	private static final long serialVersionUID = 100000000000001L;
	
	public static class WrongBuildSequence extends Exception implements Serializable {
		
		private static final long serialVersionUID = 200000000000001L;
	}
	
	/*
	 * Constructor
	 */
	public NeuralNetwork(){
		
	}
	
	NetworkLayer inputLayer = null;
	NetworkLayer outputLayer = null;
	private double learningRate = 0.01; // default value
	protected List<NetworkLayer> allLayers = new ArrayList<NetworkLayer>();

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	/*
	 * Method to construct an input layer
	 * @param numberOfNeurons the number of Neurons the layer contains
	 */
	public void buildInputLayer(int numberOfNeurons, String actFunction) {		
		NetworkLayer newInputLayer = new NetworkLayer(numberOfNeurons, 1, actFunction);
		
		this.inputLayer = newInputLayer;	
		this.allLayers.add(this.inputLayer);
		
	}
	
	/*
	 * Method to construct a hidden layer
	 * @param numberOfNeurons the number of Neurons the layer contains
	 */
	public void buildHiddenLayer(int numberOfNeurons, String actFunction) throws WrongBuildSequence {
		NetworkLayer newHiddenLayer = new NetworkLayer(numberOfNeurons, 2, actFunction);
		
		/* Assume that every new hidden layer is positioned after an already
		 * existing one (except for the first one) */
		
		if (this.allLayers.isEmpty()) {
			// throw exception because you first have to construct the 
			// input layer
			throw new WrongBuildSequence();
		}
		else {
			newHiddenLayer.setPreviousLayer(this.allLayers.get(this.allLayers.size()-1) );
		}
		
		// first initialize the weights
		// then set the forward pointer in the previous layer
		newHiddenLayer.initializeWeightMatrix();
		this.setNextLayerInPreviousLayer(newHiddenLayer);
		this.allLayers.add(newHiddenLayer);
	}
	

	/*
	 * Method to construct an output layer
	 * @param numberOfNeurons the number of Neurons the layer contains
	 */
	public void buildOutputLayer(int numberOfNeurons, String actFunction) throws WrongBuildSequence {
		NetworkLayer newOutputLayer = new NetworkLayer(numberOfNeurons, 3, actFunction);
		
		if (this.allLayers.isEmpty()) {
			// throw exception because you first have to construct the 
			// input layer
			throw new WrongBuildSequence();
		}
		else {
			newOutputLayer.setPreviousLayer(this.allLayers.get(this.allLayers.size()-1) );
		}
		
		// first initialize the weights
		// then set the forward pointer in the previous layer
		newOutputLayer.initializeWeightMatrix();
		this.outputLayer = newOutputLayer; 
		this.setNextLayerInPreviousLayer(newOutputLayer);
		this.allLayers.add(this.outputLayer);
	}
	
	private void setNextLayerInPreviousLayer(NetworkLayer nl){
		
		if (!this.allLayers.isEmpty()) {
			this.allLayers.get(this.allLayers.size()-1).setNextLayer(nl);
		}
		else
		{ // must be first layer that we add therefore we can't assign
		  // the "nextLayer" to the previous layer
		}
	}
	
	public List<NetworkLayer> getAllLayers() {
		return allLayers;
	}

	public Matrix processInput(Matrix inputVec) {
		
		// set output vector for input layer because we're not going
		// to calculate the output for that layer
		this.inputLayer.setInputVector(inputVec);
		this.inputLayer.setOutputVector(inputVec);
		
		for (NetworkLayer aLayer : this.allLayers) {
			if (aLayer.getWeightMatrix() != null) {
				// get output of previous layer
				Matrix outputPrevLayer = this.allLayers.get(this.allLayers.indexOf(aLayer)-1).getOutputVector();
				//System.out.println("output dim " + NetworkLayer.getDimMatrix(outputPrevLayer) );
				aLayer.setInputVector(outputPrevLayer);
				aLayer.calculateActivation(outputPrevLayer);
				aLayer.calculateOutput();
				// System.out.println("Layer # of units " + aLayer.getNumberOfNeurons());
				// System.out.println(Arrays.deepToString(aLayer.getActivationVector().getArray()));
			}
		}
		return this.outputLayer.getOutputVector();
	}

	public double trainNN(Matrix inputVector, Matrix target){
		// this is currently not a generic method, because we assume the output is just a scalar
		// could be also more than one value
		//System.out.println("I should predict: " + Arrays.deepToString(target.getArray()));
		double[][] predictedTarget = this.processInput(inputVector).getArray();
		BackProp BP = new BackProp(this, target, this.learningRate);
		BP.computeBackProp();
		// remember, currently assuming NN returns one value only!
		//System.out.println("I predict: " + predictedTarget[0][0]);
		return predictedTarget[0][0];
	}

}
