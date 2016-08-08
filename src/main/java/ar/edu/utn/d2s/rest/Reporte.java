package ar.edu.utn.d2s.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import ar.edu.utn.d2s.FiltroCalorias;
import ar.edu.utn.d2s.FiltroGrupo;
import ar.edu.utn.d2s.FiltroIngrediente;
import ar.edu.utn.d2s.FiltroNombre;
import ar.edu.utn.d2s.FiltroPeriodo;
import ar.edu.utn.d2s.FiltroRecetasNuevas;
import ar.edu.utn.d2s.FiltroUsuario;
import ar.edu.utn.d2s.Grupo;
import ar.edu.utn.d2s.HibernateUtil;
import ar.edu.utn.d2s.Ingrediente;
import ar.edu.utn.d2s.Receta;
import ar.edu.utn.d2s.RepositorioGrupos;
import ar.edu.utn.d2s.RepositorioRecetas;
import ar.edu.utn.d2s.RepositorioUsuarios;
import ar.edu.utn.d2s.Usuario;
import ar.edu.utn.d2s.UsuarioComun;
import ar.edu.utn.d2s.exceptions.ExceptionGrupoInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

@Path("/reportes")
public class Reporte {

	@GET
	@Produces("application/json")
	public Response consultaReporte(@QueryParam("user") String user, @QueryParam("desde") String desde, @QueryParam("hasta") String hasta,@QueryParam("ingredientes") String ingredientes, @QueryParam("calorias") String calorias,@QueryParam("usuario") String usuario, @QueryParam("grupo") String grupo, @QueryParam("receta") String receta, @QueryParam("recetasNuevas") String recetasNuevas) {
		Usuario u;		
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();
			u = RepositorioUsuarios.getInstance().getUsuario(session, user);
			List<Grupo> grupos = RepositorioGrupos.getInstance().getGrupos(session);
			List<Usuario> usuarios = RepositorioUsuarios.getInstance().getUsuarios(session);
			List<Receta> recetas = RepositorioRecetas.getInstance().getRecetas(session);
			u.configurarRepositorios(grupos, usuarios, recetas);
			session.close();
		} catch (ExceptionUsuarioInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		if(recetasNuevas != null && recetasNuevas.equals("true"))
		{
			try {
				u.agregarFiltroReporte(new FiltroRecetasNuevas(u));
				List<Receta> rList = u.getReporte();
				String listado = this.allRecetasToJson(rList);

				return Response.ok().entity(listado).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
			} catch (ExceptionYaExiste | ExceptionGrupoInexistente e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
		}
		if (usuario != null) {
			try {
				Usuario us = new UsuarioComun();
				us.setMail(usuario);
				u.agregarFiltroReporte(new FiltroUsuario(us));
			} catch (ExceptionYaExiste | ExceptionValidacionDatos e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
		}
		if(ingredientes != null)
		{
			try {				
				Ingrediente i = new Ingrediente(ingredientes);
				u.agregarFiltroReporte(new FiltroIngrediente(i));
			} catch (ExceptionYaExiste e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
		}
		if(calorias != null)
		{
			try {
				u.agregarFiltroReporte(new FiltroCalorias(0, Integer.parseInt(calorias)));
			} catch (NumberFormatException | ExceptionYaExiste e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
		}
		if(receta != null)
		{
			try {
				u.agregarFiltroReporte(new FiltroNombre(receta));
			} catch (ExceptionYaExiste e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
		}
		if(grupo != null)
		{
			try {
				u.agregarFiltroReporte(new FiltroGrupo(grupo));
			} catch (ExceptionYaExiste e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
		}
		if(desde != null && hasta != null)
		{
			try {
				Calendar calD = Calendar.getInstance();
				DateFormat sdfD = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
				calD.setTime(sdfD.parse(desde));
				
				Calendar calH = Calendar.getInstance();
				DateFormat sdfH = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
				calH.setTime(sdfH.parse(desde));
				
				u.agregarFiltroReporte(new FiltroPeriodo(calD,calH));
			} catch (ParseException | ExceptionYaExiste e) {
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
			}
		}
		List<Receta> rList;
		try {
			rList = u.getReporte();
		} catch (ExceptionGrupoInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		String resultado = allRecetasToJson(rList);

		return Response.ok(resultado).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	private String allRecetasToJson(List<Receta> lista){
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

	private JSONArray ingedientesToListado(List<Ingrediente> lista){
		ArrayList<String> l = new ArrayList<String>();
		for(Ingrediente i : lista){
			l.add(i.getNombre());
		}

		return new JSONArray(l);
	}
}