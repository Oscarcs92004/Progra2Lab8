package gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import modelo.*;
import sistema.CentroLogistico;

public class MainFrame extends JFrame {
    private final CentroLogistico centro = new CentroLogistico();
    private JTextArea areaRecepcion, areaAlmacen, areaClasificacion, areaEmpaquetado;
    private JPanel panelExpedicion;
    private JPanel panelRepartidores;
    private JTextArea areaLog;
    private JLabel lblCapAlmacen, lblCapEmpaquetado;
    private JProgressBar barraAlmacen, barraEmpaquetado;
    private JLabel lblGenerados, lblEntregados, lblDevueltos, lblEnProceso, lblTiempoPromedio;
    private JButton btnIniciar, btnPausar, btnReanudar, btnDetener, btnReiniciar;

    public MainFrame() {
        super("📦 Centro Logístico de Paquetería");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(6, 6));
        add(construirBarraControles(), BorderLayout.NORTH);
        add(construirCentro(), BorderLayout.CENTER);
        add(construirPanelLog(), BorderLayout.SOUTH);
        Timer refresco = new Timer(350, e -> refrescar());
        refresco.start();
    }

    private JComponent construirBarraControles() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        btnIniciar = new JButton("▶ INICIAR");
        btnPausar = new JButton("⏸ PAUSAR");
        btnReanudar = new JButton("⏵ REANUDAR");
        btnDetener = new JButton("⏹ DETENER");
        btnReiniciar = new JButton("⟳ REINICIAR");
        JButton btnStats = new JButton("📊 ESTADÍSTICAS");

        btnIniciar.addActionListener(e -> { centro.iniciar(); actualizarBotones(); });
        btnPausar.addActionListener(e -> { centro.pausar(); actualizarBotones(); });
        btnReanudar.addActionListener(e -> { centro.reanudar(); actualizarBotones(); });
        btnDetener.addActionListener(e -> { centro.detener(); actualizarBotones(); });
        btnReiniciar.addActionListener(e -> { centro.reiniciar(); actualizarBotones(); });
        btnStats.addActionListener(e -> mostrarDialogoEstadisticas());

        panel.add(btnIniciar);
        panel.add(btnPausar);
        panel.add(btnReanudar);
        panel.add(btnDetener);
        panel.add(btnReiniciar);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnStats);

        actualizarBotones();
        return panel;
    }

    private JComponent construirCentro() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));

        JPanel fila1 = new JPanel(new GridLayout(1, 3, 6, 6));
        areaRecepcion = crearAreaTexto();
        areaAlmacen = crearAreaTexto();
        areaClasificacion = crearAreaTexto();
        areaEmpaquetado = crearAreaTexto();

        fila1.add(envolverConTitulo(new JScrollPane(areaRecepcion), "RECEPCIÓN"));
        fila1.add(envolverPanelAlmacen());
        fila1.add(envolverConTitulo(new JScrollPane(areaClasificacion), "CLASIFICACIÓN"));
        fila1.setPreferredSize(new Dimension(100, 220));
        contenedor.add(fila1);

        JPanel fila2 = new JPanel(new BorderLayout());
        JPanel cabeceraEmp = new JPanel(new BorderLayout());
        barraEmpaquetado = new JProgressBar(0, CentroLogistico.CAP_EMPAQUETADO);
        lblCapEmpaquetado = new JLabel("0 / " + CentroLogistico.CAP_EMPAQUETADO);
        cabeceraEmp.add(barraEmpaquetado, BorderLayout.CENTER);
        cabeceraEmp.add(lblCapEmpaquetado, BorderLayout.EAST);
        fila2.add(cabeceraEmp, BorderLayout.NORTH);
        fila2.add(new JScrollPane(areaEmpaquetado), BorderLayout.CENTER);
        fila2.setBorder(tituloBorde("EMPAQUETADO"));
        fila2.setPreferredSize(new Dimension(100, 130));
        contenedor.add(fila2);

        panelExpedicion = new JPanel(new GridLayout(1, 4, 6, 6));
        JPanel wrapExp = new JPanel(new BorderLayout());
        wrapExp.add(panelExpedicion, BorderLayout.CENTER);
        wrapExp.setBorder(tituloBorde("EXPEDICIÓN"));
        wrapExp.setPreferredSize(new Dimension(100, 170));
        contenedor.add(wrapExp);

        panelRepartidores = new JPanel(new GridLayout(1, centro.getRepartidores().length, 6, 6));
        JPanel wrapRep = new JPanel(new BorderLayout());
        wrapRep.add(panelRepartidores, BorderLayout.CENTER);
        wrapRep.setBorder(tituloBorde("REPARTIDORES"));
        wrapRep.setPreferredSize(new Dimension(100, 140));
        contenedor.add(wrapRep);

        return contenedor;
    }

    private JComponent envolverPanelAlmacen() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel cabecera = new JPanel(new BorderLayout());
        barraAlmacen = new JProgressBar(0, CentroLogistico.CAP_ALMACEN);
        lblCapAlmacen = new JLabel("0 / " + CentroLogistico.CAP_ALMACEN);
        cabecera.add(barraAlmacen, BorderLayout.CENTER);
        cabecera.add(lblCapAlmacen, BorderLayout.EAST);
        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaAlmacen), BorderLayout.CENTER);
        panel.setBorder(tituloBorde("ALMACÉN"));
        return panel;
    }

    private JComponent construirPanelLog() {
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaLog.setBackground(new Color(20, 20, 20));
        areaLog.setForeground(new Color(0, 230, 120));
        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setPreferredSize(new Dimension(100, 190));
        scroll.setBorder(tituloBorde("REGISTRO DEL SISTEMA"));
        return scroll;
    }

    private JTextArea crearAreaTexto() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        return area;
    }

    private JComponent envolverConTitulo(JComponent comp, String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(comp, BorderLayout.CENTER);
        panel.setBorder(tituloBorde(titulo));
        return panel;
    }

    private TitledBorder tituloBorde(String texto) {
        TitledBorder b = BorderFactory.createTitledBorder(texto);
        b.setTitleFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        return b;
    }

    private void refrescar() {
        actualizarBotones();

        actualizarArea(areaRecepcion, centro.getListaRecepcion());
        actualizarArea(areaAlmacen, centro.getListaAlmacen());
        actualizarAreaClasificacion();
        actualizarAreaEmpaquetado();

        barraAlmacen.setValue(centro.getListaAlmacen().tamano());
        lblCapAlmacen.setText(centro.getListaAlmacen().tamano() + " / " + CentroLogistico.CAP_ALMACEN);
        barraEmpaquetado.setValue(centro.getListaEmpaquetado().tamano());
        lblCapEmpaquetado.setText(centro.getListaEmpaquetado().tamano() + " / " + CentroLogistico.CAP_EMPAQUETADO);

        actualizarExpedicion();
        actualizarRepartidores();
        actualizarLog();
    }

    private void actualizarArea(JTextArea area, paqueteria.estructuras.ListaSincronizada<Paquete> lista) {
        StringBuilder sb = new StringBuilder();
        lista.recorrer(p -> sb.append(p.getCodigo()).append("  ").append(p.getPrioridad().getEtiqueta()).append('\n'));
        String texto = sb.toString();
        if (!texto.equals(area.getText())) {
            area.setText(texto);
        }
    }


    private void actualizarAreaEmpaquetado() {
        StringBuilder sb = new StringBuilder("En cola:\n");
        centro.getListaEmpaquetado().recorrer(p -> sb.append(p.toString()).append("\n"));
        sb.append("\nTrabajando:\n");
        Paquete[] activos = centro.getPaquetesEmpaquetando();
        for (int i = 0; i < activos.length; i++) {
            sb.append("Empaquetador ").append(i + 1).append(": ");
            sb.append(activos[i] == null ? "Disponible" : activos[i].toString());
            sb.append("\n");
        }
        String texto = sb.toString();
        if (!texto.equals(areaEmpaquetado.getText())) areaEmpaquetado.setText(texto);
    }

    private void actualizarAreaClasificacion() {
        StringBuilder sb = new StringBuilder("En proceso: ").append(centro.getContadorEnClasificacion().obtenerComoEntero()).append("\n\n");
        Paquete[] activos = centro.getPaquetesClasificando();
        for (int i = 0; i < activos.length; i++) {
            sb.append("Clasificador ").append(i + 1).append(": ");
            sb.append(activos[i] == null ? "Disponible" : activos[i].toString());
            sb.append("\n");
        }
        String texto = sb.toString();
        if (!texto.equals(areaClasificacion.getText())) {
            areaClasificacion.setText(texto);
        }
    }

    private void actualizarExpedicion() {
        panelExpedicion.removeAll();
        String[] rutas = centro.getRutas();
        paqueteria.estructuras.ListaSincronizada<Paquete>[] listas = centro.getListasExpedicionPorIndice();
        for (int i = 0; i < rutas.length; i++) {
            JTextArea area = crearAreaTexto();
            StringBuilder sb = new StringBuilder();
            paqueteria.estructuras.ListaSincronizada<Paquete> lista = listas[i];
            lista.recorrer(p -> sb.append(p.getCodigo()).append('\n'));
            area.setText(sb.toString());
            String titulo = "Ruta " + rutas[i].substring(1) + "  ("
                    + lista.tamano() + "/" + lista.capacidad() + ")";
            panelExpedicion.add(envolverConTitulo(new JScrollPane(area), titulo));
        }
        panelExpedicion.revalidate();
        panelExpedicion.repaint();
    }

    private void actualizarRepartidores() {
        panelRepartidores.removeAll();
        for (Repartidor r : centro.getRepartidores()) {
            JPanel tarjeta = new JPanel();
            tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
            tarjeta.setBorder(BorderFactory.createEtchedBorder());
            JLabel lblNombre = new JLabel("🚚 " + r.getNombre());
            lblNombre.setFont(lblNombre.getFont().deriveFont(Font.BOLD));
            JLabel lblRuta = new JLabel("Ruta: " + r.getRutaAsignada());
            JLabel lblEstado = new JLabel("Estado: " + r.getEstado());
            JLabel lblCarga = new JLabel("Carga: " + r.getCargaActual() + "/" + r.getCapacidadMaxima()
                    + (r.estaLleno() ? "  [LLENO]" : ""));
            JLabel lblEntregados = new JLabel("Entregados: " + r.getPaquetesEntregados());
            for (JLabel l : new JLabel[]{lblNombre, lblRuta, lblEstado, lblCarga, lblEntregados}) {
                l.setAlignmentX(Component.CENTER_ALIGNMENT);
                tarjeta.add(l);
            }
            panelRepartidores.add(tarjeta);
        }
        panelRepartidores.revalidate();
        panelRepartidores.repaint();
    }

    private void actualizarLog() {
        String[] lineas = centro.getRegistro().snapshot();
        StringBuilder sb = new StringBuilder();
        int inicio = Math.max(0, lineas.length - 200);
        for (int i = inicio; i < lineas.length; i++) {
            sb.append(lineas[i]).append('\n');
        }
        String texto = sb.toString();
        if (!texto.equals(areaLog.getText())) {
            areaLog.setText(texto);
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        }
    }

    private void actualizarBotones() {
        boolean corriendo = centro.isCorriendo();
        boolean pausado = centro.isPausado();
        btnIniciar.setEnabled(!corriendo);
        btnPausar.setEnabled(corriendo && !pausado);
        btnReanudar.setEnabled(corriendo && pausado);
        btnDetener.setEnabled(corriendo);
        btnReiniciar.setEnabled(!corriendo);
    }

    private void mostrarDialogoEstadisticas() {
        JDialog dialogo = new JDialog(this, "Estadísticas", false);
        dialogo.setSize(320, 260);
        dialogo.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblGenerados = new JLabel();
        lblEntregados = new JLabel();
        lblDevueltos = new JLabel();
        lblEnProceso = new JLabel();
        lblTiempoPromedio = new JLabel();

        panel.add(lblGenerados);
        panel.add(lblEntregados);
        panel.add(lblDevueltos);
        panel.add(lblEnProceso);
        panel.add(lblTiempoPromedio);
        for (Repartidor r : centro.getRepartidores()) {
            panel.add(new JLabel(r.getNombre() + ": " + r.getPaquetesEntregados() + " entregados"));
        }

        Timer t = new Timer(500, e -> actualizarLabelsEstadisticas());
        t.start();
        actualizarLabelsEstadisticas();

        dialogo.add(panel);
        dialogo.setVisible(true);
    }

    private void actualizarLabelsEstadisticas() {
        if (lblGenerados == null) return;
        sistema.Estadisticas stats = centro.getEstadisticas();
        lblGenerados.setText("Paquetes generados: " + stats.getGenerados());
        lblEntregados.setText("Entregados: " + stats.getEntregados());
        lblDevueltos.setText("Devueltos: " + stats.getDevueltos());
        lblEnProceso.setText("En proceso: " + stats.getEnProceso());
        lblTiempoPromedio.setText(String.format("Tiempo promedio: %.1f s", stats.getTiempoPromedioSegundos()));
    }
}
