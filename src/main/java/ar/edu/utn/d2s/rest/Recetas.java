package ar.edu.utn.d2s.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;

import ar.edu.utn.d2s.Dificultad;
import ar.edu.utn.d2s.Grupo;
import ar.edu.utn.d2s.HibernateUtil;
import ar.edu.utn.d2s.Ingrediente;
import ar.edu.utn.d2s.Receta;
import ar.edu.utn.d2s.RepositorioGrupos;
import ar.edu.utn.d2s.RepositorioRecetas;
import ar.edu.utn.d2s.RepositorioUsuarios;
import ar.edu.utn.d2s.Temporada;
import ar.edu.utn.d2s.Usuario;
import ar.edu.utn.d2s.exceptions.ExceptionGrupoInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaNoCompartidaEnGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

@Path("/recetas")
public class Recetas {

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response consultarRecetaID(@PathParam("id") String id) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Receta receta;
		try {
			receta = RepositorioRecetas.getInstance().getReceta(session, Integer.parseInt(id));
		} catch (ExceptionRecetaInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
		}
		String respuesta = recetaToJsonObject(receta).toString();
		session.close();

		return Response.ok(respuesta).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@SuppressWarnings("unchecked")
	@GET
	@Produces("application/json")
	public Response consultarRecetasUsuario(@QueryParam("usuario") String usrId, @QueryParam("ultimasRecetas") String uR, @QueryParam("autor") String autor, @QueryParam("grupo") String grupoId) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		String respuesta;
		if (uR != null && uR.equals("true") || usrId != null) {
			Criteria recetaCriteria = session.createCriteria(Receta.class);
			recetaCriteria.add(Restrictions.eq("visibilidad", true));
			if (usrId != null) {
				try {
					Usuario usuario = RepositorioUsuarios.getInstance().getUsuario(session, usrId);
					respuesta = allRecetasToJson(usuario.listarTodasLasRecetas(recetaCriteria.list()));
				} catch (ExceptionUsuarioInexistente e) {
					return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
				}
			} else {
				respuesta = allRecetasToJson(recetaCriteria.list());
			}
		} else if (autor != null) {
			Usuario usuario;
			try {
				usuario = RepositorioUsuarios.getInstance().getUsuario(session, autor);
				respuesta = allRecetasToJson(usuario.getRecetas());
			} catch (ExceptionUsuarioInexistente e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
		} else if (grupoId != null) {
			Grupo grupo;
			try {
				grupo = RepositorioGrupos.getInstance().getGrupo(session, grupoId);
			} catch (ExceptionGrupoInexistente e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
			respuesta = allRecetasToJson(grupo.getRecetas());
		} else {
			Criteria recetaCriteria = session.createCriteria(Receta.class);
			respuesta = allRecetasToJson(recetaCriteria.list());
		}
		session.close();

		return Response.ok(respuesta).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response agregarReceta(String jSon) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		JSONObject jo = new JSONObject(jSon);
		int rid = (jo.has("id")) ? jo.getInt("id") : 0;
		String u = jo.getString("autor");
		String rNombre = (jo.has("nombre")) ? jo.getString("nombre") : "";
		JSONArray arrIngredientes = jo.getJSONArray("ingredientes");
		List<Ingrediente> rIngrediente = armaIngredientes(session, arrIngredientes);
		String rProcedimiento = (jo.has("procedimiento")) ? jo.getString("procedimiento") : "";
		String rDificultad = (jo.has("dificultad")) ? jo.getString("dificultad") : "";
		String rTemporada = (jo.has("temporada")) ? jo.getString("temporada") : "";
		int rCalorias = (jo.has("calorias")) ? jo.getInt("calorias") : 0;
		Usuario usuario;
		Receta recetaOriginal = null;
		Receta recetaClonada = null;
		try {
			usuario = RepositorioUsuarios.getInstance().getUsuario(session, u);
		} catch (ExceptionUsuarioInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		if (rid != 0) {
			try {
				recetaOriginal = RepositorioRecetas.getInstance().getReceta(session, rid);
				recetaClonada = usuario.generarRecetaAPartirDeOtra(recetaOriginal);
			} catch (CloneNotSupportedException | ExceptionValidacionDatos
					| ExceptionYaExiste | ExceptionIngredienteRestringido | ExceptionRecetaInexistente e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
		} else {
			try {
				usuario.generarRecetaNueva(rNombre, rIngrediente, rProcedimiento, Dificultad.valueOf(rDificultad), Temporada.valueOf(rTemporada), rCalorias);
			} catch (ExceptionValidacionDatos | ExceptionYaExiste
					| ExceptionIngredienteRestringido e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
		}
		Transaction tx = session.beginTransaction();
		if (recetaOriginal != null) {
			session.saveOrUpdate(recetaOriginal.getAutor());
			session.saveOrUpdate(recetaOriginal);
			session.saveOrUpdate(usuario);
			session.saveOrUpdate(recetaClonada);
		} else {
			session.saveOrUpdate(usuario);
		}
		tx.commit();
		session.close();

		return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@PUT
	@Path("/{id}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response actualizarReceta(@PathParam("id") String idReceta, String jSon) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Receta recetaOriginal;
		Receta recetaClon;
		JSONObject jo = new JSONObject(jSon);
		String rNombre = (jo.has("nombre")) ? jo.getString("nombre") : "";
		JSONArray arrIngredientes = jo.getJSONArray("ingredientes");
		List<Ingrediente> rIngrediente = armaIngredientes(session, arrIngredientes);
		String rProcedimiento = (jo.has("procedimiento")) ? jo.getString("procedimiento") : "";
		String rDificultad = (jo.has("dificultad")) ? jo.getString("dificultad") : "";
		String rTemporada = (jo.has("temporada")) ? jo.getString("temporada") : "";
		Integer rCalorias = (jo.has("calorias")) ? jo.getInt("calorias") : 0;
		try {
			recetaOriginal = RepositorioRecetas.getInstance().getReceta(session, Integer.parseInt(idReceta));
		} catch (ExceptionRecetaInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Usuario usuario = recetaOriginal.getAutor();
		try {
			recetaClon = usuario.modificarReceta(recetaOriginal, rNombre, rIngrediente, rProcedimiento, Dificultad.valueOf(rDificultad), Temporada.valueOf(rTemporada), rCalorias);
		} catch (CloneNotSupportedException | ExceptionValidacionDatos
				| ExceptionYaExiste | ExceptionIngredienteRestringido
				| ExceptionRecetaNoCompartidaEnGrupo e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario);
		session.saveOrUpdate(recetaClon);
		for (Grupo grupo : usuario.getGrupos()) {
			session.saveOrUpdate(grupo);
		}
		tx.commit();
		session.close();

		return Response.ok().entity("{\"result\":\"Receta actualizada correctamente.\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@DELETE
	@Path("/{id}")
	@Produces("application/json")
	public Response eliminarReceta(@PathParam("id") String id, String json) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Receta receta;
		Usuario usuario;
		try {
			receta = RepositorioRecetas.getInstance().getReceta(session, Integer.parseInt(id));
			usuario = receta.getAutor();
			usuario.eliminarReceta(receta);
		} catch (ExceptionRecetaNoCompartidaEnGrupo
				| ExceptionValidacionDatos | ExceptionRecetaInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario);
		tx.commit();
		session.close();

		return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}
	
	private String allRecetasToJson(List<Receta> lista){
		JSONArray jsonArray = new JSONArray();
		for(Receta r : lista){
			jsonArray.put(recetaToJsonObject(r));			
		}

		return jsonArray.toString();
	}

	private String allRecetasToJson(HashSet<Receta> lista){
		JSONArray jsonArray = new JSONArray();
		for(Receta r : lista){
			jsonArray.put(recetaToJsonObject(r));			
		}

		return jsonArray.toString();
	}

	private JSONObject recetaToJsonObject(Receta r) {
		JSONObject recetaJSON = new JSONObject();
		recetaJSON.put("id", r.getId());
		recetaJSON.put("autor", r.getAutor());
		recetaJSON.put("nombre", r.getNombre());
		recetaJSON.put("ingredientes", ingedientesToListado(r.getIngredientes()));
		recetaJSON.put("procedimiento", r.getProcedimiento());
		recetaJSON.put("dificultad", r.getDificultad().toString());
		recetaJSON.put("temporada", r.getTemporada().toString());
		recetaJSON.put("calorias", r.getCalorias());

		return recetaJSON;
	}

	private List<Ingrediente> armaIngredientes(Session session, JSONArray array) {
		List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();
		for (int i = 0; i < array.length(); i++) {
			String ing = array.getString(i);
			Ingrediente ingrediente = RepositorioRecetas.getInstance().getIngrediente(session, ing);
			ingredientes.add(ingrediente);
		}
		return ingredientes;
	}

	private JSONArray ingedientesToListado(List<Ingrediente> lista){
		ArrayList<String> l = new ArrayList<String>();
		for(Ingrediente i : lista){
			l.add(i.getNombre());
		}

		return new JSONArray(l);
	}
}