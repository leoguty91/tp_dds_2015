package ar.edu.utn.d2s.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import ar.edu.utn.d2s.Grupo;
import ar.edu.utn.d2s.HibernateUtil;
import ar.edu.utn.d2s.Mes;
import ar.edu.utn.d2s.Receta;
import ar.edu.utn.d2s.RepositorioGrupos;
import ar.edu.utn.d2s.RepositorioRecetas;
import ar.edu.utn.d2s.RepositorioUsuarios;
import ar.edu.utn.d2s.Semana;
import ar.edu.utn.d2s.Temporada;
import ar.edu.utn.d2s.Usuario;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioInexistente;

@Path("/estadisticas")
public class Estadistica {

	@GET
	@Produces("application/json")
	public Response getEstadistica(@QueryParam("usuario") String usrID, @QueryParam("recetasMasCopiadas") String rmc, @QueryParam("mes") String mes, @QueryParam("semana") String semana, @QueryParam("temporada") String temporada) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Usuario usuario;
		if (usrID != null) {
			try {
				usuario = RepositorioUsuarios.getInstance().getUsuario(session, usrID);
			} catch (ExceptionUsuarioInexistente e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
			List<Grupo> grupos = RepositorioGrupos.getInstance().getGrupos(session);
			List<Usuario> usuarios = RepositorioUsuarios.getInstance().getUsuarios(session);
			List<Receta> recetas = RepositorioRecetas.getInstance().getRecetas(session);
			usuario.configurarRepositorios(grupos, usuarios, recetas);
			session.close();
			if(rmc != null) {
				return getRecetasMasCopiadas(usuario);
			} else if (mes != null && semana != null && temporada != null){
				return getEstPorEstacionYSemana(usuario, mes, semana, temporada);
			} else if (mes != null && temporada != null) {
				return getEstPorEstacionYMes(usuario, mes, temporada);
			}
		}

		return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
	}

	private Response getEstPorEstacionYSemana(Usuario usuario, String mes, String semana, String temporada) {
		Map<Receta,Long> map = usuario.getEstadisticasPorEstacionYSemana(Mes.valueOf(mes), Semana.valueOf(semana), Temporada.valueOf(temporada));
		String resultado = armaEstadisticasJson(map);
		return Response.ok(resultado).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	private Response getEstPorEstacionYMes(Usuario usuario, String mes, String temporada) {
		Map<Receta,Long> map = usuario.getEstadisticasPorEstacionYMes(Mes.valueOf(mes), Temporada.valueOf(temporada));
		String resultado = armaEstadisticasJson(map);
		return Response.ok(resultado).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	private Response getRecetasMasCopiadas(Usuario usuario) {
		Map<Receta,Long> map = usuario.getEstadisticasPorRecetasMasCopiadas();
		String resultado = armaEstadisticasJson(map);
		return Response.ok(resultado).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	private JSONObject recetaToJsonObject(Receta r, Long puntaje) {
		JSONObject recetaJSON = new JSONObject();
		recetaJSON.put("id", r.getId());
		recetaJSON.put("nombre", r.getNombre());
		recetaJSON.put("puntaje", puntaje);
		return recetaJSON;
	}

	private String armaEstadisticasJson(Map<Receta, Long> map) {
		JSONArray jsonArray = new JSONArray();
		for(Receta r : map.keySet()){
			jsonArray.put(this.recetaToJsonObject(r, map.get(r)));
		}
		return jsonArray.toString();
	}
}