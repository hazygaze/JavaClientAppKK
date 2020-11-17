package com.kk.graph;

import com.kk.start.JavaClientKK;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SwingWorkerRealTime extends Thread{
    MySwingWorker mySwingWorker;
    SwingWrapper<XYChart> sw;
    XYChart chart;
    Socket s;
    double freq = 100;

    public SwingWorkerRealTime(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        chart = QuickChart.getChart("SwingWorker XChart Real-time Demo", "Time", "Value", "randomWalk", new double[] { 0 }, new double[] { 0 });
        chart.getStyler().setLegendVisible(true);

        chart.getStyler().setXAxisTicksVisible(true);

        // Show it
        sw = new SwingWrapper<XYChart>(chart);
        sw.displayChart();


        mySwingWorker = new MySwingWorker();
        mySwingWorker.execute();
    }

    private class MySwingWorker extends SwingWorker<Boolean, double[]> {

        LinkedList<Double> fifo = new LinkedList<Double>();

        public MySwingWorker() {

            fifo.add(0.0);
        }

        @Override
        protected Boolean doInBackground() throws Exception {

            while (!isCancelled()) {
                //read data from a socket
                try {
                    DataInputStream dataIs = new DataInputStream(s.getInputStream());
                    while (true) {
                        String data = dataIs.readLine();
                        System.out.println(data);

                        double doubleData = Double.parseDouble(data);

                        //TODO remove jitter

                        double toDraw = (doubleData*freq)%(10*freq);

                        fifo.add(toDraw);
                        if (fifo.size() > 500) {
                            fifo.removeFirst();
                        }

                        double[] array = new double[fifo.size()];
                        for (int i = 0; i < fifo.size(); i++) {
                            array[i] = fifo.get(i);
                        }
                        publish(array);

                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            // eat it. caught when interrupt is called
                            System.out.println("MySwingWorker shut down.");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(JavaClientKK.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            return true;
        }

        @Override
        protected void process(List<double[]> chunks) {

            System.out.println("number of chunks: " + chunks.size());

            double[] mostRecentDataSet = chunks.get(chunks.size() - 1);

            chart.updateXYSeries("randomWalk", null, mostRecentDataSet, null);
            sw.repaintChart();

            long start = System.currentTimeMillis();
            long duration = System.currentTimeMillis() - start;
            try {
                Thread.sleep(40 - duration); // 40 ms ==> 25fps
                // Thread.sleep(400 - duration); // 40 ms ==> 2.5fps
            } catch (InterruptedException e) {
            }

        }
    }
}
