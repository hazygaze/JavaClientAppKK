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
    double freq;

    public SwingWorkerRealTime(Socket s, double freq) {
        this.s = s;
        this.freq = freq;
    }

    @Override
    public void run() {
        chart = QuickChart.getChart("SwingWorker XChart Real-time", "Time", "Value", "Series", new double[] { 0 }, new double[] { 0 });
        chart.getStyler().setLegendVisible(false);
        chart.setTitle("Chart");
        chart.getStyler().setXAxisTicksVisible(true);
        //chart.getStyler().setXAxisMin(2000.0);

        // Show it
        sw = new SwingWrapper<XYChart>(chart);
        sw.displayChart();

        mySwingWorker = new MySwingWorker();
        mySwingWorker.execute();

    }

    public void reset() {
        mySwingWorker.reset();
    }
    
    private class MySwingWorker extends SwingWorker<Boolean, double[]> {

        DataManager dm = new DataManager(freq);

        public MySwingWorker() {
            //fifo.add(0.0);
        }

        public void reset() {
            dm.reset();
            dm.setFreq(freq); //TODO when resseting the freq in dm is not set
            publish(new double[0]);
        }

        @Override
        protected Boolean doInBackground() throws Exception {

            while (!isCancelled()) {
                //reads data from a socket
                try {
                    DataInputStream dataIs = new DataInputStream(s.getInputStream());
                    while (true) {
                        String data = dataIs.readLine();
//                      System.out.println(data);

                        double doubleData = Double.parseDouble(data);

                        dm.addDataPoint(doubleData);

                        //double toDraw = (doubleData*freq)%(10*freq);
                        //System.out.println(toDraw+ " " +data);

                        //fifo.add(toDraw);

                        //draws last 20 seconds
//                        while (fifo.size() > freq*20) {
//                            fifo.removeFirst();
//                        }
                        //moved to process because it was late
//                        double[] array = new double[fifo.size()];
//                        for (int i = 0; i < fifo.size(); i++) {
//                            array[i] = fifo.get(i);
//                        }

                        //gives data to the process to repaint the chart
                        double[] f = {};
                        publish(f);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(JavaClientKK.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            return true;
        }

        @Override
        protected void process(List<double[]> chunks) {
            long start = System.currentTimeMillis();

//            System.out.println("number of chunks: " + chunks.size());

//            double[] mostRecentDataSet = chunks.get(chunks.size() - 1);

//            int l = fifo.size();
//            double[] array = new double[l-1];
//            for (int i = 0; i < l-1; i++) {
//                array[i] = fifo.get(i);
//            }


            chart.updateXYSeries("Series", null, dm.getDataToDraw(), null);
            sw.repaintChart();

            //show only last 15 seconds
            while (dm.getSize() > freq*15) {
                 dm.removeFirst();
            }

            long duration = System.currentTimeMillis() - start;
            try {
//                Thread.sleep( 500); // 40 ms ==> 25fps
                 Thread.sleep(40 - duration); // 40 ms ==> 2.5fps
                // Thread.sleep(400 - duration); // 40 ms ==> 2.5fps
            } catch (InterruptedException e) {

            }

        }
    }

}
