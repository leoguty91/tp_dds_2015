package ar.edu.utn.d2s.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import ar.edu.utn.d2s.Grupo;
import ar.edu.utn.d2s.HibernateUtil;
import ar.edu.utn.d2s.Ingrediente;
import ar.edu.utn.d2s.RepositorioGrupos;
import ar.edu.utn.d2s.RepositorioRecetas;
import ar.edu.utn.d2s.RepositorioUsuarios;
import ar.edu.utn.d2s.Restriccion;
import ar.edu.utn.d2s.Sexo;
import ar.edu.utn.d2s.Usuario;
import ar.edu.utn.d2s.UsuarioComun;
import ar.edu.utn.d2s.exceptions.ExceptionGrupoInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

@Path("/usuarios")
public class Usuarios {

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response consultarUsuarioID(@PathParam("id") String id) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Usuario usuario;
		try {
			usuario = RepositorioUsuarios.getInstance().getUsuario(session, id);
		} catch (ExceptionUsuarioInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		String respuesta = usuarioToJsonObject(usuario).toString();
		session.close();

		return Response.ok(respuesta).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@GET
	@Produces("application/json")
	public Response consultarUsuariosGrupo(@QueryParam("grupo") String g) {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		Grupo grupo;
		try {
			grupo = RepositorioGrupos.getInstance().getGrupo(session, g);
		} catch (ExceptionGrupoInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		String respuesta = listaUsuariosToJsonArray(grupo.getUsuarios());
		session.close();

		return Response.ok(respuesta).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response crearUruario(String jSon) throws ParseException {
		SessionFactory sf = HibernateUtil.buildSessionFactory();
		Session session = sf.openSession();
		JSONObject jo = new JSONObject(jSon);
		String uNombre = (jo.has("nombre")) ? jo.getString("nombre") : "";
		String uMail = (jo.has("email")) ? jo.getString("email") : "";
		String uPassword = (jo.has("password")) ? jo.getString("password") : "";
		Calendar uFechaNacimiento = Calendar.getInstance();
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		String fechaNacimiento = (jo.has("fechaNacimiento")) ? jo.getString("fechaNacimiento") : ""; 
		uFechaNacimiento.setTime(sdf.parse(fechaNacimiento));
		Date ahora = Calendar.getInstance().getTime();
		Date fechaUsuario = uFechaNacimiento.getTime();
		String uAltura = (jo.has("altura")) ? jo.getString("altura") : "";
		String uSexo = (jo.has("sexo")) ? jo.getString("sexo") : "";
		List<Ingrediente> uPreferencias = new ArrayList<Ingrediente>();
		if (jo.has("preferencias")) {
			JSONArray arrPreferencias = jo.getJSONArray("preferencias");
			uPreferencias = armaPreferencias(session, arrPreferencias);
		}
		int uRutina = (jo.has("rutina")) ? jo.getInt("rutina") : 0;
		List<Restriccion> uRestricciones = new ArrayList<Restriccion>();
		if (jo.has("condiciones")) {
			JSONArray arrRestricciones = jo.getJSONArray("condiciones");
			uRestricciones = armaRestricciones(session, arrRestricciones);
		}
		Usuario usuario;
		Long diferenciaDias = getDifferenceDays(fechaUsuario, ahora);
		int uEdad = convertirDiasAAnios(diferenciaDias);
		try {
			usuario = new UsuarioComun(uNombre, uMail, uEdad);
			usuario.setPassword(uPassword);
			usuario.setAltura(uAltura);
			usuario.setSexo(Sexo.valueOf(uSexo));
			for (int i = 0; i < uPreferencias.size(); i++) {
				try {
					usuario.agregarPreferencia(uPreferencias.get(i));
				} catch (ExceptionYaExiste e) {
					return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
				}
			}
			usuario.setRutina(uRutina);
			for (int i = 0; i < uRestricciones.size(); i++) {
				try {
					usuario.agregarRestriccion(uRestricciones.get(i));
				} catch (ExceptionYaExiste e) {
					return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
				}
			}
		} catch (ExceptionValidacionDatos e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"" + e.getMessage() + "\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		Transaction tx = session.beginTransaction();
		session.save(usuario);
		tx.commit();
		session.close();

		return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
	}

	private List<Restriccion> armaRestricciones(Session session, JSONArray array) {
		List<Restriccion> restricciones = new ArrayList<Restriccion>();
		for (int i = 0; i < array.length(); i++) {
			String res = array.getString(i);
			Restriccion restriccion = RepositorioUsuarios.getInstance().getRestriccion(session, res);
			restricciones.add(restriccion);
		}
		return restricciones;
	}

	private List<Ingrediente> armaPreferencias(Session session, JSONArray array) {
		List<Ingrediente> preferencias = new ArrayList<Ingrediente>();
		for (int i = 0; i < array.length(); i++) {
			String ing = array.getString(i);
			Ingrediente ingrediente = RepositorioRecetas.getInstance().getIngrediente(session, ing);
			preferencias.add(ingrediente);
		}
		return preferencias;
	}

	private String listaUsuariosToJsonArray(List<Usuario> usuarios) {
		JSONArray jsonArray = new JSONArray();		
		for(Usuario r : usuarios){
			jsonArray.put(usuarioToJsonObject(r));			
		}

		return jsonArray.toString();
	}

	private JSONObject usuarioToJsonObject(Usuario r) {
		JSONObject object = new JSONObject();
		object.put("email", r.getMail());
		object.put("nombre", r.getNombre());
		return object;
	}

	private int convertirDiasAAnios(Long dias) {
		return (int) (dias.intValue() / 365);
	}

	private static long getDifferenceDays(Date d1, Date d2) {
		long diff = d2.getTime() - d1.getTime();
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}
}