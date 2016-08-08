package ar.edu.utn.d2s.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;

import ar.edu.utn.d2s.Grupo;
import ar.edu.utn.d2s.HibernateUtil;
import ar.edu.utn.d2s.Receta;
import ar.edu.utn.d2s.RecomendacionBalanceo;
import ar.edu.utn.d2s.RecomendacionMejorPuntaje;
import ar.edu.utn.d2s.RepositorioGrupos;
import ar.edu.utn.d2s.RepositorioUsuarios;
import ar.edu.utn.d2s.TipoComida;
import ar.edu.utn.d2s.Usuario;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioInexistente;

@Path("/recomendacion")
public class Recomendacion {

	@GET
	@Produces("application/json")
	public Response obtenerRecomendacion(@QueryParam("usuario") String usuario, @QueryParam("tipo") String tipo, @QueryParam("tipoComida") String tipoComida) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Usuario u = null;
		try {
			u = RepositorioUsuarios.getInstance().getUsuario(session, usuario);
		} catch (ExceptionUsuarioInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Criteria recetaCriteria = session.createCriteria(Receta.class);
		recetaCriteria.add(Restrictions.eq("visibilidad", true));
		@SuppressWarnings("unchecked")
		List<Receta> recetas = new ArrayList<Receta>(u.listarTodasLasRecetas(recetaCriteria.list()));
		List<Grupo> grupos = RepositorioGrupos.getInstance().getGrupos(session);
		session.close();
		if (tipo.equals("puntaje")) {
			return obtenerRecomendacionPuntaje(u, tipoComida, recetas, grupos);
		} else if (tipo.equals("piramide")) {
			return obtenerRecomendacionPiramide(u, tipoComida, recetas, grupos);
		}

		return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
	}

	private Response obtenerRecomendacionPuntaje(Usuario usuario, String tipoComida, List<Receta> recetas, List<Grupo> grupos) {
		RecomendacionMejorPuntaje recomendacion = new RecomendacionMejorPuntaje();
		Map<Receta, Double> map = recomendacion.recomendar(usuario, TipoComida.valueOf(tipoComida), recetas, grupos);
		String resultado = armaResultado(map);

		return Response.ok(resultado).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	private Response obtenerRecomendacionPiramide(Usuario usuario, String tipoComida, List<Receta> recetas, List<Grupo> grupos) {
		RecomendacionBalanceo recomendacion = new RecomendacionBalanceo();
		Map<Receta, Double> map = recomendacion.recomendar(usuario, TipoComida.valueOf(tipoComida), recetas, grupos);
		String resultado = armaResultado(map);

		return Response.ok(resultado).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	private String armaResultado(Map<Receta, Double> map) {
		JSONArray jsonArray = new JSONArray();
		if(!map.isEmpty()) {
			for(Receta r : map.keySet()){
				jsonArray.put(this.recetaToJsonObject(r, map.get(r)));
			}
		}
		return jsonArray.toString();
	}

	private JSONObject recetaToJsonObject(Receta r, Double puntaje) {
		JSONObject recetaJSON = new JSONObject();
		recetaJSON.put("id", r.getId());
		recetaJSON.put("nombre", r.getNombre());
		recetaJSON.put("puntaje", puntaje);

		return recetaJSON;
	}
}