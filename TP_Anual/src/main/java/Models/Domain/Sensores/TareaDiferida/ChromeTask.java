package Models.Domain.Sensores.TareaDiferida;

public class ChromeTask implements AdapterChromeTask{
    private  AdapterScheduledExecutorService adapterChromeTask;

    public ChromeTask(){
        this.adapterChromeTask = new AdapterScheduledExecutorService();

    }

    public void ejecutarTareaPrograma(int periodo, Object objeto, String metodo){
        adapterChromeTask.ejecutarTareaPrograma(periodo,objeto,metodo);
    }
    public void pausarTarea(){
        adapterChromeTask.pausarTareaProgramada();
    }


}
