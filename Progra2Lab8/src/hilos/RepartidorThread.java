package hilos;
import paqueteria.estructuras.ListaEnlazada;
import modelo.*;
import sistema.CentroLogistico;
import java.util.Random;

public class RepartidorThread extends Thread {
    private final CentroLogistico centro;
    private final Repartidor repartidor;
    private final Random random = new Random();

    public RepartidorThread(CentroLogistico centro, Repartidor repartidor) {
        super("Repartidor-" + repartidor.getId());
        this.centro = centro;
        this.repartidor = repartidor;
    }

    @Override
    public void run() {
        try {
            while (centro.isCorriendo()) {
                centro.esperarSiPausado();
                repartidor.setEstado(EstadoRepartidor.DISPONIBLE);

                paqueteria.estructuras.ListaSincronizada<modelo.Paquete> listaExpedicion = centro.getListaExpedicion(repartidor.getRutaAsignada());

                Paquete primero = listaExpedicion.extraerSiguiente();

                ListaEnlazada<Paquete> carga = new ListaEnlazada<>();
                cargarEnVehiculo(primero, carga);

                int restante = repartidor.getCapacidadMaxima() - carga.tamano();
                if (restante > 0) {
                    listaExpedicion.extraerHasta(restante, paquete -> cargarEnVehiculo(paquete, carga));
                }
                repartidor.setCargaActual(carga.tamano());
                centro.esperarSiPausado();

                repartidor.setEstado(EstadoRepartidor.EN_RUTA);
                centro.getRegistro().registrar(repartidor.getNombre() + " inicia ruta " + repartidor.getRutaAsignada() + " con " + carga.tamano() + " paquete(s)");
                Thread.sleep(800 + random.nextInt(700));

                final int[] entregadosEnViaje = {0};
                carga.recorrer(paquete -> {
                    try {
                        centro.esperarSiPausado();
                        entregarPaquete(paquete, listaExpedicion);
                        entregadosEnViaje[0]++;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

                repartidor.setEstado(EstadoRepartidor.REGRESANDO);
                centro.getRegistro().registrar(repartidor.getNombre() + " regresando al centro");
                Thread.sleep(500 + random.nextInt(500));
                repartidor.setCargaActual(0);
            }
        } catch (InterruptedException e) {
        } catch (RuntimeException e) {
            if (!(e.getCause() instanceof InterruptedException)) {
                throw e;
            }
        }
    }

    private void cargarEnVehiculo(Paquete paquete, ListaEnlazada<Paquete> carga) {
        paquete.cambiarEstado(EstadoPaquete.CARGANDO);
        carga.agregar(paquete);
        repartidor.setEstado(EstadoRepartidor.CARGANDO);
        repartidor.setCargaActual(carga.tamano());
        centro.getRegistro().registrar(paquete.getCodigo() + " asignado a " + repartidor.getNombre());
    }

    private void entregarPaquete(Paquete paquete, paqueteria.estructuras.ListaSincronizada<Paquete> listaExpedicion) throws InterruptedException {
        repartidor.setEstado(EstadoRepartidor.ENTREGANDO);
        paquete.cambiarEstado(EstadoPaquete.EN_REPARTO);
        Thread.sleep(600 + random.nextInt(900));

        boolean exito = random.nextInt(100) < 80;

        if (exito) {
            paquete.cambiarEstado(EstadoPaquete.ENTREGADO);
            centro.getListaEntregados().agregar(paquete);
            repartidor.incrementarEntregados();
            centro.getEstadisticas().registrarEntregado(paquete.tiempoTotalSegundos());
            centro.getRegistro().registrar(paquete.getCodigo() + " entregado por " + repartidor.getNombre());
        } else {
            paquete.registrarIntentoFallido();
            centro.getRegistro().registrar(paquete.getCodigo() + " - cliente ausente (intento " + paquete.getIntentos() + ")");
            if (paquete.getIntentos() >= 3) {
                paquete.cambiarEstado(EstadoPaquete.DEVUELTO);
                centro.getListaDevueltos().agregar(paquete);
                centro.getEstadisticas().registrarDevuelto();
                centro.getRegistro().registrar(paquete.getCodigo() + " -> DEVUELTO");
            } else {
                paquete.cambiarEstado(EstadoPaquete.NUEVO_INTENTO);
                listaExpedicion.agregar(paquete);
            }
        }
    }
}
