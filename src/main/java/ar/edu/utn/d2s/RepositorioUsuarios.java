package ar.edu.utn.d2s;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import ar.edu.utn.d2s.exceptions.ExceptionPlanificacionInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class RepositorioUsuarios {

	private static RepositorioUsuarios repositorioUsuarios;
	private List<Usuario> usuarios = new ArrayList<Usuario>();

	public static RepositorioUsuarios getInstance() {
		if (repositorioUsuarios == null) {
			repositorioUsuarios = new RepositorioUsuarios();
		}
		return repositorioUsuarios;
	}

	public void resetearRepositorio(){
		usuarios.clear();
	}

	public Usuario getUsuario(String mail) throws ExceptionUsuarioInexistente {
		if (!contieneUsuario(mail)) {
			throw new ExceptionUsuarioInexistente();
		}
		return usuarios
				.stream()
				.filter(u -> u.getMail().equals(mail))
				.findFirst().get();
	}

	public void agregarUsuario(Usuario usuario) throws ExceptionYaExiste {
		if(contieneUsuario(usuario.getMail())) {
			throw new ExceptionYaExiste(usuario.getNombre());
		}
		usuarios.add(usuario);
	}

	public boolean contieneUsuario(String mail) {
		return usuarios
				.stream()
				.anyMatch(u -> u.getMail().equals(mail));
	}

	public void eliminaUsuario(Usuario usuario) {
		if(contieneUsuario(usuario.getMail())) {
			usuarios.remove(usuario);
		}
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public Usuario getUsuario(Session session, String usuario) throws ExceptionUsuarioInexistente {
		Criteria usuarioCriteria = session.createCriteria(Usuario.class);
		usuarioCriteria.add(Restrictions.eq("mail", usuario));
		if (usuarioCriteria.list().size() == 0) {
			throw new ExceptionUsuarioInexistente();
		}
		return (Usuario) usuarioCriteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> getUsuarios(Session session) {
		Criteria usuarioCriteria = session.createCriteria(Usuario.class);
		return (List<Usuario>) usuarioCriteria.list();
	}

	public Planificacion getPlanificacion(Session session, Integer idPlanificacion) throws ExceptionPlanificacionInexistente {
		Criteria planificacionCriteria = session.createCriteria(Planificacion.class);
		planificacionCriteria.add(Restrictions.eq("id", idPlanificacion));
		if (planificacionCriteria.list().size() == 0) {
			throw new ExceptionPlanificacionInexistente();
		}
		return (Planificacion) planificacionCriteria.uniqueResult();
	}

	public Restriccion getRestriccion(Session session, String idRestriccion) {
		Criteria restriccionCriteria = session.createCriteria(Restriccion.class);
		restriccionCriteria.add(Restrictions.eq("nombre", idRestriccion));
		Restriccion restriccion = null;
		if (restriccionCriteria.list().size() == 1) {
			restriccion = (Restriccion) restriccionCriteria.uniqueResult();
		} else {
			restriccion = new Restriccion();
			restriccion.setNombre(idRestriccion);
		}
		return restriccion;
	}
}