package ar.edu.utn.d2s.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import ar.edu.utn.d2s.Grupo;
import ar.edu.utn.d2s.HibernateUtil;
import ar.edu.utn.d2s.Receta;
import ar.edu.utn.d2s.RepositorioGrupos;
import ar.edu.utn.d2s.RepositorioRecetas;
import ar.edu.utn.d2s.RepositorioUsuarios;
import ar.edu.utn.d2s.Usuario;
import ar.edu.utn.d2s.exceptions.ExceptionGrupoInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaNoCompartidaEnGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

@Path("/grupos")
public class Grupos {

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response consultarGrupoID(@PathParam("id") String id){
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Grupo grupo;
		try {
			grupo = RepositorioGrupos.getInstance().getGrupo(session, id);
		} catch (ExceptionGrupoInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		String respuesta = grupoToJsonObject(grupo).toString();
		session.close();

		return Response.ok().entity(respuesta).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@SuppressWarnings("unchecked")
	@GET
	@Produces("application/json")
	public Response consultarUsuariosGrupo(@QueryParam("usuario") String usr){
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		String respuesta;
		if (usr == null) {
			Criteria grupoCriteria = session.createCriteria(Grupo.class);
			respuesta = allGruposToJson(grupoCriteria.list());
		} else {
			Usuario usuario;
			try {
				usuario = RepositorioUsuarios.getInstance().getUsuario(session, usr);
			} catch (ExceptionUsuarioInexistente e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
			respuesta = this.allGruposToJson(usuario.getGrupos());
		}
		session.close();

		return Response.ok(respuesta).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@Path("/{id}/usuarios")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response agregarUsuarioAGrupo(@PathParam("id") String id, String jSon){
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Grupo grupo;
		Usuario usuario;
		try {
			grupo = RepositorioGrupos.getInstance().getGrupo(session, id);
			usuario = RepositorioUsuarios.getInstance().getUsuario(session, getUsuarioFromJson(jSon));
			usuario.unirAlGrupo(grupo);
		} catch (ExceptionGrupoInexistente | ExceptionUsuarioInexistente
				| ExceptionYaExiste e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario);
		session.saveOrUpdate(grupo);
		tx.commit();
		session.close();
		
		return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
	}	
	
	@Path("/{id}/usuarios/{usr}")
	@DELETE
	@Produces("application/json")
	public Response suprimirUsuarioAGrupo(@PathParam("id") String id, @PathParam("usr") String usr, String jSon){
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Grupo grupo;
		Usuario usuario;
		try {
			grupo = RepositorioGrupos.getInstance().getGrupo(session, id);
			usuario = RepositorioUsuarios.getInstance().getUsuario(session, usr);
			grupo.eliminarUsuario(usuario);
		} catch (ExceptionGrupoInexistente | ExceptionUsuarioInexistente
				| ExceptionUsuarioNoPertenceAlGrupo e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario);
		session.saveOrUpdate(grupo);
		tx.commit();
		session.close();

		return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
	}

	@Path("/{id}/recetas")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response compartirRecetaAGrupo(@PathParam("id") String id, String jSon) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Grupo grupo;
		Receta receta;
		try {
			grupo = RepositorioGrupos.getInstance().getGrupo(session, id);
			receta = RepositorioRecetas.getInstance().getReceta(session, getRecetaId(jSon));
			grupo.agregarReceta(receta);
		} catch (ExceptionGrupoInexistente | ExceptionRecetaInexistente
				| ExceptionYaExiste e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(grupo);
		tx.commit();
		session.close();

		return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
	}

	@Path("/{id}/recetas/{rid}")
	@DELETE
	@Produces("application/json")
	public Response desCompartirRecetaAGrupo(@PathParam("id") String id, @PathParam("rid") String rid, String jSon) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Grupo grupo;
		Receta receta;
		try {
			grupo = RepositorioGrupos.getInstance().getGrupo(session, id);
			receta = RepositorioRecetas.getInstance().getReceta(session, Integer.parseInt(rid));
			grupo.eliminarReceta(receta);
		} catch (ExceptionGrupoInexistente | ExceptionRecetaInexistente
				| ExceptionRecetaNoCompartidaEnGrupo e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(grupo);
		tx.commit();
		session.close();

		return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
	}

	private String getUsuarioFromJson(String jSon) {
		JSONObject jo = new JSONObject(jSon);
		String usuario = null;
		if (jo.has("email")) {
			usuario = jo.getString("email");
		}
		return usuario;
	}

	private String allGruposToJson(List<Grupo> grupos) {
		JSONArray jsonArray = new JSONArray();
		for (Grupo grupo : grupos) {
			jsonArray.put(grupoToJsonObject(grupo));
		}
		return jsonArray.toString();
	}

	private JSONObject grupoToJsonObject(Grupo grupo) {
		JSONObject grupoJSON = new JSONObject();
		grupoJSON.put("nombre", grupo.getNombre());
		return grupoJSON;
	}

	private Integer getRecetaId(String jSon) {
		JSONObject jo = new JSONObject(jSon);
		Integer recetaId = null;
		if (jo.has("recetaId")) {
			recetaId = jo.getInt("recetaId");
		}
		return recetaId;
	}
}