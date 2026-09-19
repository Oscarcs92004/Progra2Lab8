package modelo;

public enum Prioridad {
    BAJA(0, "🟢 BAJA"),
    NORMAL(1, "🟡 NORMAL"),
    ALTA(2, "🟠 ALTA"),
    URGENTE(3, "🔴 URGENTE");

    private final int nivel;
    private final String etiqueta;

    Prioridad(int nivel, String etiqueta) {
        this.nivel = nivel;
        this.etiqueta = etiqueta;
    }

    public int getNivel() {
        return nivel;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static Prioridad aleatoria(java.util.Random rnd) {
        Prioridad[] valores = values();
        int r = rnd.nextInt(100);
        if (r < 45) return NORMAL;
        if (r < 75) return ALTA;
        if (r < 90) return BAJA;
        return URGENTE;
    }
}
