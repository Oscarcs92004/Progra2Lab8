package hilos;
import modelo.*;
import sistema.CentroLogistico;
import java.util.Random;

public class ClasificadorThread extends Thread{
    private final CentroLogistico centro;
    private final int num;
    private final Random r = new Random();

    public ClasificadorThread(CentroLogistico centro, int num){
        super("Clasificador-"+num);
        this.centro = centro;
        this.num = num;
    }

    @Override
    public void run() {
       // vacio por mientras
    }
}
