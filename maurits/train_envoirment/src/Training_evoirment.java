import java.io.IOException;
import java.io.FileReader;
import com.opencsv.*;

import Jama.Matrix;

import java.util.*;
import java.math.BigDecimal;
public class Training_evoirment {
	
	//System.out.println("Hello, World");
	
	private static Matrix makeVector(String[] data_point) {
		double[][] vector_array = new double[data_point.length][1];
	    int i=0;
	    for(String str:data_point){
	    	BigDecimal bd = new BigDecimal(str);
		    bd.longValue();
		    vector_array[i][0]= Double.valueOf(str).longValue();//Exception in this line
		    i++;
	    }
	    Matrix vector = new Matrix(vector_array);
	    return vector; 
	}
	
	private static NeuralNetwork makeOneLayerNeuralNetwork(String activationFunction, int inputNodes,int hiddenNotes, int outputNodes, double learningRate){
		NeuralNetwork MyNN = new NeuralNetwork();
		
		try {
			MyNN.buildInputLayer(inputNodes, activationFunction);
			MyNN.buildHiddenLayer(hiddenNotes, activationFunction);
			MyNN.buildOutputLayer(outputNodes, activationFunction);
			MyNN.setLearningRate(learningRate);
		
		}
		catch (NeuralNetwork.WrongBuildSequence e){
			e.printStackTrace();
		}
		return MyNN;
	}
	
	public static void main(String[] args) throws IOException {
		
		// Array's with parameters for the NN that could be different
		int[] inputNodes = {19};
		int[] hiddenNodes = {20,30,20};;
		int[] hiddenLayers = {1,2,3};
		double[] learningRates = {0.01,0.1,0.2};
		String[] activationFunctions = {"sig"};
		// load different datafiles for different input dims 
		
		System.out.println("Start training program");
		String training_data_path = "/Users/Maurits/Documents/UvA AI/Computational Intelligene /torcs data project/data/data output/Traing_and_Test_set2015-11-19 19:29:22/training_data.csv";
		for(int inputNode = 0; inputNode < inputNodes.length; inputNode++){
			//for(int hiddenLayer = 0; hiddenLayer < hiddenLayers.length; hiddenLayer++){
				for(int hiddenNode = 0; hiddenNode < hiddenNodes.length; hiddenNode++){
					for(int learningRate = 0; learningRate < learningRates.length;learningRate++){
						for(int activationFunction = 0; activationFunction < activationFunctions.length; activationFunction++){
							
							NeuralNetwork NN = makeOneLayerNeuralNetwork(activationFunctions[activationFunction],inputNodes[inputNode], hiddenNodes[hiddenNode],5, learningRates[learningRate]);
						    int counter = 0;
						    int wrongPoints = 0;
						    
						    CSVReader reader = new CSVReader(new FileReader(training_data_path),';');
							String [] nextLine;
							System.out.println("Start new tranaing cycle");
							while ((nextLine = reader.readNext()) != null) { 
						        // nextLine[] is an array of values from the line
						    	if(nextLine.length == 24){  // there is somelines not correctly preprocesed 
						    		try {
						    			String[] traing_data = Arrays.copyOfRange(nextLine, 0, nextLine.length-5);
								    	String[] target_data = Arrays.copyOfRange(nextLine, nextLine.length-5, nextLine.length);
								    	
								    	Matrix traingVector = makeVector(traing_data);
								    	Matrix targerVector = makeVector(target_data);
								    	NN.trainNN(traingVector, targerVector);
						    		} catch (Exception e ) {
						    			
						    			System.out.println("Wrong datapoint");
						    		}
						    	}
						    	counter++;
							}
							NN.storeGenome();
						}
					}
				//}
			}
		}
	}
}