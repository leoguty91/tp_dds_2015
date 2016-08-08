package ar.edu.utn.d2s;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.*;

import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionPlanificacionSuperior7Dias;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaHorarioNoCorresponde;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaNoCompartidaEnGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class PersistenciaTest {

	private Session session = null;

	@Before
	public void setUp() {
		session = HibernateUtil.getSessionFactory().openSession();
	}

	@Test
	public void testPersistenciaUsuario() throws ExceptionValidacionDatos {

		UsuarioComun usuario = new UsuarioComun("Leo", "leo@hotmail.com", 24);
		usuario.setPassword("123456");

		Transaction tx = session.beginTransaction();
		session.save(usuario);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		Criteria usuarioCriteria = session.createCriteria(UsuarioComun.class);
		usuarioCriteria.add(Restrictions.eq("nombre", "Leo"));
		Usuario u2 = (Usuario) usuarioCriteria.uniqueResult();
		
		assertEquals("Los usuarios deberían ser los mismos",
				usuario, u2);
		session.close();
	}

	@Test
	public void testPersistenciaGrupo() {

		Grupo grupo = new Grupo("Equipo futbol");

		Transaction tx = this.session.beginTransaction();
		session.saveOrUpdate(grupo);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		Criteria grupoCriteria = session.createCriteria(Grupo.class);
		grupoCriteria.add(Restrictions.eq("nombre", "Equipo futbol"));

		assertEquals("Las listas deberían tener los mismos elementos",
				1, grupoCriteria.list().size());
		session.close();
	}

	@Test
	public void testPersistenciaGeneracionReceta() throws ExceptionValidacionDatos, ExceptionYaExiste, ExceptionIngredienteRestringido {

		UsuarioComun usuario1 = new UsuarioComun("Juan", "juan@hotmail.com", 20);
		usuario1.setPassword("123456");
		ArrayList<Ingrediente> ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Vacio", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Asado", TipoAlimento.CARNES));
		Receta receta1 = new Receta("Asado", ingredientes1, "...", Dificultad.MEDIA, Temporada.PRIMAVERA, 600);
		receta1.setAutor(usuario1);

		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario1);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		Criteria recetaCriteria = session.createCriteria(Receta.class);
		recetaCriteria.add(Restrictions.eq("nombre", "Asado"));
		Receta r1 = (Receta) recetaCriteria.uniqueResult();

		assertEquals("Las recetas deberían ser iguales",
				receta1, r1);
		session.close();
	}

	@Test
	public void testPersistenciaRestriccion() {

		Ingrediente azucar = new Ingrediente("Azucar", TipoAlimento.DULCES);
		Restriccion diabetes = new Restriccion("Diabetes", azucar);

		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(diabetes);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();
		
		Criteria restriccionCriteria = session.createCriteria(Restriccion.class);
		restriccionCriteria.add(Restrictions.eq("nombre", "Diabetes"));
		Restriccion r = (Restriccion) restriccionCriteria.uniqueResult();
		assertEquals("Las restricciones deberían ser iguales",
				diabetes, r);
		session.close();
	}

	// Entrega 5
	@Test
	public void testComparteReceta() throws ExceptionYaExiste, ExceptionValidacionDatos, ExceptionUsuarioNoPertenceAlGrupo, ExceptionIngredienteRestringido {

		UsuarioComun usuario1 = new UsuarioComun("Pedro", "pedro@hotmail.com", 20);
		usuario1.setPassword("123456");
		Grupo grupo1 = new Grupo("Equipo basquet");
		ArrayList<Ingrediente> ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Nalga", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Arroz", TipoAlimento.PASTASPANARROZ));
		Receta receta1 = new Receta("Milanesas con arroz", ingredientes1, "...", Dificultad.MEDIA, Temporada.PRIMAVERA, 600);
		receta1.setAutor(usuario1);

		usuario1.unirAlGrupo(grupo1);
		usuario1.compartirReceta(grupo1, receta1);

		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario1);
		session.saveOrUpdate(receta1);
		session.saveOrUpdate(grupo1);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		Criteria recetaCriteria = session.createCriteria(Receta.class);
		recetaCriteria.add(Restrictions.eq("nombre", "Milanesas con arroz"));
		Receta recetaC = (Receta) recetaCriteria.uniqueResult();
		assertEquals("Las recetas deberían ser las mismas",
				receta1, recetaC);
		
		Criteria grupoCriteria = session.createCriteria(Grupo.class);
		grupoCriteria.add(Restrictions.eq("nombre", "Equipo basquet"));
		Grupo grupoC = (Grupo) grupoCriteria.uniqueResult();
		assertTrue("El grupo debería contener la receta",
				grupoC.contieneReceta(recetaC));
		session.close();
	}

	@Test
	public void testUsuarioSeUneAGrupo() throws ExceptionYaExiste, ExceptionValidacionDatos {

		UsuarioComun usuario1 = new UsuarioComun("Andres", "andres@hotmail.com", 25);
		usuario1.setPassword("123456");
		Grupo grupo1 = new Grupo("Equipo volley");
		usuario1.unirAlGrupo(grupo1);

		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario1);
		session.saveOrUpdate(grupo1);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		Criteria usuarioCriteria = session.createCriteria(Usuario.class);
		usuarioCriteria.add(Restrictions.eq("nombre", "Andres"));
		Usuario usuarioC = (Usuario) usuarioCriteria.uniqueResult();
		assertEquals("Los usuarios deberían ser los mismos",
				usuario1, usuarioC);

		Criteria grupoCriteria = session.createCriteria(Grupo.class);
		grupoCriteria.add(Restrictions.eq("nombre", "Equipo volley"));
		Grupo grupoC = (Grupo) grupoCriteria.uniqueResult();
		assertEquals("Las grupos deberían ser los mismos",
				grupo1, grupoC);
		
		assertTrue("El grupo debería contener al usuario",
				grupoC.contieneUsuario(usuarioC));
		session.close();
	}

	@Test
	public void testUsuarioSeBorraDeGrupo() throws ExceptionUsuarioNoPertenceAlGrupo, ExceptionYaExiste, ExceptionValidacionDatos {

		UsuarioComun usuario1 = new UsuarioComun("Carlos", "carlos@hotmail.com", 30);
		usuario1.setPassword("123456");
		Grupo grupo1 = new Grupo("Yoga");
		usuario1.unirAlGrupo(grupo1);

		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario1);
		session.saveOrUpdate(grupo1);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		Criteria usuarioCriteria = session.createCriteria(Usuario.class);
		usuarioCriteria.add(Restrictions.eq("nombre", "Carlos"));
		Usuario usuarioC = (Usuario) usuarioCriteria.uniqueResult();

		Criteria grupoCriteria = session.createCriteria(Grupo.class);
		grupoCriteria.add(Restrictions.eq("nombre", "Yoga"));
		Grupo grupoC = (Grupo) grupoCriteria.uniqueResult();

		assertTrue("El grupo debería contener al usuario",
				grupoC.contieneUsuario(usuarioC));
		
		grupoC.eliminarUsuario(usuarioC);

		tx = session.beginTransaction();
		session.saveOrUpdate(grupoC);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		Criteria grupoCriteriaSinUsuario = session.createCriteria(Grupo.class);
		grupoCriteriaSinUsuario.add(Restrictions.eq("nombre", "Yoga"));
		Grupo grupoCSU = (Grupo) grupoCriteriaSinUsuario.uniqueResult();
		assertFalse("El grupo no debería contener al usuario",
				grupoCSU.contieneUsuario(usuarioC));
		session.close();
	}

	@Test
	public void testCalificaReceta() throws ExceptionValidacionDatos, ExceptionYaExiste, ExceptionUsuarioNoPertenceAlGrupo, ExceptionIngredienteRestringido {

		UsuarioComun usuario1 = new UsuarioComun("Dario", "dario@hotmail.com", 25);
		usuario1.setPassword("123456");
		Grupo grupo1 = new Grupo("Club carnivoros");
		List<Ingrediente> ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Manzana", TipoAlimento.FRUTAS));
		ingredientes1.add(new Ingrediente("Azucar", TipoAlimento.DULCES));
		Receta receta1 = new Receta("Torta de manzana", ingredientes1, "...", Dificultad.MEDIA, Temporada.VERANO, 300);
		receta1.agregarTipoComida(TipoComida.MERIENDA);
		receta1.agregarTipoComida(TipoComida.DESAYUNO);
		receta1.setAutor(usuario1);

		usuario1.unirAlGrupo(grupo1);
		usuario1.compartirReceta(grupo1, receta1);
		usuario1.calificarReceta(grupo1, receta1, 5);
		
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario1);
		session.saveOrUpdate(receta1);
		session.saveOrUpdate(grupo1);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		Criteria grupoCriteria = session.createCriteria(Grupo.class);
		grupoCriteria.add(Restrictions.eq("nombre", "Club carnivoros"));
		Grupo grupoC = (Grupo) grupoCriteria.uniqueResult();

		assertEquals("El grupo deberia tener una calificacion y tiene " + grupoC.getCalificaciones().size() ,
				1, grupoC.getCalificaciones().size());
		assertEquals("Los usuarios deberian ser los mismos",
				usuario1, grupoC.getCalificaciones().get(0).getUsuario());
		assertEquals("Las puntuaciones deberian ser los mismos",
				5, grupoC.getCalificaciones().get(0).getCalificacion());
		session.close();
	}

	@Test
	public void testPlanificaComida() throws ExceptionRecetaHorarioNoCorresponde, ExceptionIngredienteRestringido, ExceptionValidacionDatos, ExceptionYaExiste, ExceptionPlanificacionSuperior7Dias {

		UsuarioComun usuario1 = new UsuarioComun("Emilio", "emilio@hotmail.com", 20);
		usuario1.setPassword("123456");
		List<Ingrediente> ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Merluza", TipoAlimento.PESCADOS));
		ingredientes1.add(new Ingrediente("Harina", TipoAlimento.PASTASPANARROZ));
		Receta receta1 = new Receta("Filet de merluza", ingredientes1, "...", Dificultad.DIFICIL, Temporada.VERANO, 200);
		receta1.agregarTipoComida(TipoComida.CENA);
		receta1.setAutor(usuario1);
		Calendar fecha1 = Calendar.getInstance();
		Planificacion planificacion1 = new Planificacion(fecha1, TipoComida.CENA, receta1);

		usuario1.agregarPlanificacion(planificacion1);

		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario1);
		session.saveOrUpdate(receta1);
		session.saveOrUpdate(planificacion1);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		Criteria usuarioCriteria = session.createCriteria(Usuario.class);
		usuarioCriteria.add(Restrictions.eq("nombre", "Emilio"));
		Usuario u1 = (Usuario) usuarioCriteria.uniqueResult();

		assertEquals("El usuario debería tener una planificacion",
				1, u1.getPlanificaciones().size());
		Planificacion p1 = u1.getPlanificaciones().get(0);
		assertEquals("La planificacion del usuario debería ser Filet de merluza",
				"Filet de merluza", p1.getReceta().getNombre());
		session.close();
	}

	@Test
	public void testReplanificaComida() throws ExceptionRecetaHorarioNoCorresponde, ExceptionPlanificacionSuperior7Dias, ExceptionIngredienteRestringido, ExceptionValidacionDatos, ExceptionYaExiste {
		
		UsuarioComun usuario1 = new UsuarioComun("Federico", "federico@hotmail.com", 20);
		usuario1.setPassword("123456");
		List<Ingrediente> ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Lechuga", TipoAlimento.VEGETALES));
		ingredientes1.add(new Ingrediente("Pan", TipoAlimento.PASTASPANARROZ));
		Receta receta1 = new Receta("Ensalada cesar", ingredientes1, "...", Dificultad.FACIL, Temporada.VERANO, 200);
		receta1.agregarTipoComida(TipoComida.CENA);
		receta1.setAutor(usuario1);
		Calendar fecha1 = Calendar.getInstance();
		Planificacion planificacion1 = new Planificacion(fecha1, TipoComida.CENA, receta1);

		usuario1.agregarPlanificacion(planificacion1);

		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario1);
		session.saveOrUpdate(receta1);
		session.saveOrUpdate(planificacion1);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		Criteria usuarioCriteria = session.createCriteria(Usuario.class);
		usuarioCriteria.add(Restrictions.eq("nombre", "Federico"));
		Usuario u1 = (Usuario) usuarioCriteria.uniqueResult();

		assertEquals("El usuario debería tener una planificacion",
				1, u1.getPlanificaciones().size());
		Planificacion p1 = u1.getPlanificaciones().get(0);
		assertEquals("La planificacion del usuario debería ser Ensalada cesar",
				"Ensalada cesar", p1.getReceta().getNombre());

		Usuario usuario2 = new UsuarioComun("Matias", "matias@hotmail.com", 21);
		usuario2.setPassword("123456");
		List<Ingrediente> ingredientes2 = new ArrayList<Ingrediente>();
		ingredientes2.add(new Ingrediente("Pollo", TipoAlimento.CARNES));
		ingredientes2.add(new Ingrediente("Papas", TipoAlimento.CARNES));
		Receta receta2 = new Receta("Pollo al horno con papas", ingredientes2, "...", Dificultad.DIFICIL, Temporada.INVIERNO, 200);
		receta2.agregarTipoComida(TipoComida.CENA);
		receta2.setAutor(usuario2);

		p1.setReceta(receta2);
		u1.agregarPlanificacion(p1);
		
		tx = session.beginTransaction();
		session.saveOrUpdate(u1);
		session.saveOrUpdate(usuario2);
		session.saveOrUpdate(receta2);
		session.saveOrUpdate(p1);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		usuarioCriteria = session.createCriteria(Usuario.class);
		usuarioCriteria.add(Restrictions.eq("nombre", "Federico"));
		u1 = (Usuario) usuarioCriteria.uniqueResult();

		assertEquals("El usuario debería tener una planificacion",
				1, u1.getPlanificaciones().size());
		Planificacion p2 = u1.getPlanificaciones().get(0);
		assertEquals("La planificacion del usuario debería ser Pollo al horno con papas",
				"Pollo al horno con papas", p2.getReceta().getNombre());
		session.close();
	}

	@Test
	public void testBorraReceta() throws ExceptionRecetaHorarioNoCorresponde, ExceptionYaExiste, ExceptionIngredienteRestringido, ExceptionValidacionDatos, ExceptionUsuarioNoPertenceAlGrupo, ExceptionPlanificacionSuperior7Dias, ExceptionRecetaNoCompartidaEnGrupo {

		UsuarioComun usuario1 = new UsuarioComun("Gabriel", "gabriel@hotmail.com", 19);
		usuario1.setPassword("123456");
		Grupo grupo1 = new Grupo("Tenis");
		List<Ingrediente> ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Leche", TipoAlimento.LACTEOS));
		ingredientes1.add(new Ingrediente("Huevo", TipoAlimento.CARNES));
		Receta receta1 = new Receta("Flan", ingredientes1, "...", Dificultad.FACIL, Temporada.VERANO, 200);
		receta1.agregarTipoComida(TipoComida.MERIENDA);
		receta1.setAutor(usuario1);
		Calendar fecha1 = Calendar.getInstance();
		Planificacion planificacion1 = new Planificacion(fecha1, TipoComida.MERIENDA, receta1);

		usuario1.unirAlGrupo(grupo1);
		usuario1.compartirReceta(grupo1, receta1);
		usuario1.agregarPlanificacion(planificacion1);

		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario1);
		session.saveOrUpdate(grupo1);
		session.saveOrUpdate(planificacion1);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		Criteria uC1 = session.createCriteria(Usuario.class);
		uC1.add(Restrictions.eq("nombre", "Gabriel"));
		Usuario u1 = (Usuario) uC1.uniqueResult();
		Criteria gC1 = session.createCriteria(Grupo.class);
		gC1.add(Restrictions.eq("nombre", "Tenis"));
		Grupo g1 = (Grupo) gC1.uniqueResult();
		Criteria rC1 = session.createCriteria(Receta.class);
		rC1.add(Restrictions.eq("nombre", "Flan"));
		Receta r1 = (Receta) rC1.uniqueResult();

		assertEquals("El usuario debería tener una planificacion",
				1, u1.getPlanificaciones().size());
		assertEquals("El grupo debería tener una receta",
				1, g1.getRecetas().size());

		g1.eliminarReceta(r1);

		tx = session.beginTransaction();
		session.saveOrUpdate(g1);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		assertEquals("El usuario debería tener su planificacion si se elimina la receta del grupo",
				1, u1.getPlanificaciones().size());
		assertEquals("El grupo no debería tener recetas",
				0, g1.getRecetas().size());
		session.close();
	}

	@Test
	public void testObtenerRankingRecetas() throws Exception {
		
		UsuarioComun usuario1 = new UsuarioComun("Hugo", "hugo@hotmail.com", 19);
		usuario1.setPassword("123456");
		UsuarioComun usuario2 = new UsuarioComun("Ignacio", "ignacio@hotmail.com", 19);
		usuario2.setPassword("123456");
		List<Ingrediente> ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Paty", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Pan de Hamburguesa", TipoAlimento.PASTASPANARROZ));
		Receta receta1 = new Receta("Hamburguesa", ingredientes1, "...", Dificultad.FACIL, Temporada.VERANO, 200);
		receta1.agregarTipoComida(TipoComida.CENA);
		receta1.setAutor(usuario1);

		Receta receta2 = usuario2.generarRecetaAPartirDeOtra(receta1);

		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(usuario1);
		session.saveOrUpdate(receta1);
		session.saveOrUpdate(usuario2);
		session.saveOrUpdate(receta2);
		tx.commit();
		session.close();
		session = HibernateUtil.getSessionFactory().openSession();

		Criteria recetaCriteria = session.createCriteria(Receta.class);
		recetaCriteria.add(Restrictions.eq("nombre", "Hamburguesa"));

		assertEquals("La receta1 debería tener 1 receta copiada", 1, receta1.getCopias());
		assertEquals("Deberia haber 2 recetas con el nombre Hamburguesa",
				2, recetaCriteria.list().size());

		Criteria recetaCriteria2 = session.createCriteria(Receta.class);
		recetaCriteria2.add(Restrictions.eq("nombre", "Hamburguesa"));
		recetaCriteria2.add(Restrictions.eq("autor.id", "hugo@hotmail.com"));
		Receta recetaConCopia = (Receta) recetaCriteria2.uniqueResult();

		assertEquals("La receta debería tener una copia",
				1, recetaConCopia.getCopias());
		session.close();
	}
}