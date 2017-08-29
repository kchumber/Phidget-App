import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.DetachListener;
import com.phidgets.event.ErrorEvent;
import com.phidgets.event.ErrorListener;
import com.phidgets.event.InputChangeEvent;
import com.phidgets.event.InputChangeListener;
import com.phidgets.event.OutputChangeEvent;
import com.phidgets.event.OutputChangeListener;
import com.phidgets.event.SensorChangeEvent;
import com.phidgets.event.SensorChangeListener;

public class SensorToServer implements SensorChangeListener, InputChangeListener,
        AttachListener, DetachListener, ErrorListener, OutputChangeListener {
    
    public static int LED_PORT=7;
    public static int INPUT_PORT=3;
    public static int ANALOG_PORT=7;
    public static String sensorServerURL = "http://localhost:8080/PhidgetServer/sensorLocation";
     public static void main(String[] args) throws PhidgetException {

        new SensorToServer();
    }

    public SensorToServer() throws PhidgetException {

        InterfaceKitPhidget phid = new InterfaceKitPhidget();

        try {

            phid.addAttachListener(this);
            phid.addDetachListener(this);
            phid.addSensorChangeListener(this);
            phid.addInputChangeListener(this);
            //phid.addOutputChangeListener(this);
            phid.openAny();

            phid.waitForAttachment();

            System.out.println(phid.getDeviceType());
            System.out.println("Serial Number " + phid.getSerialNumber());
            System.out.println("Device Version " + phid.getDeviceVersion());

            System.out.println("Looping...\n");
            boolean led = true;
            boolean dummy = true;
            for (; dummy;) {

                phid.setOutputState(LED_PORT, led);
                if (!phid.getInputState(INPUT_PORT))
                    led = !led;
                try {
                    // changing the output too often seems to mess up the sensor
                    // box
                    // Thread.sleep(100 + phid.getSensorValue(7));
                    Thread.sleep(1000);
                    // Thread.sleep(1000);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } finally {
            phid.close();
            System.out.println("Closed and exiting...");
        }
    }

    public void sensorChanged(SensorChangeEvent arg0) {
        // System.out.println(arg0);
    		int sensorValue = arg0.getValue();
    		int sensorIndex = arg0.getIndex();
        System.out.println("Slider value is now "+sensorValue);
        String sendResult = sendToServer(sensorValue, sensorIndex);
        try {
        		System.out.println("Sleeping.... 1 sec");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       System.out.println("Sent to server, result: "+sendResult);
    }
public void inputChanged(InputChangeEvent arg0) {
        System.out.println(arg0);
    }

    public void attached(AttachEvent arg0) {
        System.out.println(arg0);
    }

    public void detached(DetachEvent arg0) {
        System.out.println(arg0);
    }

    public void error(ErrorEvent arg0) {
        System.out.println(arg0);
    }

    public void outputChanged(OutputChangeEvent arg0) {
        System.out.println(arg0);
    }
    public String sendToServer(int sensorValue, int sensorIndex){
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String sensor = "";
        if (sensorIndex == 0)
        	sensor = "slider";
        else if
        (sensorIndex == 1)
        	sensor = "force";
        
        String fullURL = sensorServerURL + "?"+sensor+ "="+sensorValue;
       System.out.println("Sending data to: "+fullURL);
        String line;
        String result = "";
        try {
           url = new URL(fullURL);
           conn = (HttpURLConnection) url.openConnection();
           conn.setRequestMethod("GET");
           rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
           while ((line = rd.readLine()) != null) {
              result += line;
           }
           rd.close();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return "done : "+result;
//        return result;
    	
    }
}


