package hilos;
import modelo.*;
import sistema.CentroLogistico;

public class EmpaquetadorThread extends Thread{
    private final CentroLogistico centro;

    public EmpaquetadorThread(CentroLogistico centro, int numero) {
        super("Empaquetador-" + numero);
        this.centro = centro;
    }

    @Override
    public void run() {
        try {
            while (centro.isCorriendo()) {
                centro.esperarSiPausado();

                Paquete paquete = centro.getListaEmpaquetado().extraerSiguiente();

                centro.getContadorEnEmpaquetado().incrementar();
                paquete.cambiarEstado(EstadoPaquete.EMPAQUETANDO);
                centro.getRegistro().registrar(paquete.getCodigo() + " en empaquetado por " + getName());

                Thread.sleep(tiempoSegunPeso(paquete.getPeso()));
                centro.esperarSiPausado();

                paquete.cambiarEstado(EstadoPaquete.EMPAQUETADO);
                centro.getContadorEnEmpaquetado().decrementar();
                centro.getRegistro().registrar(paquete.getCodigo() + " empaquetado");

                paquete.cambiarEstado(EstadoPaquete.EN_EXPEDICION);
                centro.getListaExpedicion(paquete.getRuta()).agregar(paquete);
                centro.getRegistro().registrar(paquete.getCodigo() + " en expedición, ruta " + paquete.getRuta());
            }
        } catch (InterruptedException e) {
        }
    }

    private long tiempoSegunPeso(double pesoKg) {
        if (pesoKg <= 2.0) return 1000;
        if (pesoKg <= 5.0) return 2000;
        return 3000;
    }
}
