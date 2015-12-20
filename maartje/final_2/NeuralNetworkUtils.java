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
    int epochsAcc = 0;
    int epochsBreak = 0;

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

        double[] targetValueSteering = {Double.parseDouble(splits[23])};
        return new Matrix(new double [][] {targetValueSteering}).transpose();
    }

    public Matrix createTargetVectorAcc(String[] splits) {
        double[] targetValueAcc = {Double.parseDouble(splits[24])}; // value for acceleration in the data
        return new Matrix(new double [][] {targetValueAcc}).transpose();
    }

    public Matrix createTargetVectorBreak(String[] splits) {
        //System.out.println("SPLITS: " + splits[25]);
        double[] targetValueAcc = {Double.parseDouble(splits[25])}; // value for break in the data
        return new Matrix(new double [][] {targetValueAcc}).transpose();
    }

    public Matrix createInputVector(String[] splits) {

        // 6 range values
        String[] inputRangeValues = Arrays.copyOfRange(splits, 5,18);

        double[] training_data = new double[15];
        int i = 0;
        // convert training data to array of doubles
        for (String elem : inputRangeValues) {
            training_data[i] = Double.parseDouble(elem);
            i++;
        }
        // add angle to trackaxis and distance to trackaxis
        training_data[13] = Double.parseDouble(splits[19]); // angle to trackaxis
        training_data[14] = Double.parseDouble(splits[20]); // track position

        return new Matrix(new double [][] {training_data}).transpose();
    }

    public Matrix createInputVectorAcc(String[] splits) {

        String[] inputRangeValuesAcc = Arrays.copyOfRange(splits, 1, 18);

        double[] trainingDataAcc = new double[22];
        int i = 0;

        // these are the 'normal' input values
        for (String elem : inputRangeValuesAcc) {
            trainingDataAcc[i] = Double.parseDouble(elem);
            i++;
        }

        // angle to track axis, distance to track axis, speed, gear
        trainingDataAcc[17] = Double.parseDouble(splits[19]); // angle
        trainingDataAcc[18] = Double.parseDouble(splits[20]); // position
        trainingDataAcc[19] = Double.parseDouble(splits[21]); // speed
        trainingDataAcc[20] = Double.parseDouble(splits[22]); // gear
        trainingDataAcc[21] = Double.parseDouble(splits[23]); // steering

        return new Matrix(new double [][] {trainingDataAcc}).transpose();
    }

    public Matrix createInputVectorBreak(String[] splits) {
        // Now this is still exactly the same as for the acceleration

        String[] inputRangeValuesAcc = Arrays.copyOfRange(splits, 1, 18);

        double[] trainingDataAcc = new double[22];
        int i = 0;

        // these are the 'normal' input values
        for (String elem : inputRangeValuesAcc) {
            trainingDataAcc[i] = Double.parseDouble(elem);
            i++;
        }

        // angle to track axis, distance to track axis, speed, gear
        trainingDataAcc[17] = Double.parseDouble(splits[19]); // angle
        trainingDataAcc[18] = Double.parseDouble(splits[20]); // position
        trainingDataAcc[19] = Double.parseDouble(splits[21]); // speed
        trainingDataAcc[20] = Double.parseDouble(splits[22]); // gear
        trainingDataAcc[21] = Double.parseDouble(splits[23]); // steering

        return new Matrix(new double [][] {trainingDataAcc}).transpose();
    }

    public double trainNeuralNetwork(){

        String sCurrentLine;
        double mse = 0;
        double error = 0;
        this.epochs = 0;
        Matrix targetVector;
        Matrix inputVector;

        try {
            while ((sCurrentLine = this.train_data.readLine()) != null) {
                String[] splits = sCurrentLine.split(";");
                inputVector = this.createInputVector(splits);
                targetVector = this.createTargetVector(splits);
                double predictedValue = this.MyNN.trainNN(inputVector, targetVector);
                error = Math.pow((targetVector.getArray()[0][0] - predictedValue),2);
                mse = mse + error;
                this.epochs++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (mse/this.epochs);

    }

    public double trainNeuralNetworkAcc() {
        String sCurrentLine;
        double mseAcc = 0;
        double errorAcc = 0;
        Matrix targetVectorAcc;
        Matrix inputVectorAcc;
        this.epochsAcc = 0;

        try {
            while ((sCurrentLine = this.train_data.readLine()) != null) {
                String[] splits = sCurrentLine.split(";");
                inputVectorAcc = this.createInputVectorAcc(splits);
                targetVectorAcc = this.createTargetVectorAcc(splits);
                double predictedValueAcc = this.MyNN.trainNN(inputVectorAcc, targetVectorAcc);
                errorAcc = Math.pow((targetVectorAcc.getArray()[0][0] - predictedValueAcc),2);
                mseAcc += errorAcc;

                this.epochsAcc++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("MSE Acc: " + mseAcc/this.epochsAcc);
        return (mseAcc/this.epochsAcc);

    }

    public double trainNeuralNetworkBreak() {
        String sCurrentLine;
        double mseBreak = 0;
        double errorBreak = 0;
        Matrix targetVectorBreak = null;
        Matrix inputVectorBreak = null;
        this.epochsBreak = 0;

        int count = 0;
        try {
            while ((sCurrentLine = this.train_data.readLine()) != null) {
                count += 1;
                String[] splits = sCurrentLine.split(";");
                inputVectorBreak = this.createInputVectorBreak(splits);
                targetVectorBreak = this.createTargetVectorBreak(splits);
                double predictedValueBreak = this.MyNN.trainNN(inputVectorBreak, targetVectorBreak);
                errorBreak = Math.pow((targetVectorBreak.getArray()[0][0] - predictedValueBreak),2);
                mseBreak += errorBreak;

                this.epochsBreak++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("MSE Break: " + mseBreak/this.epochsBreak);

        return (mseBreak/this.epochsBreak);

    }

}