package ar.edu.utn.d2s.rest;

import javax.ws.rs.Consumes;

import ar.edu.utn.d2s.HibernateUtil;
import ar.edu.utn.d2s.RepositorioUsuarios;
import ar.edu.utn.d2s.Usuario;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioInexistente;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.json.JSONObject;

@Path("/login")
public class Login {

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response loginMethod(String jSon) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		JSONObject jo = new JSONObject(jSon);
		String u = "";
		if (jo.has("usuario")) {
			u = jo.getString("usuario");
		}
		String password = "";
		if (jo.has("password")) {
			password = jo.getString("password");
		}
		Usuario usuario;
		try {
			usuario = RepositorioUsuarios.getInstance().getUsuario(session, u);
		} catch (ExceptionUsuarioInexistente e) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"El usuario/contraseña es incorrecto\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		if (!usuario.getPassword().equals(password)) {
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity("{\"result\":\"El usuario/contraseña es incorrecto\"}").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
		}
		session.close();

		return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST").build();
	}
}