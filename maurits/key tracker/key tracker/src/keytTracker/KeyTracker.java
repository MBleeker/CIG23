package keytTracker;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.Map;
import java.util.HashMap;

public class KeyTracker extends JFrame implements KeyListener {

    JLabel label;
    private Map<String, Integer> keyMap = new HashMap<String, Integer>(); 
    
    public KeyTracker(String s) {
        super(s);
        JPanel p = new JPanel();
        label = new JLabel("Key Listener!");
        p.add(label);
        add(p);
        addKeyListener(this);
        setSize(20000, 100);
        setVisible(true);
        keyMap.put("Right", 0);
        keyMap.put("Left", 0);
        keyMap.put("Down", 0);
        keyMap.put("Up", 0);

    }

    @Override
    public void keyTyped(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            System.out.println("Right key typed");
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            System.out.println("Left key typed");
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
            keyMap.put("Right", 0);
    		keyMap.put("Left", 1);
    		
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            System.out.println("Left key pressed");
            keyMap.put("Up", 1);
    		keyMap.put("Down", 0);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            System.out.println("Left key pressed");
            keyMap.put("Up", 0);
    		keyMap.put("Down", 1);
        }
        

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            System.out.println("Right key Released");
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            System.out.println("Left key Released");
        }
    }

    public static void main(String[] args) {
        new KeyTracker("Key Listener Tester");
    }
}
