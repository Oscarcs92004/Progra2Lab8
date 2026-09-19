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
}


