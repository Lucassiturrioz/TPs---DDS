package Models.Domain.Personas.Actores;

import Service.APIPuntos.AreaCobertura;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Tecnico extends Rol {
    private String cuil;
    private AreaCobertura area;

    public Tecnico(){
        this.tipo = TipoRol.TECNICO;
    }

}
