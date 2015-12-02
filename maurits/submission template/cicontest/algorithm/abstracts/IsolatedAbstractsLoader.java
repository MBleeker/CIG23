package cicontest.algorithm.abstracts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Hashtable;

public class IsolatedAbstractsLoader extends URLClassLoader {
    private Hashtable<String, Class<?>> classes = new Hashtable();
    private final HashMap<String, Package> packages = new HashMap();

    public IsolatedAbstractsLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public Class<?> loadClass(String className)
            throws ClassNotFoundException {
        Class<?> result = null;
        result = (Class) this.classes.get(className);
        if (result != null) {
            return result;
        }

        if (className.startsWith("cicontest.algorithm.abstracts.")) {
            try {
                byte[] classByte = readFromClasspath(className.replace('.', File.separatorChar) + ".class");
                result = defineClass(className, classByte, 0, classByte.length);

                this.classes.put(className, result);
                this.packages.put(Class.forName(className).getPackage().getName(), Class.forName(className).getPackage());

                return result;
            } catch (Exception e2) {
                try {
                    result = Class.forName(className);
                    this.classes.put(className, result);
                    this.packages.put(result.getPackage().getName(), result.getPackage());
                    return result;
                } catch (Exception e3) {
                    return null;
                }
            }
        }
        return super.loadClass(className);
    }

    private byte[] readFromClasspath(String className)
            throws IOException {
        byte[] buf = new byte[12000000];
        InputStream in = getParent().getResourceAsStream(className);
        int total = 0;
        for (; ; ) {
            int numRead = in.read(buf, total, buf.length - total);

            if (numRead <= 0) {
                break;
            }
            total += numRead;
        }
        byte[] classBuf = new byte[total];
        System.arraycopy(buf, 0, classBuf, 0, total);
        return classBuf;
    }
}
