package ar.edu.utn.d2s.cargadatos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ar.edu.utn.d2s.Dificultad;
import ar.edu.utn.d2s.Grupo;
import ar.edu.utn.d2s.HibernateUtil;
import ar.edu.utn.d2s.Ingrediente;
import ar.edu.utn.d2s.PiramideAlimenticia;
import ar.edu.utn.d2s.Planificacion;
import ar.edu.utn.d2s.Receta;
import ar.edu.utn.d2s.Restriccion;
import ar.edu.utn.d2s.Temporada;
import ar.edu.utn.d2s.TipoAlimento;
import ar.edu.utn.d2s.TipoComida;
import ar.edu.utn.d2s.Usuario;
import ar.edu.utn.d2s.UsuarioAdministrador;
import ar.edu.utn.d2s.UsuarioComun;
import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionPlanificacionSuperior7Dias;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaHorarioNoCorresponde;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class CargaDatos {

	public static void main(String[] args) throws ExceptionValidacionDatos, ExceptionYaExiste, ExceptionIngredienteRestringido, ExceptionUsuarioNoPertenceAlGrupo, ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		// Usuarios
		Usuario usuario1 = new UsuarioComun("Juan", "juan@hotmail.com", 20);
		usuario1.setPassword("123456");
		Usuario usuario2 = new UsuarioComun("Leo", "leo@hotmail.com", 24);
		usuario2.setPassword("123456");
		Usuario usuario3 = new UsuarioComun("Pedro", "pedro@hotmail.com", 20);
		usuario3.setPassword("123456");
		Ingrediente i1 = new Ingrediente("Azucar", TipoAlimento.DULCES);
		usuario3.agregarRestriccion(new Restriccion("Diabetes", i1));
		Usuario usuario4 = new UsuarioComun("Andres", "andres@hotmail.com", 25);
		usuario4.setPassword("123456");
		Ingrediente i2 = new Ingrediente("Harina", TipoAlimento.PASTASPANARROZ);
		usuario4.agregarRestriccion(new Restriccion("Celiasis", i2));
		Usuario usuario5 = new UsuarioAdministrador("Matias", "matias@hotmail.com", 20);
		usuario5.setPassword("123456");
		session.saveOrUpdate(usuario1);
		session.saveOrUpdate(usuario2);
		session.saveOrUpdate(usuario3);
		session.saveOrUpdate(usuario4);
		session.saveOrUpdate(usuario5);
		// Recetas
		ArrayList<Ingrediente> ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Vacio", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Asado", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Chorizo", TipoAlimento.CARNES));
		Receta receta1 = new Receta("Asado", ingredientes1, "...", Dificultad.MEDIA, Temporada.PRIMAVERA, 600);
		receta1.setAutor(usuario1);
		receta1.agregarTipoComida(TipoComida.ALMUERZO);
		receta1.agregarTipoComida(TipoComida.CENA);
		receta1.setVisibilidad(true);
		ArrayList<Ingrediente> ingredientes2 = new ArrayList<Ingrediente>();
		ingredientes2.add(new Ingrediente("Nalga", TipoAlimento.CARNES));
		ingredientes2.add(new Ingrediente("Arroz", TipoAlimento.PASTASPANARROZ));
		Receta receta2 = new Receta("Milanesas con arroz", ingredientes2, "...", Dificultad.MEDIA, Temporada.PRIMAVERA, 600);
		receta2.agregarTipoComida(TipoComida.ALMUERZO);
		receta2.agregarTipoComida(TipoComida.CENA);
		receta2.setAutor(usuario1);
		receta2.setVisibilidad(true);
		List<Ingrediente> ingredientes3 = new ArrayList<Ingrediente>();
		ingredientes3.add(new Ingrediente("Manzana", TipoAlimento.FRUTAS));
		ingredientes3.add(i1);
		Receta receta3 = new Receta("Torta de manzana", ingredientes3, "...", Dificultad.MEDIA, Temporada.VERANO, 300);
		receta3.agregarTipoComida(TipoComida.MERIENDA);
		receta3.agregarTipoComida(TipoComida.DESAYUNO);
		receta3.setAutor(usuario1);
		receta3.setVisibilidad(true);
		List<Ingrediente> ingredientes4 = new ArrayList<Ingrediente>();
		ingredientes4.add(new Ingrediente("Merluza", TipoAlimento.PESCADOS));
		ingredientes4.add(i2);
		Receta receta4 = new Receta("Filet de merluza", ingredientes4, "...", Dificultad.DIFICIL, Temporada.VERANO, 200);
		receta4.agregarTipoComida(TipoComida.ALMUERZO);
		receta4.agregarTipoComida(TipoComida.CENA);
		receta4.setAutor(usuario1);
		receta4.setVisibilidad(true);
		List<Ingrediente> ingredientes5 = new ArrayList<Ingrediente>();
		ingredientes5.add(new Ingrediente("Lechuga", TipoAlimento.VEGETALES));
		ingredientes5.add(new Ingrediente("Pan", TipoAlimento.PASTASPANARROZ));
		ingredientes5.add(new Ingrediente("Queso", TipoAlimento.LACTEOS));
		Receta receta5 = new Receta("Ensalada cesar", ingredientes5, "...", Dificultad.FACIL, Temporada.VERANO, 200);
		receta5.agregarTipoComida(TipoComida.ALMUERZO);
		receta5.agregarTipoComida(TipoComida.CENA);
		receta5.setAutor(usuario1);
		receta5.setVisibilidad(true);
		session.saveOrUpdate(receta1);
		session.saveOrUpdate(receta2);
		session.saveOrUpdate(receta3);
		session.saveOrUpdate(receta4);
		session.saveOrUpdate(receta5);
		// Grupos
		Grupo grupo1 = new Grupo("Futbol");
		Grupo grupo2 = new Grupo("Equipo basquet");
		Grupo grupo3 = new Grupo("Equipo volley");
		Grupo grupo4 = new Grupo("Yoga");
		session.saveOrUpdate(grupo1);
		session.saveOrUpdate(grupo2);
		session.saveOrUpdate(grupo3);
		session.saveOrUpdate(grupo4);
		// Grupos-Usuarios
		usuario1.unirAlGrupo(grupo1);
		usuario2.unirAlGrupo(grupo1);
		usuario2.unirAlGrupo(grupo2);
		usuario3.unirAlGrupo(grupo3);
		usuario4.unirAlGrupo(grupo4);
		usuario5.unirAlGrupo(grupo4);
		session.saveOrUpdate(usuario1);
		session.saveOrUpdate(usuario2);
		session.saveOrUpdate(usuario3);
		session.saveOrUpdate(usuario4);
		session.saveOrUpdate(usuario5);
		// Grupos-Recetas
		usuario1.compartirReceta(grupo1, receta1);
		usuario1.compartirReceta(grupo1, receta2);
		usuario1.calificarReceta(grupo1, receta1, 2);
		usuario1.calificarReceta(grupo1, receta2, 5);
		session.saveOrUpdate(grupo1);
		// Planificaciones
		Calendar fecha1 = Calendar.getInstance();
		Calendar fecha2 = Calendar.getInstance();
		fecha2.add(Calendar.DAY_OF_MONTH, 1);
		Planificacion planificacion1 = new Planificacion(fecha1, TipoComida.ALMUERZO, receta1);
		Planificacion planificacion2 = new Planificacion(fecha1, TipoComida.CENA, receta1);
		Planificacion planificacion3 = new Planificacion(fecha1, TipoComida.DESAYUNO, receta3);
		Planificacion planificacion4 = new Planificacion(fecha1, TipoComida.MERIENDA, receta3);
		Planificacion planificacion5 = new Planificacion(fecha2, TipoComida.CENA, receta2);
		Planificacion planificacion6 = new Planificacion(fecha1, TipoComida.ALMUERZO, receta4);
		Planificacion planificacion7 = new Planificacion(fecha1, TipoComida.CENA, receta4);
		Planificacion planificacion8 = new Planificacion(fecha2, TipoComida.DESAYUNO, receta3);
		Planificacion planificacion9 = new Planificacion(fecha2, TipoComida.MERIENDA, receta3);
		Planificacion planificacion10 = new Planificacion(fecha2, TipoComida.CENA, receta2);
		usuario1.agregarPlanificacion(planificacion1);
		usuario1.agregarPlanificacion(planificacion2);
		usuario1.agregarPlanificacion(planificacion3);
		usuario1.agregarPlanificacion(planificacion4);
		usuario1.agregarPlanificacion(planificacion5);
		usuario2.agregarPlanificacion(planificacion6);
		usuario2.agregarPlanificacion(planificacion7);
		usuario2.agregarPlanificacion(planificacion8);
		usuario2.agregarPlanificacion(planificacion9);
		usuario2.agregarPlanificacion(planificacion10);
		session.saveOrUpdate(usuario1);
		session.saveOrUpdate(usuario2);
		session.saveOrUpdate(planificacion1);
		session.saveOrUpdate(planificacion2);
		session.saveOrUpdate(planificacion3);
		session.saveOrUpdate(planificacion4);
		session.saveOrUpdate(planificacion5);
		session.saveOrUpdate(planificacion6);
		session.saveOrUpdate(planificacion7);
		session.saveOrUpdate(planificacion8);
		session.saveOrUpdate(planificacion9);
		session.saveOrUpdate(planificacion10);
		// Piramide alimenticia
		PiramideAlimenticia piramide1 = new PiramideAlimenticia();
		piramide1.agregarComidaDiaria(TipoAlimento.LACTEOS, 1);
		piramide1.agregarComidaSemanal(TipoAlimento.PASTASPANARROZ, 2);
		piramide1.agregarComidaSemanal(TipoAlimento.PESCADOS, 3);
		piramide1.agregarComidaSemanal(TipoAlimento.FRUTOSSECOS, 2);
		piramide1.agregarComidaSemanal(TipoAlimento.CARNES, 4);
		piramide1.agregarComidaSemanal(TipoAlimento.VEGETALES, 1);
		PiramideAlimenticia piramide2 = new PiramideAlimenticia();
		piramide2.agregarComidaDiaria(TipoAlimento.FRUTAS, 1);
		piramide2.agregarComidaSemanal(TipoAlimento.PESCADOS, 4);
		piramide2.agregarComidaSemanal(TipoAlimento.FRUTOSSECOS, 5);
		piramide2.agregarComidaSemanal(TipoAlimento.VEGETALES, 3);
		piramide2.agregarComidaSemanal(TipoAlimento.CARNES, 1);
		usuario1.setPiramide(piramide1);
		usuario2.setPiramide(piramide1);
		usuario3.setPiramide(piramide2);
		session.saveOrUpdate(usuario1);
		session.saveOrUpdate(usuario2);
		session.saveOrUpdate(usuario3);
		tx.commit();
		session.close();
	}
}