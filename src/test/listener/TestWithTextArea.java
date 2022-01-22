package test.listener;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import omegaui.listener.KeyStrokeListener;

import java.awt.*;

import static java.awt.event.KeyEvent.*;

class TestWithTextArea {
    public static void main(String[] args) {
        JFrame f = new JFrame("KeyStrokeTest");
        f.setSize(500, 400);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextArea textArea = new JTextArea("Just a text area!");
        f.add(textArea, BorderLayout.CENTER);

        KeyStrokeListener listener = new KeyStrokeListener(textArea);
        listener.putKeyStroke((e)->{
            //This block executes only if CTRL, SHIFT & S Keys are pressed
            //But this block will also execute even if any other key was
            //also pressed in combination with specified keys like CTRL + ALT + SHIFT + S 
            System.out.println("You pressed CTRL+SHIFT+S");
        }, VK_CONTROL, VK_SHIFT, VK_S);
        listener.putKeyStroke((e)->{
            //This block executes only if CTRL & S Keys are pressed
            //But this block will not execute if the specified stop key
            //is pressed in combination with the specified keys
            System.out.println("You only pressed CTRL+S and not SHIFT key");
        }, VK_CONTROL, VK_S).setStopKeys(VK_SHIFT);
        listener.putKeyStroke((e)->{
            //This block executes if CTRL, SHIFT & S Keys are pressed
            //Use the useAutoReset() method only if this block does something which
            //Makes the textArea component to lose focus
            //Like in case of showing a dialog
            System.out.println("You only pressed CTRL+SHIFT+V and I cleared the specified key's cache");
        }, VK_CONTROL, VK_SHIFT, VK_V).useAutoReset();

        textArea.addKeyListener(listener);

        f.setVisible(true);
    }
}