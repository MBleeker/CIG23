import cicontest.torcs.client.SensorModel;

/*
 * Class to implement the feed forward neural network that is 
 * used by the bot to drive the track. The network consists
 * of an input layer, followed by (a number of) hidden layer(s)
 * and one output node. The network is trained using
 * backpropagation. 
 */
public class NeuralNetwork {
	
	/*
	 * Constructor
	 */
	public NeuralNetwork(){
		
	}
	
	NetworkLayer inputLayer;
	NetworkLayer hiddenLayer;
	NetworkLayer outputLayer;
	
	/*
	 * Method to construct an input layer
	 * @param numberOfNeurons the number of Neurons the layer contains
	 */
	public void buildInputLayer(int numberOfNeurons) {		
		NetworkLayer newInputLayer = new NetworkLayer(numberOfNeurons, 1);
		
		/* Assume that there can only be one input layer */
		if (this.hiddenLayer != null) {
			newInputLayer.setNextLayer(this.hiddenLayer);
		}
		
		else if (this.outputLayer != null) {
			newInputLayer.setNextLayer(this.outputLayer);
		}
		
		this.inputLayer = newInputLayer;		
		
	}
	
	/*
	 * Method to construct a hidden layer
	 * @param numberOfNeurons the number of Neurons the layer contains
	 */
	public void buildHiddenLayer(int numberOfNeurons) {
		NetworkLayer newHiddenLayer = new NetworkLayer(numberOfNeurons, 2);
		
		/* Assume that every new hidden layer is positioned after an already
		 * existing one (except for the first one) */
		if (this.hiddenLayer != null) {
			newHiddenLayer.setPreviousLayer(this.hiddenLayer);
		}
		
		if (this.outputLayer != null) {
			newHiddenLayer.setNextLayer(this.outputLayer);
		}
		
		this.hiddenLayer = newHiddenLayer;
	}
	
	/*
	 * Method to construct an output layer
	 * @param numberOfNeurons the number of Neurons the layer contains
	 */
	public void buildOutputLayer(int numberOfNeurons) {
		NetworkLayer newOutputLayer = new NetworkLayer(numberOfNeurons, 3);
		
		if (this.hiddenLayer != null) {
			newOutputLayer.setPreviousLayer(this.hiddenLayer);
		}
		
		else if (this.inputLayer != null) {
			newOutputLayer.setPreviousLayer(this.inputLayer);
		}
		
		this.outputLayer = newOutputLayer; 
	}
	
}


/*public class NeuralNetwork {

	NeuralNetwork(){ }

	NeuralNetwork(int inputs, int hidden, int outputs){}

	public double getOutput(SensorModel a) {
		return 0.5;
	}
}*/
