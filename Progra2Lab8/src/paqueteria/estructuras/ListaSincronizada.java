package paqueteria.estructuras;

public class ListaSincronizada<T>{
    private final ListaEnlazada<T> lista = new ListaEnlazada<>();
    private final int capMax;
    private final ComparadorPrioridad<T> comparadorPrioridad;

    public ListaSincronizada(int capMax){
        this(capMax,null);
    }

    public ListaSincronizada(int capMax, ComparadorPrioridad<T> comparadorPrioridad) {
        this.capMax = capMax;
        this.comparadorPrioridad = comparadorPrioridad;
    }

    public synchronized void agregar(T dato) throws InterruptedException {
        while (lista.tamano() >= capMax) {
            wait();
        }
        lista.agregar(dato);
        notifyAll(); 
    }

    public synchronized T extraerSiguiente() throws InterruptedException {
        while (lista.estaVacia()) {
            wait();
        }
        T elegido;
        if (comparadorPrioridad != null) {
            elegido = lista.obtenerMaximo(comparadorPrioridad);
        } else {
            elegido = lista.obtener(0);
        }
        lista.eliminar(elegido);
        notifyAll();
        return elegido;
    }

    public synchronized T extraerCuando(Condicion<T> condicion) throws InterruptedException {
        while (true) {
            T elegido = lista.buscar(condicion);
            if (elegido != null) {
                lista.eliminar(elegido);
                notifyAll();
                return elegido;
            }
            wait();
        }
    }

    public synchronized int extraerHasta(int n, Accion<T> receptor) {
        int extraidos = 0;
        while (extraidos < n && !lista.estaVacia()) {
            T elegido;
            if (comparadorPrioridad != null) {
                elegido = lista.obtenerMaximo(comparadorPrioridad);
            } else {
                elegido = lista.obtener(0);
            }
            lista.eliminar(elegido);
            receptor.ejecutar(elegido);
            extraidos++;
        }
        if (extraidos > 0) {
            notifyAll();
        }
        return extraidos;
    }

    public synchronized int tamano() {
        return lista.tamano();
    }

    public synchronized boolean estaVacia() {
        return lista.estaVacia();
    }

    public int capacidad() {
        return capMax;
    }

    public synchronized void recorrer(Accion<T> accion) {
        lista.recorrer(accion);
    }

    public synchronized void despertarTodos() {
        notifyAll();
    }
    
}
