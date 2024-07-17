package Models.Domain.Heladera.Sensores;

import Models.Domain.Heladera.Heladera;
import Models.Domain.Incidentes.Alerta;
import Models.Domain.Incidentes.TipoAlerta;
import Service.TareaDiferida.AdapterChromeTask;

import Service.TareaDiferida.ChromeTask;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SensorTemperatura implements Sensor {
    private Heladera heladera;
    private AdapterChromeTask tareaProgramada;



    public SensorTemperatura(Heladera heladera) {
        this.heladera = heladera;
        this.tareaProgramada = new ChromeTask();
    }

    public void activar(){
        this.tareaProgramada.ejecutarTareaPrograma(5,this,"chequear");
    }

    public void desactivar(){
        this.tareaProgramada.pausarTarea();
    }

    public void chequear(){
       if ( this.superaTemperaturaMax() || this.superaTemperaturaMin() )
       {
           this.notificar();
           this.tareaProgramada.pausarTarea();
       }
    }




    public boolean superaTemperaturaMax(){
        return heladera.getTemperaturaActual() > heladera.getTemperaturaMax();
    }

    public boolean superaTemperaturaMin(){
        return heladera.getTemperaturaActual() < heladera.getTemperaturaMin();
    }

    public void notificar(){
        //System.out.println(" TEMPERATURA EN PELIGRO DANGER D:");
        var incidente = new Alerta(TipoAlerta.TEMPERATURA);
        incidente.notificar();
    }


}
