import java.io.IOException;
import java.io.FileReader;
import com.opencsv.*;
import java.util.*;

public class Training_evoirment {
	
	//System.out.println("Hello, World");
	
	private static float[][] make_vector(String[] data_point) {
		float[][] vector =new float[1][data_point.length];
	    int i=0;
	    for(String str:data_point){
	    	vector[0][i]= Float.parseFloat(str);//Exception in this line
	        i++;
	    }
	    return vector; 
	}

	public static void main(String[] args) throws IOException {
		
		// Array's with parameters for the NN that could be different 
		System.out.println("Start training program");
		String training_data_path = "/Users/Maurits/Documents/GitHub/School Projects/CIG23/maurits/data/data output/Traing_and_Test_set2015-11-19 19:29:22/test_data.csv";
		CSVReader reader = new CSVReader(new FileReader(training_data_path),';');
		String [] nextLine;
	     while ((nextLine = reader.readNext()) != null) {
	        // nextLine[] is an array of values from the line
	    	
	    	String[] traing_data = Arrays.copyOfRange(nextLine, 0, nextLine.length-5);
	    	String[] target_data = Arrays.copyOfRange(nextLine, nextLine.length-5, nextLine.length);
	    	//float[][] traing_vector = make_vector(traing_data);
	    	//float[][] targer_vector = make_vector(target_data); 
	    	
	     }
	}
}