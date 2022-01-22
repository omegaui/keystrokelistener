/*
 * An Advanced KeyListener for Java Swing UI
 * Copyright (C) 2022 Omega UI

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package omegaui.listener;
import java.awt.Component;

import java.util.LinkedList;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class KeyStrokeListener implements KeyListener{

    /**
     * The list of keys.
     */
    public LinkedList<Key> keys = new LinkedList<>();

    /**
     * The list of keyStrokeData.
     */
    public LinkedList<KeyStrokeData> keyStrokes = new LinkedList<>();

    /**
     * The component to which the KeyStrokeListener is to be installed.
     */
    public Component c;

    /**
     * The Default Constructor,
     * Accepts java.awt.Component and installs a focus listener to it.
     * @param c
     */
    public KeyStrokeListener(Component c){
        this.c = c;
        c.addFocusListener(new FocusAdapter(){
            @Override
            public void focusLost(FocusEvent e){
                keyStrokes.forEach(keyStrokeData->keyStrokeData.autoReset());
            }
        });
    }

    /**
     * Accepts a listener and the array of keys.
     * @param listener
     * @param keys
     */
    public KeyStrokeData putKeyStroke(KeyStrokeDataListener listener, int... keys){
        var stroke = new KeyStrokeData(listener, keys);
        keyStrokes.add(stroke);
        return stroke;
    }

    /**
     * This method is responsible for the increased performance of this API item compared to the traditional way of implementing this behavior.
     * Enables selective addition of keys into the list keys list by removing redundancy.
     * @param key
     * @return true if the key wasn't already there in the list else returns false
     */
    public boolean offerKey(Key key){
        for(Key kx : keys){
            if(kx.key == key.key)
                return false;
        }
        this.keys.add(key);
        return true;
    }

    /**
     * Searches for the specified key value in the list keys,
     * If found then returns the key else create a new one.
     * @param key
     * @return the key object in the list
     */
    public Key huntKey(int key){
        for(Key kx : keys){
            if(kx.key == key)
                return kx;
        }
        return new Key(key);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        synchronized(c){
            keys.forEach(key->key.checkPressed(e.getKeyCode(), true));
            keyStrokes.forEach(keyStrokeData->keyStrokeData.stroke(e));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        synchronized(c){
            keys.forEach(key->key.checkPressed(e.getKeyCode(), false));
        }
    }

    public interface KeyStrokeDataListener {
        void listen(KeyEvent e);
    }

    /**
     * KeyStrokeData class,
     * Contains the listener and the set of keys stroking which triggers the task.
     */
    public class KeyStrokeData {
        /**
         * List of keys in a specific KeyStrokeData class.
         */
        public LinkedList<Key> keys = new LinkedList<>();

        /**
         * List of keys which prevents the task from execution.
         */
        public LinkedList<Key> stopKeys = new LinkedList<>();

        /**
         * The KeyStrokeDataListener object.
         */
        public KeyStrokeDataListener listener;

        /**
         * Boolean value holding the autoReset flag.
         */
        public volatile boolean useAutoReset = false;

        /**
         * The Default Constructor,
         * Accepts the listener and the keys (array or varargs form).
         * @param listener
         * @param keys
         */
        public KeyStrokeData(KeyStrokeDataListener listener, int... keys){
            this.listener = listener;
            for(int key : keys){
                this.keys.add(huntKey(key));
            }
        }

        /**
         * Setter for stopKeys.
         * @param keys
         * @return current object i.e this
         */
        public KeyStrokeData setStopKeys(int... keys){
            for(int key : keys)
                this.stopKeys.add(huntKey(key));
            return this;
        }

        /**
         * Setter of useAutoReset in builder form.
         * @return current object i.e this
         */
        public KeyStrokeData useAutoReset(){
            this.useAutoReset = true;
            return this;
        }

        /**
         * Executes the task if Strokable.
         * @param e
         */
        public synchronized void stroke(KeyEvent e){
            if(isStrokable()){
                listener.listen(e);
                if(useAutoReset)
                    autoReset();
            }
        }

        /**
         * Resets the keys' cache to pressed=false.
         */
        public void autoReset(){
            for(Key kx : this.keys){
                huntKey(kx.key).setPressed(false);
            }
            for(Key kx : this.stopKeys){
                huntKey(kx.key).setPressed(false);
            }
        }

        /**
         * Checks for the occurrence of key in keys.
         * @param key
         * @return true if found else false
         */
        public boolean containsStrokeKey(Key key){
            for(Key kx : this.keys){
                if(kx.key == key.key)
                    return true;
            }
            return false;
        }

        /**
         * Checks for the occurrence of key in stopKeys.
         * @param key
         * @return true if found else false
         */
        public boolean containsStopKey(Key key){
            for(Key kx : this.stopKeys){
                if(kx.key == key.key)
                    return true;
            }
            return false;
        }

        /**
         * @return true if all the keys are pressed provided no stopKey is pressed.
         */
        public boolean isStrokable(){
            int strokeKeysLength = 0;
            int stopKeysLength = 0;
            for(Key kx : KeyStrokeListener.this.keys){
                if(kx.isPressed()){
                    if(containsStrokeKey(kx))
                        strokeKeysLength++;
                    else if(containsStopKey(kx))
                        stopKeysLength++;
                }
            }
            return (strokeKeysLength == this.keys.size()) && stopKeysLength == 0;
        }
    }

    /**
     * Key class,
     * Holds key's code and pressed state.
     */
    public class Key {
        /**
         * The key's code.
         */
        public int key;

        /**
         * Pressed state.
         */
        public volatile boolean pressed = false;

        /**
         * The Default Constructor,
         * Accepts keyCode and offers the key to KeyStrokeListener.
         * @param key
         */
        public Key(int key){
            this.key = key;
            offerKey(this);
        }

        /**
         * Puts pressed state if specified keyCode matches the current Code.
         * @param key
         * @param pressed
         */
        public void checkPressed(int key, boolean pressed){
            if(this.key == key){
                setPressed(pressed);
            }
        }

        /**
         * Setter for pressed.
         * @param pressed
         */
        public void setPressed(boolean pressed){
            this.pressed = pressed;
        }

        /**
         * Getter for pressed.
         * @return pressed state
         */
        public boolean isPressed(){
            return pressed;
        }

        @Override
        public String toString(){
            return KeyEvent.getKeyText(key);
        }
    }
}