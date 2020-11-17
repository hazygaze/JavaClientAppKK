package com.kk.start;

import com.kk.forms.ClientForm;
import jdk.nashorn.internal.scripts.JD;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaClientKK {

    private static JavaClientKK instance;
    static Socket s;


    public JavaClientKK() {

    }

    public static JavaClientKK getInstance() {
        if (instance == null) {
            instance = new JavaClientKK();
        }
        return instance;
    }

    public static Socket getS() {
        return s;
    }

    public static void setS(Socket s) {
        JavaClientKK.s = s;
    }


    public boolean sendCommand(String command) {
        try {
            DataOutputStream dataOs = new DataOutputStream(s.getOutputStream());
            dataOs.writeUTF(command);
            return true;
        } catch (SocketException ex) {
            return false;
        } catch (IOException ex) {
            Logger.getLogger(JavaClientKK.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (NullPointerException ex) {
            Logger.getLogger(JavaClientKK.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    private static void receiveData() {
        try {
            DataInputStream dataIs = new DataInputStream(s.getInputStream());
            while (true) {
                String data = dataIs.readLine();

                System.out.println(data);
            }
        } catch (IOException ex) {
            Logger.getLogger(JavaClientKK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            s = new Socket("localhost", 9002);

            System.out.println("Connected to the server.");

//            JFrame form = new JFrame("ClientForm");
//            ClientForm clientForm = (ClientForm) form;
//            clientForm.setContentPane(new ClientForm().panelMain);
//            clientForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            clientForm.pack();
//            clientForm.setVisible(true);


        } catch (IOException ex) {
            Logger.getLogger(JavaClientKK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startDrawing() {
//        SwingWorkerRealTime swrt = new SwingWorkerRealTime(s);
//        swrt.start();
    }
}
