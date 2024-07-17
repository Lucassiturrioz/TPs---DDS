package Models.Domain.Heladera.Sensores;
import Models.Domain.Heladera.Heladera;
import Models.Domain.Incidentes.Alerta;
import Models.Domain.Incidentes.TipoAlerta;

public class SensorMovimiento implements Sensor {
    private Heladera heladera;
    private Boolean estaPrendido;

    public SensorMovimiento(Heladera heladera) {
        this.heladera = heladera;
        this.estaPrendido = false;
    }

    public void chequear(){
        if(this.estaCerradaHeladera() && this.estaPrendido){
            this.notificar();
        }
    }

    @Override
    public void activar() {
        this.estaPrendido = true;
    }

    @Override
    public void desactivar() {
        this.estaPrendido = false;
    }

    public void notificar(){
        //System.out.println("Estan robando !!! D:");
        var incidente = new Alerta(TipoAlerta.FRAUDE);
        incidente.notificar();
    }


    public boolean estaCerradaHeladera(){
        return !this.heladera.getAbierto();
    }

}

