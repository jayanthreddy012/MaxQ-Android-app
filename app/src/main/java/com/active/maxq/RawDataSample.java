package com.active.maxq;

/**
 * Created by Trung on 1/24/2018.
 */

public class RawDataSample {
    private int id;
    private double temp;
    private int boxStt;
    private int payloadStt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getBoxStt() {
        return boxStt;
    }

    public void setBoxStt(int boxStt) {
        this.boxStt = boxStt;
    }

    public int getPayloadStt() {
        return payloadStt;
    }

    public void setPayloadStt(int payloadStt) {
        this.payloadStt = payloadStt;
    }

    @Override
    public String toString() {
        return "RawDataSample{" +
                "id='" + id + '\'' +
                ", temp=" + temp +
                ", boxStt=" + boxStt +
                ", payloadStt=" + payloadStt +
                '}';
    }
}
