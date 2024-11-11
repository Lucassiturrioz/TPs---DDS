package Controller;

import Controller.DTO.CrearContribucionDTO;
import Models.Domain.FormasDeContribucion.Utilidades.FactoryContribucion;
import Models.Domain.FormasDeContribucion.Utilidades.Contribucion;
import Models.Domain.Heladera.Heladera;
import Models.Domain.Personas.Actores.Fisico;
import Models.Repository.RepoContribucion;
import Service.Server.ICrudViewsHandler;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContribucionController extends Controller implements ICrudViewsHandler {

    private RepoContribucion repo;

    public ContribucionController(RepoContribucion repo){
        this.repo = repo;
    }


    @Override
    public void index(Context context) {
        this.estaLogueado(context);
        Map<String, Object> model = this.basicModel(context);

        model.put("esHumano", this.usuario instanceof Fisico );

        context.render("FormasDeContribucion/index.hbs", model);

    }

    @Override
    public void show(Context context) {

    }


    @Override
    public void create(Context context) {
        this.estaLogueado(context);

        String tipoContribucion = context.queryParam("tipo");

        context.render("FormasDeContribucion/"+tipoContribucion+".hbs", this.obtenerModeloContribucion(tipoContribucion,context));

    }


    @Override
    public void save(Context context) {
        this.estaLogueado(context);

        Map<String, String> singleValueParams = context.formParamMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get(0)
                ));

        CrearContribucionDTO dto = new CrearContribucionDTO(context.formParam("tipo"), singleValueParams );
        FactoryContribucion.getInstance().factoryMethod( context.sessionAttribute("idPersona") , dto );

        context.render("FormasDeContribucion/contribucionExitosa.hbs", this.basicModel(context));
    }

    @Override
    public void edit(Context context) {

    }

    @Override
    public void update(Context context) {

    }

    public void consultarContribuciones(Context context){
        this.estaLogueado(context);
        Map<String, Object> model = this.basicModel(context);

        List<Contribucion> contribuciones = repo.queryContribucion( context.sessionAttribute("idPersona") );

        model.put("contribuciones",contribuciones);

        context.render("FormasDeContribucion/misContribuciones.hbs",model);
    }



    private Map<String, Object> obtenerModeloContribucion(String tipoContribucion, Context context) {
        Map<String,Object> model = this.basicModel(context);
        switch (tipoContribucion) {
            case "donarViandas" -> {
                model.put("heladeras", repo.queryHeladeraDisponible());
                return model;
            }
            case "distribucionViandas" -> {
                List<Heladera> heladeraList = repo.queryHeladera();

                List<Heladera> heladerasConAlimentos = new ArrayList<>();
                List<Heladera> heladerasDisponibles = new ArrayList<>();

                for (Heladera heladera : heladeraList) {
                    if (!heladera.getViandas().isEmpty()) {
                        heladerasConAlimentos.add(heladera);
                    }
                    if (!heladera.getEstaLlena()) {
                        heladerasDisponibles.add(heladera);
                    }
                }

                model.put("heladeras", heladerasConAlimentos);
                model.put("heladerasDisponible", heladerasDisponibles);

                return model;
            }
            case "hacerseCargoHeladera" -> {
                model.put("heladeras", repo.queryHeladeraDuenia());
                return model;
            }
        }


        return model;

    }


}
