class NetworkLayer {
	
	int numberOfNeurons;
	int layerType;
	NetworkLayer previousLayer;
	NetworkLayer nextLayer;
	
	/* 
	 * Constructor
	 */
	public NetworkLayer(){
		this.numberOfNeurons = 5;
	}
	
	/*
	 * Constructor
	 * 
	 * @param numberOfNeurons 
	 * @param layerType 1: input, 2: hidden, 3: output
	 */
	public NetworkLayer(int numberOfNeurons, int layerType) {
		this.numberOfNeurons = numberOfNeurons;
		this.layerType = layerType;
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
}