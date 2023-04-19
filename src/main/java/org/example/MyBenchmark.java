package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import org.example.Controller;

@Measurement(iterations = 10)
@Warmup(iterations = 5)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class MyBenchmark {
    long[] imageArray;

    //On every benchmark method invocation,
    //create a new 1000-element integer array
    //with random values between 0 and 9999.
    @Setup(Level.Invocation)
    public void setup() {
        imageArray=new long[1000];
        for (int x=0;x<imageArray.length;x++){
            imageArray[x] = (long)(Math.random() * 10000);
        }
    }

    @Benchmark
    public void bubbleSortBM(){
    }

    @Benchmark
    public void countSetsBM(){
        ArrayList<Integer> knownRoots = new ArrayList<>();
//        Controller.countSets(imageArray,knownRoots);
    }

    public static void main(String[] args) throws RunnerException, IOException {
        Main.main(args);
    }

}
