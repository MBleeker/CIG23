class NetworkLayer {
	
	int numberOfNeurons;
	int layerType;
	
	/* 
	 * Constructor
	 */
	public NetworkLayer(){
		this.numberOfNeurons = 5;
	}
	
	/*
	 * Constructor
	 * @param numberOfNeurons 
	 * @param layerType 1: input, 2: hidden, 3: output
	 */
	public NetworkLayer(int numberOfNeurons, int layerType) {
		this.numberOfNeurons = numberOfNeurons;
		this.layerType = layerType;
	}
	

}