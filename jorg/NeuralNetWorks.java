import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Jama.Matrix;

public class NeuralNetWorks {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		NeuralNetwork MyNN = new NeuralNetwork();
		
		try {
			MyNN.buildInputLayer(6);
			MyNN.buildHiddenLayer(4);
			MyNN.buildOutputLayer(1);
		}
		catch (NeuralNetwork.WrongBuildSequence e){
			e.printStackTrace();
		}
		
		List<NetworkLayer> allLayers = MyNN.getAllLayers();
		for (NetworkLayer alayer : allLayers) {
			System.out.println("Print layer of type " + alayer.getLayerType());
			if (alayer.getWeightMatrix() != null) {
				System.out.println(Arrays.deepToString(alayer.getWeightMatrix().getArray()));
				System.out.println(Arrays.deepToString(alayer.getOutputVector().getArray()));

			}
		}
		
		Matrix o = new Matrix(new double[][] {{2.0}, {2.0}, {2.0}});
		System.out.println(o.getRowDimension() + ", " + o.getColumnDimension());
		Matrix input = new Matrix(new double[][] {{2.0}, {2.0}, {2.0}, {2.0}, {3.0}, {4.0}}); 
		MyNN.processInput(input);
	}

}
