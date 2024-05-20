package sbu.cs.Semaphore;

import java.util.concurrent.Semaphore;

public class Operator extends Thread {
    private Semaphore semaphore;

    public Operator(String name, Semaphore semaphore) {
        super(name);
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            System.out.println("Operator (" + getName() + ") accessed.");
            for (int i = 0; i < 10; i++){
                Resource.accessResource();
                sleep(500);
            }
            System.out.println("Operator (" + getName() + ") exited.");
            semaphore.release();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}