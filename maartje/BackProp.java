import java.util.HashMap;
import java.util.Map;

class BackProp {

	/* HashMap to map a certain forwarding layer to a BackProp object, which has all info about the backpropagation in it */
	public Map<NetworkLayer, BackPropLayer> trainingMap = new HashMap<NetworkLayer, BackPropLayer>();
	
}