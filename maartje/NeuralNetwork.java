import cicontest.torcs.client.SensorModel;

/*
 * Class to implement the feed forward neural network that is 
 * used by the bot to drive the track. The network consists
 * of an input layer, followed by (a number of) hidden layer(s)
 * and one output node. The network is trained using
 * backpropagation. 
 */
public class NeuralNetwork {
	
	public void newLayer(int numberOfNeurons) {
	
		networkLayer layer = new networkLayer(numberOfNeurons);
	}
	
}


/*public class NeuralNetwork {

	NeuralNetwork(){ }

	NeuralNetwork(int inputs, int hidden, int outputs){}

	public double getOutput(SensorModel a) {
		return 0.5;
	}
}*/
