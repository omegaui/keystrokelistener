# keystrokelistener

An advanced KeyListener for Java Swing UI.

In Swing, We don't have any default way of mapping a set of KeyStrokes to a specific task.

Or I should say **We didn't had one earlier** but now, it is possible with `KeyStrokeListener`.

**Mapping Set of KeyStrokes with a runnable is now possible without creating a custom implementation**.

Either use the source code or download the tiny [precompiled-jar](https://raw.githubusercontent.com/omegaui/keystrokelistener/main/out/keystrokelistener.jar).

# Required Java Version
If you want to use this library through the **jar** then you need to have at least **17** as the compilation level.

Simply, you need java 17 if you want to use this library through the **jar**.

# Usage

This API is **super simple** to understand.

- Just create a `KeyStrokeListener` object by passing the component object
in the constructor
- Use `putKeyStroke` method to map a task with a keystroke
- pass the `KeyStrokeListener`'s Object to `component.addKeyListener()` method

Like below

```java
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
            //This block executes if CTRL, SHIFT & V Keys are pressed and uses autoReset()
            //Use the useAutoReset() method only if this block does something which
            //Makes the textArea component to lose focus
            //Like in case of showing a dialog
            System.out.println("You only pressed CTRL+SHIFT+V and I cleared the specified key's cache");
        }, VK_CONTROL, VK_SHIFT, VK_V).useAutoReset();

        textArea.addKeyListener(listener);

        f.setVisible(true);
    }
}
```

### Execution of above example

Running the above code will create a window containing text area

> In the above example, the `e` in the lambda expressions is the object of `KeyEvent` class

If you press

- **Ctrl + SHIFT + S** you will see `You pressed CTRL+SHIFT+S` as output
- **Ctrl + S** you will see `You only pressed CTRL+S and not SHIFT key` as output
- **Ctrl + SHIFT + V** you will see `You only pressed CTRL+SHIFT+V and I cleared the specified key's cache` as output


