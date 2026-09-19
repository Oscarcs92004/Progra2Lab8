package sistema;
import paqueteria.estructuras.ListaEnlazada;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class RegistroEventos {
    private static final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int maxEntradas = 500;
    private final ListaEnlazada<String> entradas = new ListaEnlazada<>();

    public synchronized void registrar(String mensaje) {
        String hora = LocalTime.now().format(formatoHora);
        entradas.agregar(hora + " - " + mensaje);
        while (entradas.tamano() > maxEntradas) {
            entradas.eliminarPrimero();
        }
    }

    public synchronized String[] snapshot() {
        String[] copia = new String[entradas.tamano()];
        int[] i = {0};
        entradas.recorrer(linea -> copia[i[0]++] = linea);
        return copia;
    }

    public synchronized void limpiar() {
        entradas.vaciar();
    }
}
