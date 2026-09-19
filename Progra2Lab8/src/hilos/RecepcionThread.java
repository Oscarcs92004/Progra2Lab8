package hilos;
import modelo.*;
import sistema.CentroLogistico;
import java.util.Random;

public class RecepcionThread extends Thread {
    private static final String[] CLIENTES = {
            "Carlos López", "Ana Martínez", "Luis Fernández", "María García",
            "Jorge Pérez", "Laura Sánchez", "Pedro Gómez", "Sofía Ruiz",
            "Diego Torres", "Elena Díaz"
    };
    private static final String[] DIRECCIONES = {
            "Calle Mayor 12", "Av. Diagonal 400", "Passeig de Gràcia 55",
            "Calle Aragón 210", "Rambla Catalunya 88", "Calle Provença 300"
    };

    private final CentroLogistico centro;
    private final Random random = new Random();

    public RecepcionThread(CentroLogistico centro) {
        super("Recepcion");
        this.centro = centro;
    }

    @Override
    public void run() {
        try {
            String[] ciudades = centro.getCiudades();
            while (centro.isCorriendo()) {
                centro.esperarSiPausado();

                Paquete paquete = new Paquete(
                        CLIENTES[random.nextInt(CLIENTES.length)],
                        DIRECCIONES[random.nextInt(DIRECCIONES.length)],
                        ciudades[random.nextInt(ciudades.length)],
                        redondear(0.5 + random.nextDouble() * 9.5),
                        Prioridad.aleatoria(random)
                );

                centro.getListaRecepcion().agregar(paquete);
                centro.getEstadisticas().registrarGenerado();
                centro.getRegistro().registrar(paquete.getCodigo() + " recibido (" + paquete.getPrioridad().getEtiqueta() + ")");

                Thread.sleep(300 + random.nextInt(400));
                centro.esperarSiPausado();

                centro.getListaRecepcion().extraerSiguiente();
                paquete.cambiarEstado(EstadoPaquete.ALMACENADO);
                centro.getListaAlmacen().agregar(paquete);
                centro.getRegistro().registrar(paquete.getCodigo() + " almacenado");

                Thread.sleep(1500 + random.nextInt(1500));
            }
        } catch (InterruptedException e) {
        }
    }

    private double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }
}
