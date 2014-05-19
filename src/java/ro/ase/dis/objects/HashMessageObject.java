/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ro.ase.dis.objects;

/**
 *
 * @author costin1989
 */
public class HashMessageObject implements MessageObject{
    private String text;
    private String algorithm;


    public HashMessageObject() {
    }

    public HashMessageObject(String text, String algorithm) {
        this.text = text;
        this.algorithm = algorithm;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String toString() {
        return "HashMessageObject{" + "text=" + text + ", algorithm=" + algorithm + '}';
    }
    
}
