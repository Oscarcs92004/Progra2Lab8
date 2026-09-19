package paqueteria.estructuras;

public class ListaEnlazada<T>{
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int size;

    public ListaEnlazada(){
        this.cabeza = null;
        this.cola = null;
        this.size = 0;
    }
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            cola = nuevo;
        }
        size++;
    }

    public boolean eliminar(T dato) {
        if (cabeza == null) {
            return false;
        }
        if (cabeza.getDato().equals(dato)) {
            cabeza = cabeza.getSiguiente();
            if (cabeza == null) {
                cola = null;
            }
            size--;
            return true;
        }
        Nodo<T> actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getDato().equals(dato)) {
                Nodo<T> eliminado = actual.getSiguiente();
                actual.setSiguiente(eliminado.getSiguiente());
                if (eliminado == cola) {
                    cola = actual;
                }
                size--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public T eliminarPrimero() {
        if (cabeza == null) {
            return null;
        }
        T dato = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        if (cabeza == null) {
            cola = null;
        }
        size--;
        return dato;
    }

    public T buscar(Condicion<T> condicion) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (condicion.cumple(actual.getDato())) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public T obtenerMaximo(ComparadorPrioridad<T> comparador) {
        if (cabeza == null) {
            return null;
        }
        Nodo<T> actual = cabeza.getSiguiente();
        T mejor = cabeza.getDato();
        while (actual != null) {
            if (comparador.comparar(actual.getDato(), mejor) > 0) {
                mejor = actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return mejor;
    }

    public T obtener(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + index);
        }
        Nodo<T> actual = cabeza;
        int i = 0;
        while (i < index) {
            actual = actual.getSiguiente();
            i++;
        }
        return actual.getDato();
    }

    public void recorrer(Accion<T> accion) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            accion.ejecutar(actual.getDato());
            actual = actual.getSiguiente();
        }
    }

    public int tamano() {
        return size;
    }

    public boolean estaVacia() {
        return size == 0;
    }

    public void vaciar() {
        cabeza = null;
        cola = null;
        size = 0;
    }
}


