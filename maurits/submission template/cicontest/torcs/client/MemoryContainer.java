package cicontest.torcs.client;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.HashMap;


public class MemoryContainer
        implements Serializable {
    private static final long serialVersionUID = 1870802441415464686L;
    private static HashMap<Class<?>, MemoryContainer> instance = new HashMap();
    private Class<?> owner;
    private HashMap<String, Serializable> memories = new HashMap();
    private boolean remote = false;

    private MemoryContainer(Class<?> owner) {
        this.owner = owner;
    }

    private static boolean inJar(Class<?> owner) {
        String classname = owner.getName();
        classname = classname.split("\\.")[(classname.split("\\.").length - 1)];
        String bp = new File(owner.getResource(classname + ".class").getPath()).getParent();

        if (bp.contains("!")) {
            return true;
        }
        return false;
    }

    public static void removeInstance(Class<?> owner) {
        instance.remove(owner);
    }

    public static MemoryContainer getInstance(Class<?> owner) {
        if (!instance.containsKey(owner)) {
            if (System.getProperty("memorydir") != null) {

                System.out.println("Memory from file: " + System.getProperty("memorydir") + File.separator + owner.getCanonicalName() + ".mem");
                XStream xstream = new XStream(new StaxDriver());
                try {
                    FileInputStream fin = new FileInputStream(System.getProperty("memorydir") + File.separator + owner.getCanonicalName() + ".mem");
                    MemoryContainer hm = (MemoryContainer) xstream.fromXML(fin);
                    instance.put(owner, hm);
                    hm.remote = true;
                    fin.close();

                    return hm;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    instance.put(owner, new MemoryContainer(owner));
                    return (MemoryContainer) instance.get(owner);
                } catch (IOException e) {
                    e.printStackTrace();
                    instance.put(owner, new MemoryContainer(owner));
                    return (MemoryContainer) instance.get(owner);
                }
            }
            if (!inJar(owner)) {
                XStream xstream = new XStream(new StaxDriver());


                try {
                    FileInputStream fin = new FileInputStream(getCurrentPath(owner) + File.separator + "memory" + File.separator + owner.getCanonicalName() + ".mem");

                    MemoryContainer hm = (MemoryContainer) xstream.fromXML(fin);


                    instance.put(owner, hm);
                    fin.close();
                    return hm;
                } catch (FileNotFoundException e) {
                    instance.put(owner, new MemoryContainer(owner));
                    return (MemoryContainer) instance.get(owner);
                } catch (IOException e) {
                    e.printStackTrace();


                    return null;
                }
            }


            URL url = owner.getResource("/memory/" + owner.getCanonicalName() + ".mem");
            return getInstance(owner, (URLClassLoader) owner.getClassLoader(), url);
        }


        return (MemoryContainer) instance.get(owner);
    }

    public static MemoryContainer getInstance(Class<?> owner, URLClassLoader classloader, URL url) {
        if (!instance.containsKey(owner)) {
            XStream xstream = new XStream(new StaxDriver());
            xstream.setClassLoader(classloader);
            try {
                URLConnection con = url.openConnection();
                con.connect();
                InputStream fin = con.getInputStream();
                MemoryContainer hm = (MemoryContainer) xstream.fromXML(fin);

                hm.remote = true;

                instance.put(owner, hm);
                fin.close();
                return hm;
            } catch (IOException e) {
                e.printStackTrace();


                return null;
            }
        }
        return (MemoryContainer) instance.get(owner);
    }

    public void store() {
        if (!this.remote) {
            try {
                File memdir = new File(getCurrentPath(this.owner) + File.separator + "memory");
                if (!memdir.isDirectory()) {
                    memdir.mkdir();
                }

                FileOutputStream fout = new FileOutputStream(getCurrentPath(this.owner) + File.separator + "memory" + File.separator + this.owner.getCanonicalName() + ".mem");


                XStream xstream = new XStream(new StaxDriver());
                xstream.toXML(this, fout);
                fout.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Serializable clearMemory(String key) {
        return (Serializable) this.memories.remove(key);
    }

    public Serializable getMemory(String key) {
        return (Serializable) this.memories.get(key);
    }

    public boolean hasMemory(String key) {
        return this.memories.containsKey(key);
    }

    public void putMemory(String key, Serializable mem) {
        this.memories.put(key, mem);
    }

    private static String getCurrentPath(Class<?> owner) {
        String classname = owner.getName();
        classname = classname.split("\\.")[(classname.split("\\.").length - 1)];
        String bp = new File(owner.getResource(classname + ".class").getPath()).getParent();

        if (bp.contains("!")) {
            bp = bp.split("!")[0];
            bp = new File(bp).getParent();
        } else {
            for (int i = 0; i < owner.getName().split("\\.").length; i++) {
                bp = new File(bp).getParent();
            }
        }

        if (bp.contains("file:")) {
            bp = bp.split("file:")[1];
        }
        return bp;
    }
}