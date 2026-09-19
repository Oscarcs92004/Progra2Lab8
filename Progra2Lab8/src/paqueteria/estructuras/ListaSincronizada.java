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

    

}
