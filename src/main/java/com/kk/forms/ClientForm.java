package com.kk.forms;

import com.kk.commands.Commands;
import com.kk.graph.SwingWorkerRealTime;
import com.kk.start.JavaClientKK;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientForm extends JFrame {
    private JPanel panelMain;
    private JButton btnStart;
    private JButton btnStop;
    private JPanel panelGraph;

    private static Socket s;

    public Socket getS() {
        return s;
    }

    public void setS(Socket s) {
        this.s = s;
    }

    public ClientForm() {
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(sendCommand(Commands.START)) {
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);

                    SwingWorkerRealTime swrt = new SwingWorkerRealTime(s);
                    swrt.start();

                };

            }
        });
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendCommand(Commands.STOP);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
            }
        });
    }

    private boolean sendCommand(String command) {
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

    public static void main(String[] args) {
        try {
            s = new Socket("localhost", 9002);
            System.out.println("Connected to the server.");

            JFrame clientForm = new JFrame("ClientForm");
            clientForm.setContentPane(new ClientForm().panelMain);
            clientForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            clientForm.setSize(700,700);
            clientForm.pack();
            clientForm.setVisible(true);


        } catch (IOException ex) {
            Logger.getLogger(JavaClientKK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
