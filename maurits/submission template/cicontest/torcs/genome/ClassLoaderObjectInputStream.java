package cicontest.torcs.genome;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ClassLoaderObjectInputStream extends ObjectInputStream {
    private ClassLoader classLoader;

    public ClassLoaderObjectInputStream(ClassLoader classLoader, InputStream in) throws java.io.IOException {
        super(in);
        this.classLoader = classLoader;
    }

    protected Class<?> resolveClass(ObjectStreamClass desc) throws java.io.IOException, ClassNotFoundException {
        try {
            String name = desc.getName();
            return Class.forName(name, false, this.classLoader);
        } catch (ClassNotFoundException e) {
        }
        return super.resolveClass(desc);
    }

}