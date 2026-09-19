package hilos;
import modelo.*;
import sistema.CentroLogistico;
import java.util.Random;

public class RecepcionThread extends Thread {
    private static final String[] CLIENTES = {
            "Carlos López", "Ana Martínez", "Luis Fernández", "María García",
            "Jorge Pérez", "Laura Sánchez", "Pedro Gómez", "Sofía Ruiz",
            "Diego Torres", "Elena Díaz"
    };
    private static final String[] DIRECCIONES = {
            "Calle Mayor 12", "Av. Diagonal 400", "Passeig de Gràcia 55",
            "Calle Aragón 210", "Rambla Catalunya 88", "Calle Provença 300"
    };

    private final CentroLogistico centro;
    private final Random random = new Random();

    public RecepcionThread(CentroLogistico centro) {
        super("Recepcion");
        this.centro = centro;
    }

}
