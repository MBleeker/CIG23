
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.Map;
import java.util.HashMap;

public class KeyTracker extends JFrame implements KeyListener {

    JLabel label;
	private String keyEvent;
	private Map<String, Integer> keyMap = new HashMap<String, Integer>(); 
    
	public KeyTracker(String s) {
        super(s);
        JPanel p = new JPanel();
        label = new JLabel("Key Listener!");
        p.add(label);
        add(p);
        addKeyListener(this);
        setSize(100, 200);
        setVisible(true);
        keyMap.put("Right", 0);
        keyMap.put("Left", 0);
        keyMap.put("Down", 0);
        keyMap.put("Up", 0);
    }
    
    void setKeyEvent(KeyEvent e){
    	System.out.println("test");
    	if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            System.out.println("Right key pressed");
            this.keyEvent =  "Right";
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            System.out.println("Left key pressed");
            this.keyEvent = "Left";
    		
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            System.out.println("Up key pressed");
            this.keyEvent = "Up";
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            System.out.println("Down key pressed");
            this.keyEvent = "Down";
        }
    }
    
    int getLeftSteering(){
    	return keyMap.get("Left");
    }
    
    int getRightSteering(){
    	return keyMap.get("Right");
    }
    
    int accelerate(){
    	return keyMap.get("Up");
    }
    
    int doBreake(){
    	return keyMap.get("Down");
    }
    
    String getKeyEvent(){
    	return this.keyEvent;
    }
    
    
    @Override
    public void keyTyped(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            System.out.println("Right key typed!");
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            System.out.println("Left key typed!");
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            System.out.println("Down key typed!");
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            System.out.println("Up key typed!");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    	if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
    		 System.out.println("Right key pressed");
    		keyMap.put("Right", 1);
    		keyMap.put("Left", 0);
        }
    	if (e.getKeyCode() == KeyEvent.VK_LEFT) {
    		 System.out.println("Left key pressed");
    		 keyMap.put("Left", 1);
    		 keyMap.put("Right", 0);
        }
    	if (e.getKeyCode() == KeyEvent.VK_DOWN) {
    		 System.out.println("Down key pressed");
    		keyMap.put("Down", 1);
    		keyMap.put("Up", 0);
        }
    	if (e.getKeyCode() == KeyEvent.VK_UP) {
    		 System.out.println("Down key pressed");
    		 keyMap.put("Up", 1);
    		 keyMap.put("Down", 0);
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    	if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
    		keyMap.put("Right", 0);
        }
    	if (e.getKeyCode() == KeyEvent.VK_LEFT) {
    		 keyMap.put("Left", 0);
        }
    	if (e.getKeyCode() == KeyEvent.VK_DOWN) {
    		keyMap.put("Down", 0);
        }
    	if (e.getKeyCode() == KeyEvent.VK_UP) {
    		 keyMap.put("Up", 0);
        }
    }
}
