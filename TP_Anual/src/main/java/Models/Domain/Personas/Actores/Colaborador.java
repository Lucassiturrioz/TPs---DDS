package Models.Domain.Personas.Actores;

import Models.Domain.Excepciones.NoTienePuntosCanjeException;
import Models.Domain.Producto.Canje;
import Models.Domain.FormasDeContribucion.Utilidades.Contribucion;
import Models.Domain.FormasDeContribucion.ContribucionesJuridicas.OfrecerProducto;
import Models.Domain.Tarjetas.TarjetaAccesos;
import Service.Notificacion.Mensaje.MensajeCanje;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

@Entity
@DiscriminatorValue("Colaborador")
@NoArgsConstructor
public class Colaborador extends Rol {

    @OneToMany(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "id_colaborador") // CLAVE FORANEA
    private List<Contribucion> contribuciones;

    @OneToMany(mappedBy = "colaborador", cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private List<Canje> historialCanje;

    @Column(name = "puntaje")
    private Double puntaje;

    @OneToOne(cascade = CascadeType.PERSIST)
    private TarjetaAccesos tarjeta; // Persistencia se elimina, para evitar bidireccionaldiad

    @Column(name = "cantidad_viandas_donadas")
    private int cantidadViandasDonadas;

    public Colaborador(Double puntaje){
        this.tipo = TipoRol.COLABORADOR;
        this.puntaje = puntaje;
        this.contribuciones = new ArrayList<>();
        this.historialCanje = new ArrayList<>();
    }


    public void agregarNuevaDonacion(Contribucion unaDonacion){
        this.contribuciones.add(unaDonacion);
        this.sumarPuntaje(unaDonacion);

    }

    private void sumarPuntaje(Contribucion unaDonacion){
        this.puntaje += unaDonacion.generarPuntaje();
    }


    public void realizarCanje(OfrecerProducto producto, Integer cantidad){
        if (!(puntaje >= producto.getPuntosNecesarios()*cantidad && cantidad <=producto.getStock())){
            throw new NoTienePuntosCanjeException("No tiene suficiente Puntos");
        }
        puntaje -= producto.getPuntosNecesarios() * cantidad;
        producto.setStock(producto.getStock() - cantidad);
        Canje canje = new Canje(producto, this, cantidad);

        historialCanje.add(canje);

    }

    public void reestablecerViandas(){this.cantidadViandasDonadas = 0;}


    public List<String> getPermisos(Persona persona) {
        List<String> permisos = new ArrayList<>(List.of("donarDinero"));
        if (persona instanceof Fisico) {
            permisos.addAll(List.of("donarViandas", "registrarPersonaVulnerable", "distribucionViandas"));
        } else {
            permisos.addAll(List.of("hacerseCargoHeladera", "ofrecerProducto"));
        }
        return permisos;
    }



}
