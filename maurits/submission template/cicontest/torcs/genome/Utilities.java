package cicontest.torcs.genome;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

public class Utilities {
    public static void saveGenome(IGenome genome, String filename)
            throws IOException {
        FileOutputStream fout = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(genome);
        oos.close();
    }

    public static IGenome loadGenome(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fin = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fin);
        IGenome genome = (IGenome) ois.readObject();
        ois.close();
        return genome;
    }


    public static IGenome loadGenome(URLClassLoader loader, URL genomeurl)
            throws Throwable {
        URLConnection con = genomeurl.openConnection();
        con.connect();
        InputStream fin = con.getInputStream();

        ClassLoaderObjectInputStream ois = new ClassLoaderObjectInputStream(loader, fin);
        IGenome genome = (IGenome) ois.readObject();
        ois.close();
        return genome;
    }
}