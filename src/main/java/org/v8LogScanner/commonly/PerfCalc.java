package org.v8LogScanner.commonly;

public class PerfCalc {

    private Long t1 = 0l;

    public void start() {
        t1 = System.nanoTime();
    }

    public void end() {
        t1 = System.nanoTime() - t1;
    }

    public String getTime() {
        float t2 = (float) t1 / 1000000000;
        return String.format("%f sec", t2);
    }
}
