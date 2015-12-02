package cicontest.torcs.client;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;


public class MessageParser {
    private Hashtable<String, Object> table = new Hashtable();
    private String message;

    public MessageParser(String message) {
        this.message = message;

        StringTokenizer mt = new StringTokenizer(message, "(");
        while (mt.hasMoreElements()) {
            String reading = mt.nextToken();

            int endOfMessage = reading.indexOf(")");
            if (endOfMessage > 0) {
                reading = reading.substring(0, endOfMessage);
            }
            StringTokenizer rt = new StringTokenizer(reading, " ");
            if (rt.countTokens() >= 2) {

                String readingName = rt.nextToken();
                Object readingValue = "";
                if ((readingName.equals("opponents")) || (readingName.equals("track")) || (readingName.equals("wheelSpinVel")) || (readingName.equals("focus"))) {


                    readingValue = new double[rt.countTokens()];
                    int position = 0;

                    while (rt.hasMoreElements()) {
                        String nextToken = rt.nextToken();
                        if ((nextToken.indexOf("#QNAN") >= 0) || (nextToken.indexOf("nan") >= 0)) {
                            this.table = null;
                            return;
                        }
                        try {
                            ((double[]) readingValue)[position] = Double.parseDouble(nextToken);
                        } catch (Exception e) {
                            this.table = null;
                            return;
                        }


                        position++;
                    }
                } else {
                    String token = rt.nextToken();
                    try {
                        readingValue = new Double(token);
                    } catch (Exception e) {
                        this.table = null;
                        return;
                    }
                }

                this.table.put(readingName, readingValue);
            }
        }
    }

    public void printAll() {
        Enumeration<String> keys = this.table.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            System.out.print(key + ":  ");
            System.out.println(this.table.get(key));
        }
    }

    public Object getReading(String key) {
        if (this.table.containsKey(key)) {
            return this.table.get(key);
        }
        return "";
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isValid() {
        return this.table != null;
    }
}