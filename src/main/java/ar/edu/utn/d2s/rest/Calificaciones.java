package ar.edu.utn.d2s.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import ar.edu.utn.d2s.Calificacion;
import ar.edu.utn.d2s.Grupo;
import ar.edu.utn.d2s.HibernateUtil;
import ar.edu.utn.d2s.Receta;
import ar.edu.utn.d2s.RepositorioGrupos;
import ar.edu.utn.d2s.RepositorioRecetas;
import ar.edu.utn.d2s.RepositorioUsuarios;
import ar.edu.utn.d2s.Usuario;
import ar.edu.utn.d2s.exceptions.ExceptionCalificacionInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionGrupoInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;

@Path("/calificaciones")
public class Calificaciones {

	@GET
	@Produces("application/json")
	public Response consultarCalificacion(@QueryParam("grupo") String g, @QueryParam("usuario") String u, @QueryParam("receta") String r){
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Usuario usuario;
		Grupo grupo;
		Receta receta;
		try {
			usuario = RepositorioUsuarios.getInstance().getUsuario(session, u);
			receta = RepositorioRecetas.getInstance().getReceta(session, Integer.parseInt(r));
			grupo = RepositorioGrupos.getInstance().getGrupo(session, g);
		} catch (NumberFormatException | ExceptionRecetaInexistente
				| ExceptionUsuarioInexistente | ExceptionGrupoInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Calificacion calificacion = grupo.getCalificacionObj(usuario, receta);
		String respuesta;
		if (calificacion == null) {
			respuesta = GenerarRespuestaJson(0, g, Integer.parseInt(r), u, 0);
		} else {
			respuesta = GenerarRespuestaJson(calificacion.getId(), calificacion.getGrupo().getNombre(), calificacion.getReceta().getId(), calificacion.getUsuario().getMail(), calificacion.getCalificacion());
		}
		session.close();

		return Response.ok(respuesta).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response calificarRecetaGrupo(String jSon){
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		JSONObject jo = new JSONObject(jSon);
		int r = 0;
		if (jo.has("receta")) {
			r = jo.getInt("receta");
		}
		String g = "";
		if (jo.has("grupo")) {
			g = jo.getString("grupo");
		}
		String u = "";
		if (jo.has("usuario")) {
			u = jo.getString("usuario");
		}
		int calificacion = 0;
		if (jo.has("calificacion")) {
			calificacion = jo.getInt("calificacion");
		}
		Usuario usuario;
		Grupo grupo;
		Receta receta;
		try {
			usuario = RepositorioUsuarios.getInstance().getUsuario(session, u);
			grupo = RepositorioGrupos.getInstance().getGrupo(session, g);
			receta = RepositorioRecetas.getInstance().getReceta(session, r);
			usuario.calificarReceta(grupo, receta, calificacion);
		} catch (NumberFormatException | ExceptionRecetaInexistente
				| ExceptionUsuarioInexistente | ExceptionGrupoInexistente
				| ExceptionValidacionDatos | ExceptionUsuarioNoPertenceAlGrupo e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(grupo);
		tx.commit();
		session.close();
		
		return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@PUT
	@Path("/{id}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response actualizaCalificacion(@PathParam("id") String id, String jSon) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		JSONObject jo = new JSONObject(jSon);
		int r = 0;
		if (jo.has("receta")) {
			r = jo.getInt("receta");
		}
		String g = "";
		if (jo.has("grupo")) {
			g = jo.getString("grupo");
		}
		String u = "";
		if (jo.has("usuario")) {
			u = jo.getString("usuario");
		}
		int c = 0;
		if (jo.has("calificacion")) {
			c = jo.getInt("calificacion");
		}
		Usuario usuario;
		Grupo grupo;
		Receta receta;
		try {
			usuario = RepositorioUsuarios.getInstance().getUsuario(session, u);
			grupo = RepositorioGrupos.getInstance().getGrupo(session, g);
			receta = RepositorioRecetas.getInstance().getReceta(session, r);
			grupo.modificarCalificacion(usuario, receta, c);
		} catch (NumberFormatException | ExceptionRecetaInexistente
				| ExceptionUsuarioInexistente | ExceptionGrupoInexistente
				| ExceptionValidacionDatos e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(grupo);
		tx.commit();
		session.close();

		return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@DELETE
	@Path("/{id}")
	@Produces("application/json")
	public Response suprimirCalificacion(@PathParam("id") String id){
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Calificacion calificacion;
		try {
			calificacion = RepositorioGrupos.getInstance().getCalificacion(session, Integer.parseInt(id));
		} catch (ExceptionCalificacionInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Grupo grupo = calificacion.getGrupo();
		grupo.eliminarCalificacion(calificacion.getId());
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(grupo);
		tx.commit();
		session.close();
		
		return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	private String GenerarRespuestaJson(int id, String grupo, Integer receta, String usuario, int calificacion) {
		JSONArray jsonArray = new JSONArray();
		JSONObject object = new JSONObject();
		object.put("id", id);
		object.put("grupo", grupo);
		object.put("receta", receta);
		object.put("usuario", usuario);
		object.put("calificacion", calificacion);
		jsonArray.put(object);

		return jsonArray.toString();
	}
}