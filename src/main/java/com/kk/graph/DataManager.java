package com.kk.graph;

import java.util.LinkedList;

public class DataManager {

    LinkedList<Double> fifo = new LinkedList<Double>();
    private double freq;

    public double getFreq() {
        return freq;
    }

    public void setFreq(double freq) {
        this.freq = freq;
    }

    public DataManager(double freq) {
        this.freq = freq;
    }

    public void addDataPoint(double data) {
        double linData = linearize(data);
        System.out.println(data + " "+ linData);
        fifo.add(linData);
    }

    public double[] getDataToDraw() {
        int l = fifo.size();
        double[] array = new double[l-1];
        for(int i =0; i<l-1; i++) {
            array[i] = (fifo.get(i)*freq)%(10*freq);
        }
        return array;
    }

    public double linearize(double data) {
        /*observing the signal and given
            O(n) = n/f + U[-1,1]/10*f = 1/f * (n+-0.1)
         */
        return Math.round(data*freq) / freq;
    }

    public void reset(double freq) {
        fifo = new LinkedList<Double>();
        this.freq = freq;
    }

    public int getSize() {
        return fifo.size();
    }

    public void removeFirst() {
        fifo.removeFirst();
    }
}
