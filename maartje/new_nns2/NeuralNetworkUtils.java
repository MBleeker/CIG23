import Jama.Matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by psftadm on 2-12-2015.
 */
public class NeuralNetworkUtils {

    NeuralNetwork MyNN;
    BufferedReader train_data;
    int epochs = 0;

    public NeuralNetworkUtils(NeuralNetwork myNN) {
        MyNN = myNN;
    }

    public NeuralNetworkUtils() {

    }

    public BufferedReader getInputFile(String inFile) {

        BufferedReader freader = null;
        try {
            freader = new BufferedReader((new FileReader(inFile)));


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return freader;
    }

    public static String getDimMatrix(Matrix a) {

        int M = a.getColumnDimension();
        int N = a.getRowDimension();
        return "(" + N + ", " + M + ")";
    }

    public Matrix createTargetVector(String[] splits) {

        // double[] targetValueSteering = {Double.parseDouble(splits[23])};
        // System.out.println("Target " + Double.parseDouble(splits[20]));
        double[] targetValueSteering = {Double.parseDouble(splits[23])};
        return new Matrix(new double [][] {targetValueSteering}).transpose();
    }

    public Matrix createInputVector(String[] splits) {

        // 6 range values
        String[] inputRangeValues = Arrays.copyOfRange(splits, 5,18);

        double[] training_data = new double[15];
        // double[] training_data = new double[6];
        int i = 0;
        // convert training data to array of doubles
        for (String elem : inputRangeValues) {
            training_data[i] = Double.parseDouble(elem);
            i++;
        }
        // add angle to trackaxis and distance to trackaxis
        training_data[13] = Double.parseDouble(splits[19]); // angle to trackaxis
        training_data[14] = Double.parseDouble(splits[20]); // track position
        // speed training_data[7] = Double.parseDouble(splits[21]);

        return new Matrix(new double [][] {training_data}).transpose();
    }

    public double trainNeuralNetwork(){

        String sCurrentLine;
        double mse = 0;
        double error = 0;
        this.epochs = 0;
        Matrix targetVector = null;
        Matrix inputVector = null;

        try {
            while ((sCurrentLine = this.train_data.readLine()) != null) {
                // System.out.println("Line " + this.epochs);
                String[] splits = sCurrentLine.split(";");
                inputVector = this.createInputVector(splits);
                targetVector = this.createTargetVector(splits);
                // System.out.println("Dim inputVector: " + getDimMatrix(inputVector) + " Dim targetVector: " + getDimMatrix(targetVector));
                double predictedValue = this.MyNN.trainNN(inputVector, targetVector);
                error = Math.pow((targetVector.getArray()[0][0] - predictedValue),2);
                // System.out.println("Error = " + error);
                mse = mse + error;
                this.epochs++;
            }
            System.out.println("last inputVector " + Arrays.deepToString(inputVector.getArray()));
            System.out.println("last targetVector " + Arrays.deepToString(targetVector.getArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (mse/this.epochs);

    }

}
