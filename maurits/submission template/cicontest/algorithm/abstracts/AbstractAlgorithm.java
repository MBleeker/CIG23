package cicontest.algorithm.abstracts;

import java.io.Serializable;

public abstract class AbstractAlgorithm implements Serializable {
    private static final long serialVersionUID = 1L;

    public void run() {
        run(false);
    }

    public abstract void run(boolean paramBoolean);
}