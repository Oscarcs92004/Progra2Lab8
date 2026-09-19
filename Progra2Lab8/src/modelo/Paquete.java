package modelo;
import paqueteria.estructuras.*;

public class Paquete {
    private static final ContadorSeguro CONTADOR = new ContadorSeguro();

    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final String ciudad;
    private final double peso;
    private final Prioridad prioridad;

    private volatile EstadoPaquete estado;
    private volatile String ruta;
    private volatile int intentos;

    private final long horaCreacion;
    private volatile long horaEntrega;
    private volatile long horaEntradaAlmacen;

    public Paquete(String cliente, String direccion, String ciudad, double peso, Prioridad prioridad) {
        this.codigo = String.format("PKG-%05d", CONTADOR.incrementarYObtener());
        this.cliente = cliente;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.peso = peso;
        this.prioridad = prioridad;
        this.estado = EstadoPaquete.RECIBIDO;
        this.ruta = "-";
        this.intentos = 0;
        this.horaCreacion = System.currentTimeMillis();
    }

    public synchronized void cambiarEstado(EstadoPaquete nuevoEstado) {
        if (!estado.puedeTransicionarA(nuevoEstado)) {
            throw new IllegalStateException(
                    "Transición inválida para " + codigo + ": " + estado + " -> " + nuevoEstado);
        }
        this.estado = nuevoEstado;
        if (nuevoEstado == EstadoPaquete.ENTREGADO) {
            this.horaEntrega = System.currentTimeMillis();
        }
    }

    public void registrarIntentoFallido() {
        this.intentos++;
    }

    public double tiempoTotalSegundos() {
        long fin = horaEntrega > 0 ? horaEntrega : System.currentTimeMillis();
        return (fin - horaCreacion) / 1000.0;
    }

    public String getCodigo() { return codigo; }
    public String getCliente() { return cliente; }
    public String getDireccion() { return direccion; }
    public String getCiudad() { return ciudad; }
    public double getPeso() { return peso; }
    public Prioridad getPrioridad() { return prioridad; }
    public EstadoPaquete getEstado() { return estado; }
    public String getRuta() { return ruta; }
    public void setRuta(String ruta) { this.ruta = ruta; }
    public int getIntentos() { return intentos; }
    public void marcarEntradaAlmacen() { this.horaEntradaAlmacen = System.currentTimeMillis(); }
    public long getHoraEntradaAlmacen() { return horaEntradaAlmacen; }


    @Override
    public String toString() {
        return codigo + " [" + estado + "] " + prioridad.getEtiqueta() + " -> " + ruta;
    }
}
