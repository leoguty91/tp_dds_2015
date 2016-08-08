package ar.edu.utn.d2s.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import ar.edu.utn.d2s.HibernateUtil;
import ar.edu.utn.d2s.Ingrediente;
import ar.edu.utn.d2s.Planificacion;
import ar.edu.utn.d2s.Receta;
import ar.edu.utn.d2s.RepositorioRecetas;
import ar.edu.utn.d2s.RepositorioUsuarios;
import ar.edu.utn.d2s.TipoComida;
import ar.edu.utn.d2s.Usuario;
import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionPlanificacionInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionPlanificacionSuperior7Dias;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaHorarioNoCorresponde;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioInexistente;

@Path("/planificaciones")
public class Planificaciones {
	
	@GET
	@Produces("application/json")
	public Response getPlanificaciones(@QueryParam("usuario") String u) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		String respuesta;
		if (u != null) {
			Usuario usuario;
			try {
				usuario = RepositorioUsuarios.getInstance().getUsuario(session, u);
				respuesta = allPlanificacionesToJson(usuario.getPlanificaciones());
			} catch (ExceptionUsuarioInexistente e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
			return Response.ok().entity(respuesta).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
		}
		List<Usuario> usuarios = RepositorioUsuarios.getInstance().getUsuarios(session);
		List<Planificacion> planificaciones = getPlanificaciones(usuarios);
		respuesta = planificacionesToJsonArray(planificaciones).toString();
		session.close();

		return Response.ok(respuesta).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response getPlanificacion(@PathParam("id") String id) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Planificacion planificacion;
		try {
			planificacion = RepositorioUsuarios.getInstance().getPlanificacion(session, Integer.parseInt(id));
		} catch (ExceptionPlanificacionInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		String respuesta = planificacionToJsonObject(planificacion).toString();
		session.close();

		return Response.ok(respuesta).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response actualizaPlanificacion(String jSON) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Planificacion planificacion;
		try {
			planificacion = planificacionFromJson(session, jSON);
			planificacion.getUsuario().agregarPlanificacion(planificacion);
		} catch (ParseException | ExceptionPlanificacionSuperior7Dias
				| ExceptionRecetaHorarioNoCorresponde
				| ExceptionRecetaInexistente | ExceptionUsuarioInexistente
				| ExceptionIngredienteRestringido e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Usuario usuario = planificacion.getUsuario();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario);
		tx.commit();
		session.close();

		return Response.ok().entity("{\"result\":\"Planificacion creada correctamente\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@PUT
	@Path("/{id}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response putPlanificacion(@PathParam("id") String idPlanificacion, String jSON) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Planificacion p;
		Planificacion planificacion;
		try {
			p = planificacionFromJson(session, jSON);
			planificacion = RepositorioUsuarios.getInstance().getPlanificacion(session, Integer.parseInt(idPlanificacion));
			planificacion.setTipoComida(p.getTipoComida());
			planificacion.setDia(p.getDia());
			planificacion.setReceta(p.getReceta());
			planificacion.getUsuario().agregarPlanificacion(planificacion);
		} catch (ParseException | ExceptionPlanificacionSuperior7Dias
				| ExceptionRecetaHorarioNoCorresponde
				| ExceptionRecetaInexistente | ExceptionUsuarioInexistente
				| ExceptionPlanificacionInexistente
				| ExceptionIngredienteRestringido e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Usuario usuario = planificacion.getUsuario();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario);
		tx.commit();
		session.close();

		return Response.ok().entity("{\"result\":\"Planificacion actualizada correctamente\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();		
	}

	@DELETE
	@Path("/{id}")
	@Produces("application/json")
	public Response eliminaPlanificacion(@PathParam("id") String idPlanificacion) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Planificacion planificacion;
		try {
			planificacion = RepositorioUsuarios.getInstance().getPlanificacion(session, Integer.parseInt(idPlanificacion));
		} catch (ExceptionPlanificacionInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Usuario usuario = planificacion.getUsuario();
		usuario.eliminarPlanificacion(planificacion);
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario);
		tx.commit();
		session.close();

		return Response.ok().entity("{\"result\":\"Planificacion eliminada correctamente\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	private List<Planificacion> getPlanificaciones(List<Usuario> usuarios) {
		List<Planificacion> planificaciones = new ArrayList<Planificacion>();
		for(Usuario u : usuarios){
			planificaciones.addAll(u.getPlanificaciones());
		}
		return planificaciones;
	}

	private String allPlanificacionesToJson(List<Planificacion> lista){
		String respuesta = "";
		if (lista.size() > 0) {
			JSONArray jsonArray = new JSONArray();
			for(Planificacion p : lista){
				jsonArray.put(planificacionToJsonObject(p));			
			}
			respuesta = jsonArray.toString();
		}
		return respuesta;
	}

	private JSONArray planificacionesToJsonArray(List<Planificacion> planificaciones) {
		JSONArray jArray = new JSONArray();
		for(Planificacion p : planificaciones) {
			jArray.put(this.planificacionToJsonObject(p));
		}
		return jArray;
	}

	private JSONObject planificacionToJsonObject(Planificacion p) {
		JSONObject planificacionJSON = new JSONObject();
		planificacionJSON.put("id", p.getId());
		planificacionJSON.put("usuario", p.getUsuario().getMail());
		planificacionJSON.put("fecha",( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" ) ).format( p.getDia().getTime() ).toString() );
		planificacionJSON.put("receta", this.recetaToJsonObject(p.getReceta()));
		planificacionJSON.put("tipoComida", p.getTipoComida().toString());
		
		return planificacionJSON;
	}

	private JSONObject recetaToJsonObject(Receta r) {
		JSONObject recetaJSON = new JSONObject();
		recetaJSON.put("id", r.getId());
		recetaJSON.put("autor", r.getAutor().toString());
		recetaJSON.put("nombre", r.getNombre());
		recetaJSON.put("ingredientes", this.ingedientesToListado(r.getIngredientes()));
		recetaJSON.put("procedimiento", r.getProcedimiento());
		recetaJSON.put("dificultad", r.getDificultad().toString());
		recetaJSON.put("temporada", r.getTemporada().toString());
		recetaJSON.put("calorias", r.getCalorias());
		return recetaJSON;
	}

	private JSONArray ingedientesToListado(List<Ingrediente> lista){
		ArrayList<String> l = new ArrayList<String>();
		for(Ingrediente i : lista){
			l.add(i.getNombre());
		}
		return new JSONArray(l);
	}

	private Planificacion planificacionFromJson(Session session, String jSon) throws ParseException, ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde, ExceptionRecetaInexistente, ExceptionUsuarioInexistente {
		JSONObject jo = new JSONObject(jSon);
		String fecha = "";
		if (jo.has("fecha")) {
			fecha = jo.getString("fecha");
		}
		String usuario = "";
		if (jo.has("usuario")) {
			usuario = jo.getString("usuario");
		}
		int IdReceta = 0;
		if (jo.has("receta")) {
			IdReceta = jo.getJSONObject("receta").getInt("id");
		}
		String tipoComida = "";
		if (jo.has("tipoComida")) {
			tipoComida = jo.getString("tipoComida");
		}
		Calendar cal = Calendar.getInstance();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		cal.setTime(sdf.parse(fecha));

		Planificacion planificacion = new Planificacion();

		planificacion.setTipoComida(TipoComida.valueOf(tipoComida));
		planificacion.setReceta(RepositorioRecetas.getInstance().getReceta(session, IdReceta));
		planificacion.setUsuario(RepositorioUsuarios.getInstance().getUsuario(session, usuario));
		planificacion.setDia(cal);
		
		return planificacion;
	}
}