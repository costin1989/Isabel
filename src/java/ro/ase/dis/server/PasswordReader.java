package ro.ase.dis.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PasswordReader {
    private BufferedReader br;
    private static PasswordReader instance;
    
    private PasswordReader(){
        InputStream is = PasswordReader.class.getResourceAsStream("dict");
        br = new BufferedReader(new InputStreamReader(is));
    }
    
    public static PasswordReader getInstance(){
        if(instance==null){
            instance = new PasswordReader();
        }
        return instance;
    }

    public String readPassword() {
        try {
            String pass = br.readLine();
            if(pass == null){
                instance = null;
            }
            return pass;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;

    }
}