package cicontest.torcs.client;

import race.TorcsConfiguration;

import java.io.File;
import java.io.IOException;

public class TorcsUtilities {
    private static String JAVA_COMMAND = null;

    public static String getClientLocation() {
        String fullPath = TorcsUtilities.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (!fullPath.endsWith(java.io.File.separator)) {
            int i = Math.max(fullPath.lastIndexOf("/"), fullPath.lastIndexOf("\\"));
            return fullPath.substring(0, i + 1);
        }
        return fullPath;
    }


    public static void startTORCS(boolean withgui) {
        if (JAVA_COMMAND == null) {
            JAVA_COMMAND = TorcsConfiguration.getInstance().getProperty("java_command");
        }

        try {
            if (withgui) {
                Runtime.getRuntime().exec(JAVA_COMMAND + " -cp " + TorcsConfiguration.getInstance().getProperty("torcs_launcher_jar_path") + File.separator + "TORCSLauncher.jar" + " race.TORCSLauncher " + TorcsConfiguration.getFile().getAbsolutePath());
            } else {
                Process exec = Runtime.getRuntime().exec(JAVA_COMMAND + " -cp " + TorcsConfiguration.getInstance().getProperty("torcs_launcher_jar_path") + File.separator + "TORCSLauncher.jar " + " race.TORCSLauncher " + TorcsConfiguration.getFile().getAbsolutePath()+ " -T");
                exec.waitFor();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}