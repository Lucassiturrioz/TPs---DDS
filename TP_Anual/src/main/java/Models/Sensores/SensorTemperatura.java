package Models.Sensores;

import Models.Heladera;
import Models.Sensores.TareaDiferida.AdapterChromeTask;
import Models.Sensores.TareaDiferida.ChromeTask;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Getter
@Setter
public class SensorTemperatura implements Sensor {
    private Heladera heladera;
    private AdapterChromeTask tareaProgramada;
    public SensorTemperatura(Heladera heladera) {
        this.heladera = heladera;
        this.tareaProgramada = new ChromeTask(5,this,"chequear");
    }


    public void chequear(){
       if ( this.superaTemperaturaMax() || this.superaTemperaturaMin() )
       {
           this.notificar();
       }
    }

    public boolean superaTemperaturaMax(){
        return heladera.getTemperaturaActual() > heladera.getTemperaturaMax();
    }

    public boolean superaTemperaturaMin(){
        return heladera.getTemperaturaActual() < heladera.getTemperaturaMin();
    }

    public void notificar(){
        System.out.println(" TEMPERATURA EN PELIGRO DANGER D:");
    }


}
