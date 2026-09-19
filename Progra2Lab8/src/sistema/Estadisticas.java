package sistema;
import paqueteria.estructuras.ContadorSeguro;

public class Estadisticas {
    private final ContadorSeguro generados = new ContadorSeguro();
    private final ContadorSeguro entregados = new ContadorSeguro();
    private final ContadorSeguro devueltos = new ContadorSeguro();
    private final ContadorSeguro sumaTiempoEntregaMs = new ContadorSeguro();

    public void registrarGenerado() {
        generados.incrementar();
    }

    public void registrarEntregado(double segundos) {
        entregados.incrementar();
        sumaTiempoEntregaMs.sumar((long) (segundos * 1000));
    }

    public void registrarDevuelto() {
        devueltos.incrementar();
    }

    public int getGenerados() { return generados.obtenerComoEntero(); }
    public int getEntregados() { return entregados.obtenerComoEntero(); }
    public int getDevueltos() { return devueltos.obtenerComoEntero(); }

    public int getEnProceso() {
        int enProceso = getGenerados() - getEntregados() - getDevueltos();
        return Math.max(enProceso, 0);
    }

    public double getTiempoPromedioSegundos() {
        int e = getEntregados();
        if (e == 0) return 0.0;
        return (sumaTiempoEntregaMs.obtener() / (double) e) / 1000.0;
    }

    public void reiniciar() {
        generados.establecer(0);
        entregados.establecer(0);
        devueltos.establecer(0);
        sumaTiempoEntregaMs.establecer(0);
    }
}
