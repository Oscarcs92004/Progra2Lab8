package modelo;
import paqueteria.estructuras.ContadorSeguro;

public class Repartidor {
    private final int id;
    private final String nombre;
    private final int capMax;
    private final String rutaAsignada;

    private volatile EstadoRepartidor estado = EstadoRepartidor.DISPONIBLE;
    private volatile int cargaActual = 0;
    private final ContadorSeguro paquetesEntregados = new ContadorSeguro();

    public Repartidor(int id, String nombre, int capacidadMaxima, String rutaAsignada) {
        this.id = id;
        this.nombre = nombre;
        this.capMax = capacidadMaxima;
        this.rutaAsignada = rutaAsignada;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getCapacidadMaxima() { return capMax; }
    public String getRutaAsignada() { return rutaAsignada; }

    public EstadoRepartidor getEstado() { return estado; }
    public void setEstado(EstadoRepartidor estado) { this.estado = estado; }

    public int getCargaActual() { return cargaActual; }
    public void setCargaActual(int cargaActual) { this.cargaActual = cargaActual; }

    public int getPaquetesEntregados() { return paquetesEntregados.obtenerComoEntero(); }
    public void incrementarEntregados() { paquetesEntregados.incrementar(); }

    public boolean estaLleno() {
        return cargaActual >= capMax;
    }
}
