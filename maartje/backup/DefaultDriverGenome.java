import cicontest.torcs.genome.IGenome;

public class DefaultDriverGenome implements IGenome {
    private static final long serialVersionUID = 6534186543165341653L;

    private NeuralNetwork myNN = new NeuralNetwork();
    private NeuralNetwork myAccNN = new NeuralNetwork();

    public NeuralNetwork getMyNN() {
        return myNN;
    }
    public NeuralNetwork getMyAccNN() { return myAccNN; }
}

