package sbu.cs.Semaphore;

import java.util.concurrent.Semaphore;

public class Controller {



    public static void main(String[] args) {

        final Semaphore semaphore = new Semaphore(2);

        Operator operator1 = new Operator("operator1", semaphore);
        Operator operator2 = new Operator("operator2", semaphore);
        Operator operator3 = new Operator("operator3", semaphore);
        Operator operator4 = new Operator("operator4", semaphore);
        Operator operator5 = new Operator("operator5", semaphore);

        operator1.start();
        operator2.start();
        operator3.start();
        operator4.start();
        operator5.start();

    }
}
