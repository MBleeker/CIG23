import java.util.HashMap;
import java.util.Map;
import Jama.Matrix;

class BackProp {

	/* HashMap to map a certain forwarding layer to a BackProp object, which has all info about the backpropagation in it */
	public Map<NetworkLayer, BackPropLayer> trainingMap = new HashMap<NetworkLayer, BackPropLayer>();
	
	NeuralNetwork network;
	Matrix input;
	Matrix target;
	int learningRate;
	
	/*
	 * Constructor. NOTE: Also implement momentum? What is this exactly?
	 */
	public BackProp(NeuralNetwork network, Matrix input, Matrix target, int learningRate) {
		
		this.network = network;
		this.input = input;
		this.target = target;
		this.learningRate = learningRate;
		
		/* Loop over all layers in the network and create an hash map */
		for (NetworkLayer aLayer : this.network.getAllLayers()) {
			BackPropLayer backPropLayer = new BackPropLayer(aLayer);
			this.trainingMap.put(aLayer, backPropLayer);
		}
		
	}
	
	/* Method to loop over all layers and compute the deltas (based on methods from the other class) */
	public void computeAllDeltas() {
		for (int i = this.network.getAllLayers().size(); i > 0; i--) {
			NetworkLayer aLayer = this.network.getAllLayers().get(i);
			getBackPropLayer(aLayer).computeDelta(this.target); // from now on every layer should have a delta (after the loop ofc)
		}
	}
	
	/* Returns the backproplayer that belongs to the network layer you feed */
	public BackPropLayer getBackPropLayer(NetworkLayer aLayer) {
		BackPropLayer coBackPropLayer = this.trainingMap.get(aLayer);
		return coBackPropLayer;
	}
}