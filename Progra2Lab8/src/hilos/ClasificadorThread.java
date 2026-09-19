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
        Random random = new Random();
        try {
            while (centro.isCorriendo()) {
                centro.esperarSiPausado();
                Paquete paquete = centro.getListaAlmacen().extraerCuando(
                        p -> System.currentTimeMillis() - p.getHoraEntradaAlmacen() >= 2000
                );
                centro.getContadorEnClasificacion().incrementar();
                centro.setPaqueteClasificando(num - 1, paquete);
                paquete.cambiarEstado(EstadoPaquete.CLASIFICANDO);
                centro.getRegistro().registrar(paquete.getCodigo() + " tomado por " + getName());

                Thread.sleep(500 + random.nextInt(600));
                centro.esperarSiPausado();

                String ruta = centro.rutaParaCiudad(paquete.getCiudad());
                paquete.setRuta(ruta);
                paquete.cambiarEstado(EstadoPaquete.CLASIFICADO);
                centro.getContadorEnClasificacion().decrementar();
                centro.setPaqueteClasificando(num - 1, null);
                centro.getRegistro().registrar(paquete.getCodigo() + " clasificado -> " + ruta);

                centro.getListaEmpaquetado().agregar(paquete);
            }
        } catch (InterruptedException e) {
        }
    }
}
