package sistema;
import paqueteria.estructuras.*;
import hilos.*;
import modelo.*;


public class CentroLogistico {
    public static final int CAP_RECEPCION = 10;
    public static final int CAP_ALMACEN = 20;
    public static final int CAP_EMPAQUETADO = 8;
    public static final int CAP_EXPEDICION_POR_RUTA = 8;

    private static final ComparadorPrioridad<Paquete> POR_PRIORIDAD = (a, b) -> a.getPrioridad().getNivel() - b.getPrioridad().getNivel();

    private final ListaSincronizada<Paquete> listaRecepcion = new ListaSincronizada<>(CAP_RECEPCION);
    private final ListaSincronizada<Paquete> listaAlmacen = new ListaSincronizada<>(CAP_ALMACEN, POR_PRIORIDAD);
    private final ListaSincronizada<Paquete> listaEmpaquetado = new ListaSincronizada<>(CAP_EMPAQUETADO, POR_PRIORIDAD);
    private final ListaSincronizada<Paquete> listaEntregados = new ListaSincronizada<>(Integer.MAX_VALUE);
    private final ListaSincronizada<Paquete> listaDevueltos = new ListaSincronizada<>(Integer.MAX_VALUE);

    private final String[] rutas = {"R01", "R02", "R03", "R04"};
    private final ListaSincronizada<Paquete>[] listasExpedicion;

    private final ContadorSeguro enClasificacion = new ContadorSeguro();
    private final ContadorSeguro enEmpaquetado = new ContadorSeguro();
    private final Paquete[] paquetesClasificando = new Paquete[3];
    private final Paquete[] paquetesEmpaquetando = new Paquete[2];

    private final String[] ciudades = {
            "Barcelona Centro", "Eixample", "Gràcia", "Sant Martí", "Badalona",
            "Sants", "Hospitalet", "Sarrià","San Pedro Sula", "Tegucigalpa"
    };
    private final String[] rutaDeCadaCiudad = {
            "R01", "R01", "R02", "R03", "R04",
            "R01", "R02", "R03","R01", "R03"
    };

    private final Estadisticas estadisticas = new Estadisticas();
    private final RegistroEventos registro = new RegistroEventos();

    private final Repartidor[] repartidores;

    private final Object candadoPausa = new Object();
    private volatile boolean corriendo = false;
    private volatile boolean pausado = false;

    private RecepcionThread hiloRecepcion;
    private ClasificadorThread[] hilosClasificadores;
    private EmpaquetadorThread[] hilosEmpaquetadores;
    private RepartidorThread[] hilosRepartidores;

    @SuppressWarnings("unchecked")
    public CentroLogistico() {
        listasExpedicion = new ListaSincronizada[rutas.length];
        for (int i = 0; i < rutas.length; i++) {
            listasExpedicion[i] = new ListaSincronizada<>(CAP_EXPEDICION_POR_RUTA);
        }

        repartidores = new Repartidor[]{
                new Repartidor(1, "Repartidor 1", 5, "R01"),
                new Repartidor(2, "Repartidor 2", 4, "R02"),
                new Repartidor(3, "Repartidor 3", 6, "R03"),
                new Repartidor(4, "Repartidor 4", 5, "R04"),
        };
    }

    public synchronized void iniciar() {
        if (corriendo) {
            return;
        }
        corriendo = true;
        pausado = false;
        registro.registrar("Simulación iniciada");

        hiloRecepcion = new RecepcionThread(this);
        hiloRecepcion.start();

        hilosClasificadores = new ClasificadorThread[3];
        for (int i = 0; i < hilosClasificadores.length; i++) {
            hilosClasificadores[i] = new ClasificadorThread(this, i + 1);
            hilosClasificadores[i].start();
        }

        hilosEmpaquetadores = new EmpaquetadorThread[2];
        for (int i = 0; i < hilosEmpaquetadores.length; i++) {
            hilosEmpaquetadores[i] = new EmpaquetadorThread(this, i + 1);
            hilosEmpaquetadores[i].start();
        }

        hilosRepartidores = new RepartidorThread[repartidores.length];
        for (int i = 0; i < repartidores.length; i++) {
            hilosRepartidores[i] = new RepartidorThread(this, repartidores[i]);
            hilosRepartidores[i].start();
        }
    }

    public synchronized void pausar() {
        if (!corriendo || pausado) {
            return;
        }
        pausado = true;
        registro.registrar("Simulación pausada");
    }

    public synchronized void reanudar() {
        if (!corriendo || !pausado) {
            return;
        }
        pausado = false;
        registro.registrar("Simulación reanudada");
        synchronized (candadoPausa) {
            candadoPausa.notifyAll();
        }
    }

    public synchronized void detener() {
        if (!corriendo) {
            return;
        }
        corriendo = false;
        pausado = false;
        registro.registrar("Simulación detenida");

        synchronized (candadoPausa) {
            candadoPausa.notifyAll();
        }
        listaRecepcion.despertarTodos();
        listaAlmacen.despertarTodos();
        listaEmpaquetado.despertarTodos();
        for (ListaSincronizada<Paquete> l : listasExpedicion) {
            l.despertarTodos();
        }

        interrumpirTodos();
    }

    public synchronized void reiniciar() {
        detener();
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        }
        vaciarListas();
        estadisticas.reiniciar();
        registro.limpiar();
        enClasificacion.establecer(0);
        enEmpaquetado.establecer(0);
        for (int i = 0; i < paquetesClasificando.length; i++) paquetesClasificando[i] = null;
        for (int i = 0; i < paquetesEmpaquetando.length; i++) paquetesEmpaquetando[i] = null;
        for (Repartidor r : repartidores) {
            r.setCargaActual(0);
            r.setEstado(modelo.EstadoRepartidor.DISPONIBLE);
        }
        registro.registrar("Sistema reiniciado");
    }

    private void vaciarListas() {
        while (!listaRecepcion.estaVacia()) {
            try { listaRecepcion.extraerSiguiente(); } catch (InterruptedException e) { break; }
        }
        while (!listaAlmacen.estaVacia()) {
            try { listaAlmacen.extraerSiguiente(); } catch (InterruptedException e) { break; }
        }
        while (!listaEmpaquetado.estaVacia()) {
            try { listaEmpaquetado.extraerSiguiente(); } catch (InterruptedException e) { break; }
        }
        for (ListaSincronizada<Paquete> l : listasExpedicion) {
            while (!l.estaVacia()) {
                try { l.extraerSiguiente(); } catch (InterruptedException e) { break; }
            }
        }
        while (!listaEntregados.estaVacia()) {
            try { listaEntregados.extraerSiguiente(); } catch (InterruptedException e) { break; }
        }
        while (!listaDevueltos.estaVacia()) {
            try { listaDevueltos.extraerSiguiente(); } catch (InterruptedException e) { break; }
        }
    }

    private void interrumpirTodos() {
        if (hiloRecepcion != null) hiloRecepcion.interrupt();
        if (hilosClasificadores != null) for (Thread t : hilosClasificadores) t.interrupt();
        if (hilosEmpaquetadores != null) for (Thread t : hilosEmpaquetadores) t.interrupt();
        if (hilosRepartidores != null) for (Thread t : hilosRepartidores) t.interrupt();
    }

    public void esperarSiPausado() throws InterruptedException {
        synchronized (candadoPausa) {
            while (pausado && corriendo) {
                candadoPausa.wait();
            }
        }
        if (!corriendo) {
            throw new InterruptedException("Simulación detenida");
        }
    }

    public boolean isCorriendo() {
        return corriendo;
    }

    public boolean isPausado() {
        return pausado;
    }

    public ListaSincronizada<Paquete> getListaRecepcion() { return listaRecepcion; }
    public ListaSincronizada<Paquete> getListaAlmacen() { return listaAlmacen; }
    public ListaSincronizada<Paquete> getListaEmpaquetado() { return listaEmpaquetado; }
    public ListaSincronizada<Paquete> getListaEntregados() { return listaEntregados; }
    public ListaSincronizada<Paquete> getListaDevueltos() { return listaDevueltos; }

    public ListaSincronizada<Paquete> getListaExpedicion(String ruta) {
        for (int i = 0; i < rutas.length; i++) {
            if (rutas[i].equals(ruta)) {
                return listasExpedicion[i];
            }
        }
        return null;
    }

    public String[] getRutas() { return rutas; }
    public ListaSincronizada<Paquete>[] getListasExpedicionPorIndice() { return listasExpedicion; }

    public String[] getCiudades() { return ciudades; }

    public String rutaParaCiudad(String ciudad) {
        for (int i = 0; i < ciudades.length; i++) {
            if (ciudades[i].equals(ciudad)) {
                return rutaDeCadaCiudad[i];
            }
        }
        return "R01";
    }

    public Estadisticas getEstadisticas() { return estadisticas; }
    public RegistroEventos getRegistro() { return registro; }
    public Repartidor[] getRepartidores() { return repartidores; }

    public ContadorSeguro getContadorEnClasificacion() { return enClasificacion; }
    public ContadorSeguro getContadorEnEmpaquetado() { return enEmpaquetado; }

    public synchronized void setPaqueteClasificando(int indice, Paquete paquete) { paquetesClasificando[indice] = paquete; }
    public synchronized void setPaqueteEmpaquetando(int indice, Paquete paquete) { paquetesEmpaquetando[indice] = paquete; }
    public synchronized Paquete[] getPaquetesClasificando() { return paquetesClasificando.clone(); }
    public synchronized Paquete[] getPaquetesEmpaquetando() { return paquetesEmpaquetando.clone(); }

}
