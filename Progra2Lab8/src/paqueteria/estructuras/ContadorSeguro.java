package paqueteria.estructuras;

public class ContadorSeguro {

    private long valor;

    public ContadorSeguro() {
        this(0);
    }

    public ContadorSeguro(long valorInicial) {
        this.valor = valorInicial;
    }

    public synchronized void incrementar() {
        valor++;
    }

    public synchronized long incrementarYObtener() {
        valor++;
        return valor;
    }

    public synchronized void decrementar() {
        valor--;
    }

    public synchronized void sumar(long cantidad) {
        valor += cantidad;
    }

    public synchronized long obtener() {
        return valor;
    }

    public synchronized int obtenerComoEntero() {
        return (int) valor;
    }

    public synchronized void establecer(long nuevoValor) {
        valor = nuevoValor;
    }
}
