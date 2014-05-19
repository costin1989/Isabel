/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ro.ase.dis.objects;

import java.io.Serializable;

/**
 *
 * @author costin1989
 */
public class PrimeMessageObject implements MessageObject{
    private String number;


    public PrimeMessageObject() {
    }

    public PrimeMessageObject(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "PrimeMessageObject{" + "number=" + number + '}';
    }

    
}
