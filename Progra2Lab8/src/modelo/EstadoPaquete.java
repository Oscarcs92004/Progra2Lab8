package modelo;

public enum EstadoPaquete {
    RECIBIDO,
    ALMACENADO,
    CLASIFICANDO,
    CLASIFICADO,
    EMPAQUETANDO,
    EMPAQUETADO,
    EN_EXPEDICION,
    CARGANDO,
    EN_REPARTO,
    NUEVO_INTENTO,
    ENTREGADO,
    DEVUELTO;

    public boolean puedeTransicionarA(EstadoPaquete destino) {
        switch (this) {
            case RECIBIDO:
                return destino == ALMACENADO;
            case ALMACENADO:
                return destino == CLASIFICANDO;
            case CLASIFICANDO:
                return destino == CLASIFICADO;
            case CLASIFICADO:
                return destino == EMPAQUETANDO;
            case EMPAQUETANDO:
                return destino == EMPAQUETADO;
            case EMPAQUETADO:
                return destino == EN_EXPEDICION;
            case EN_EXPEDICION:
                return destino == CARGANDO;
            case CARGANDO:
                return destino == EN_REPARTO;
            case EN_REPARTO:
                return destino == ENTREGADO || destino == NUEVO_INTENTO;
            case NUEVO_INTENTO:
                return destino == CARGANDO || destino == DEVUELTO;
            case ENTREGADO:
            case DEVUELTO:
                return false;
            default:
                return false;
        }
    }
}
