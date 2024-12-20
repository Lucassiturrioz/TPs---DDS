package Controller;

import Models.Domain.Builder.UsuariosBuilder.FisicoBuilder;
import Models.Domain.Tarjetas.TarjetaAccesos;
import Models.Repository.RepoPersona;
import Service.Notificacion.Mensaje.MensajeBienvenida;
import Service.Observabilidad.MetricsRegistry;
import Service.SSO.AdapterGoogleSSO;
import Service.SSO.GoogleAdaptado;
import Models.Domain.Personas.Actores.Colaborador;
import Models.Domain.Personas.Actores.Persona;
import Service.Validador.CredencialDeAcceso;
import Service.Validador.Encriptador;
import com.fasterxml.jackson.databind.JsonNode;
import io.javalin.http.Context;
import io.micrometer.core.instrument.MeterRegistry;

public class LoginSSOController extends Controller {
    private final RepoPersona repo;
    private final AdapterGoogleSSO ssoService = new AdapterGoogleSSO(
                new GoogleAdaptado(
                        System.getenv("GOOGLE_CLIENT_ID"), // Obtiene el Client ID desde las variables de entorno
                        System.getenv("GOOGLE_CLIENT_SECRET"), // Obtiene el Client Secret desde las variables de entorno
                        System.getenv("GOOGLE_CALLBACK_URL") // Obtiene el Callback URL desde las variables de entorno
                )
    );
    public LoginSSOController(RepoPersona repoColaborador) {
        this.repo = repoColaborador;
    }

    public void redirectToSSO(Context context) {
        String authUrl = ssoService.verAutorizacion();
        context.redirect(authUrl);
    }

    public void handleCallback(Context context) {
        String code = context.queryParam("code");
        String accessToken = ssoService.generarToken(code);

        if (accessToken != null) {
            JsonNode userInfo = ssoService.infoUsuario(accessToken);

            Persona persona = repo.existeUsuarioSso( userInfo.get("email").asText());
            if( persona == null) {
                 persona = crearPersonaDesdeSSO(userInfo);
            }

            context.sessionAttribute("usuario", persona);
            context.sessionAttribute("idPersona", Integer.toString(persona.getId()));
            context.sessionAttribute("rolTipo", persona.getTipoUsuario().toString());

            //Incremento la metrica
            MeterRegistry registry = MetricsRegistry.getInstance().getRegistry();
            registry.counter("dds.iniciosDeSesion").increment();

            context.redirect("/index/" + persona.getTipoUsuario().toString().toLowerCase());
        } else {
            context.status(500).result("Error al obtener el token.");
        }
    }

    private Persona crearPersonaDesdeSSO(JsonNode userInfo) {

        String nombre = userInfo.get("given_name").asText();
        String correo = userInfo.get("email").asText();
        String apellido = userInfo.get("family_name").asText();

        CredencialDeAcceso credencialDeAcceso = new CredencialDeAcceso(correo, Encriptador.getInstancia().encriptarMD5("1"));

        FisicoBuilder builder = new FisicoBuilder();
        Persona persona = builder
                .nombre(nombre)
                .apellido(apellido)
                .correoElectronico(correo)
                .credencialDeAcceso(credencialDeAcceso)
                .construir();

        Colaborador colaborador = new Colaborador(0.0);
        TarjetaAccesos tarjetaAccesos = new TarjetaAccesos(persona);
        colaborador.setTarjeta(tarjetaAccesos);

        persona.agregarRol(colaborador);

        repo.agregar(persona);

        new MensajeBienvenida(persona.getCorreElectronico(), String.valueOf(tarjetaAccesos.getCodigo()) );


        //Incremento la metrica
        MeterRegistry registry = MetricsRegistry.getInstance().getRegistry();
        registry.counter("dds.usuariosFisicosCreados").increment();

        return persona;
    }

}
