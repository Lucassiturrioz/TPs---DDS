package Models.Domain.FormasDeContribucion.Utilidades;

import Models.Domain.Builder.ContribucionBuilder.*;
import Models.Domain.Builder.TarjetaBuilder;
import Models.Domain.Builder.UsuariosBuilder.VulnerableBuilder;
import Models.Domain.Excepciones.Permisos;
import Models.Domain.FormasDeContribucion.ContribucionesHumana.EntregaDeTarjeta;
import Models.Domain.FormasDeContribucion.ContribucionesHumana.Utilidades.TipoFrecuencia;
import Models.Domain.Heladera.Heladera;
import Models.Domain.Heladera.Vianda;
import Models.Domain.Personas.Actores.Colaborador;
import Models.Domain.Personas.Actores.Humano;
import Models.Domain.Personas.Actores.PersonaVulnerable;
import Models.Domain.Personas.Utilidades.TipoRolNegocio;
import Models.Domain.Producto.Producto;
import Models.Domain.Tarjetas.TarjetaPersonaVulnerable;
import lombok.Getter;


@Getter
public class FactoryContribucion {
    FormaDeContribucion nuevaDonacion;


    Permisos permisos;
    Colaborador colaborador;
    public FactoryContribucion(Colaborador colaborador) {
       this.colaborador = colaborador;
       this.permisos = new Permisos(colaborador);
    }

    // ------------------- LO HACEN TODOS ----------------------------------------//
    private FormaDeContribucion donacionDeDinero(Object ... Context){

        Double monto = (Double) Context[1];
        TipoFrecuencia tipoFrecuencia = (TipoFrecuencia) Context[2];

        DonacionDeDineroBuilder builder = new DonacionDeDineroBuilder();

        FormaDeContribucion donacion = builder
                .monto(monto)
                .frecuencia(tipoFrecuencia)
                .construir();

        this.colaborador.agregarNuevaDonacion(donacion);

        return  donacion;
    }


    private FormaDeContribucion donacionDeVianda(Object ... Context){

        permisos.checkUserRoleAndProceed(TipoRolNegocio.HUMANO);

        Vianda vianda = (Vianda) Context[1];
        Heladera heladera = (Heladera) Context[2];

        heladera.agregarVianda(vianda);

        DonacionDeViandaBuilder builder = new DonacionDeViandaBuilder();

        FormaDeContribucion donacion = builder
                .heladera(heladera)
                .vianda(vianda)
                .construir();


        this.colaborador.agregarNuevaDonacion(donacion);

        return donacion;

    }

    private FormaDeContribucion distribucionDeVianda(Object ... Context){

        permisos.checkUserRoleAndProceed(TipoRolNegocio.HUMANO);

        Heladera heladeraOrigen = (Heladera) Context[1];
        Heladera heladeraDestino = (Heladera) Context[2];
        Integer cantidad = (Integer) Context[3];
        String motivo = (String) Context[4];

        // Arreglar



        DistribucionDeViandasBuilder builder = new DistribucionDeViandasBuilder();

        FormaDeContribucion donacion = builder
                .heladeraOrigen(heladeraOrigen)
                .heladeraDestino(heladeraDestino)
                .cantidadDeViandasAMover(cantidad)
                .motivos(motivo)
                .construir();

        this.colaborador.agregarNuevaDonacion(donacion);

        return donacion;
    }

    private FormaDeContribucion registrarTarjeta(Object ... Context){

        permisos.checkUserRoleAndProceed(TipoRolNegocio.HUMANO);

        String nombre = (String) Context[1];
        Integer menoresACargo = (Integer) Context[2];

        VulnerableBuilder vulnerableBuilder = new VulnerableBuilder();
        TarjetaBuilder tarjetaBuilder = new TarjetaBuilder();

        PersonaVulnerable persona =
                vulnerableBuilder
                        .nombre(nombre)
                        .menoresACargo(menoresACargo)
                        .construir();

    /*
        TarjetaPersonaVulnerable nuevaTarjetaPersonaVulnerable =
                tarjetaBuilder
                .titular(persona)
                        .construir();
    */
        FormaDeContribucion donacion = new EntregaDeTarjeta();

        this.colaborador.agregarNuevaDonacion(donacion);

        return donacion;
    }



    private FormaDeContribucion hacerceCargoDeHeladera(Object ... Context){

        permisos.checkUserRoleAndProceed( TipoRolNegocio.JURIDICO );

        String nombreCaracteristico = (String) Context[1];
        Heladera heladera = (Heladera) Context[3];

        HacerseCargoDeHeladeraBuilder builder = new HacerseCargoDeHeladeraBuilder();

        FormaDeContribucion donacion = builder
                .nombreCaracteristico(nombreCaracteristico)
                .heladera(heladera)
                .construir();

        this.colaborador.agregarNuevaDonacion(donacion);

        return donacion;
    }

    private  FormaDeContribucion ofrecerProducto(Object ... Context){

        permisos.checkUserRoleAndProceed(TipoRolNegocio.JURIDICO);

        Producto producto = (Producto) Context[1];
        Double puntosNecesarios = (Double) Context[2];
        Integer stock = (Integer) Context[3];


        OfrecerProductoBuilder builder = new OfrecerProductoBuilder();

        FormaDeContribucion donacion = builder
                .producto(producto)
                .stock(stock)
                .puntosNecesarios(puntosNecesarios)
                .construir();

        this.colaborador.agregarNuevaDonacion(donacion);

        return donacion;
    }


    public FormaDeContribucion factoryMethod(Object ... Context){
        FormaDeContribucion contribucion = null;
        switch( (TipoDonacion) Context[0] ){
            case DONACION_DINERO: contribucion = this.donacionDeDinero( Context ); break;
            case DONACION_DE_VIANDA: contribucion = this.donacionDeVianda( Context ); break;
            case HACERSE_CARGO_DE_HELADERA: contribucion = this.hacerceCargoDeHeladera( Context ); break;
            case DISTRIBUCION_VIANDAS: contribucion = this.distribucionDeVianda( Context ); break;
            case ENTREGA_TARJETAS : contribucion = this.registrarTarjeta( Context ); break;
            case OFRECER_PRODUCTO: contribucion = this.ofrecerProducto( Context ); break;
        }
        return contribucion;
    }


}