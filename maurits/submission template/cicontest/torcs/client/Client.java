package cicontest.torcs.client;

import java.util.StringTokenizer;

@Deprecated
public class Client {
    private static int UDP_TIMEOUT = 10000;


    private static int port;


    private static String host;


    private static String clientId;


    private static boolean verbose;


    private static int maxEpisodes;


    private static int maxSteps;


    private static Controller.Stage stage;

    private static String trackName;


    public static void main(String[] args) {
        parseParameters(args);
        SocketHandler mySocket = new SocketHandler(host, port, verbose);


        Controller driver = load(args[0]);
        driver.setStage(stage);
        driver.setTrackName(trackName);


        float[] angles = driver.initAngles();
        String initStr = clientId + "(init";
        for (int i = 0; i < angles.length; i++) {
            initStr = initStr + " " + angles[i];
        }
        initStr = initStr + ")";

        long curEpisode = 0L;
        boolean shutdownOccurred = false;

        do {
            String inMsg;

            do {
                mySocket.send(initStr);
                inMsg = mySocket.receive(UDP_TIMEOUT);
            } while ((inMsg == null) || (inMsg.indexOf("***identified***") < 0));


            long currStep = 0L;


            for (; ; ) {
                inMsg = mySocket.receive(UDP_TIMEOUT);

                if (inMsg != null) {


                    if (inMsg.indexOf("***shutdown***") >= 0) {
                        shutdownOccurred = true;
                        System.out.println("Server shutdown!");
                        break;
                    }


                    if (inMsg.indexOf("***restart***") >= 0) {
                        driver.reset();
                        if (!verbose) break;
                        System.out.println("Server restarting!");
                        break;
                    }


                    Action action = new Action();
                    if ((currStep < maxSteps) || (maxSteps == 0)) {
                        action = driver.determineAction(new MessageBasedSensorModel(inMsg));
                    } else {
                        action.abandonRace = true;
                    }
                    currStep += 1L;
                    mySocket.send(action.toString());
                } else {
                    System.out.println("Server did not respond within the timeout");
                }

            }
        } while ((++curEpisode < maxEpisodes) && (!shutdownOccurred));


        driver.shutdown();
        mySocket.close();
        System.out.println("Client shutdown.");
        System.out.println("Bye, bye!");
    }


    private static void parseParameters(String[] args) {
        port = 3001;
        host = "localhost";
        clientId = "championship2010";
        verbose = false;
        maxEpisodes = 1;
        maxSteps = 0;
        stage = Controller.Stage.PRACTICE;
        trackName = "unknown";

        for (int i = 1; i < args.length; i++) {
            StringTokenizer st = new StringTokenizer(args[i], ":");
            String entity = st.nextToken();
            String value = st.nextToken();
            if (entity.equals("port")) {
                port = Integer.parseInt(value);
            }
            if (entity.equals("host")) {
                host = value;
            }
            if (entity.equals("id")) {
                clientId = value;
            }
            if (entity.equals("verbose")) {
                if (value.equals("on")) {
                    verbose = true;
                } else if (value.equals(Boolean.valueOf(false))) {
                    verbose = false;
                } else {
                    System.out.println(entity + ":" + value + " is not a valid option");

                    System.exit(0);
                }
            }
            if (entity.equals("id")) {
                clientId = value;
            }
            if (entity.equals("stage")) {
                stage = Controller.Stage.fromInt(Integer.parseInt(value));
            }
            if (entity.equals("trackName")) {
                trackName = value;
            }
            if (entity.equals("maxEpisodes")) {
                maxEpisodes = Integer.parseInt(value);
                if (maxEpisodes <= 0) {
                    System.out.println(entity + ":" + value + " is not a valid option");

                    System.exit(0);
                }
            }
            if (entity.equals("maxSteps")) {
                maxSteps = Integer.parseInt(value);
                if (maxSteps < 0) {
                    System.out.println(entity + ":" + value + " is not a valid option");

                    System.exit(0);
                }
            }
        }
    }

    private static Controller load(String name) {
        Controller controller = null;
        try {
            controller = (Controller) Class.forName(name).newInstance();
        } catch (ClassNotFoundException e) {
            System.out.println(name + " is not a class name");
            System.exit(0);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return controller;
    }
}