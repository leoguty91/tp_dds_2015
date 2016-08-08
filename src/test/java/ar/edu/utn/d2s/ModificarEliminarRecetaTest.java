package ar.edu.utn.d2s;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionPlanificacionSuperior7Dias;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaHorarioNoCorresponde;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaNoCompartidaEnGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class ModificarEliminarRecetaTest {

	private Usuario usuario1, usuario2;
	private Grupo grupo1;
	private Receta receta1, recetaObtenidaDelGrupo, recetaModificada;
	private List<Ingrediente> ingredientes1;
	private RepositorioUsuarios repositorioUsuario;
	private RepositorioRecetas repositorioRecetas;
	private Planificacion planificacion1, planificacion2, planificacion3;
	private Calendar fecha1;

	@Before
	public void inicio() throws ExceptionValidacionDatos, ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde, ExceptionYaExiste, ExceptionIngredienteRestringido, ExceptionUsuarioNoPertenceAlGrupo {
		repositorioUsuario = RepositorioUsuarios.getInstance();
		repositorioRecetas = RepositorioRecetas.getInstance();
		repositorioUsuario.resetearRepositorio();
		repositorioRecetas.resetearRepositorio();
		fecha1 = Calendar.getInstance();
		usuario1 = new UsuarioComun("Martin", "martin@gmail.com", 22);
		usuario2 = new UsuarioComun("Juan", "juan@hotmail.com", 24);
		grupo1 = new Grupo("Amigos");
		usuario1.unirAlGrupo(grupo1);
		usuario2.unirAlGrupo(grupo1);
		ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Vacio", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Asado", TipoAlimento.VEGETALES));
		receta1 = new Receta("Asado", ingredientes1, "...", Dificultad.MEDIA, Temporada.PRIMAVERA, 600);
		receta1.agregarTipoComida(TipoComida.ALMUERZO);
		receta1.agregarTipoComida(TipoComida.CENA);
		receta1.setAutor(usuario1);
		repositorioUsuario.agregarUsuario(usuario1);
		repositorioUsuario.agregarUsuario(usuario2);
		repositorioRecetas.agregarReceta(receta1);
		usuario1.compartirReceta(grupo1, receta1);
		recetaObtenidaDelGrupo = usuario2.listarTodasLasRecetas(repositorioRecetas.getRecetas()).stream().findFirst().get();
		planificacion1 = new Planificacion(fecha1, TipoComida.CENA, receta1);
		usuario1.agregarPlanificacion(this.planificacion1);
		planificacion2 = new Planificacion(fecha1, TipoComida.CENA, recetaObtenidaDelGrupo);
		usuario2.agregarPlanificacion(planificacion2);
	}

	// Entrega 3
	@Test
	public void testEliminarRecetaSinCambiarHistorialDeUsuarios() throws ExceptionValidacionDatos {
		assertEquals("El repositorio debería tener 1 receta",
				1, repositorioRecetas.getRecetas().size());
		assertTrue("El usuario 1 debería contener la receta",
				usuario1.contieneReceta(receta1));
		assertEquals("La planificación del usuario 2 debería ser Asado",
				"Asado", usuario2.consultarPlanificacion(fecha1, TipoComida.CENA));
		
		repositorioRecetas.eliminaReceta(receta1);
		
		assertEquals("El repositorio no debería tener recetas",
				0, repositorioRecetas.getRecetas().size());
		assertEquals("La planificacion del usuario 2 no debería haber cambiado si se elimna la receta",
				"Asado", usuario2.consultarPlanificacion(fecha1, TipoComida.CENA));
		assertTrue("El usuario 1 deberia contener la receta también",
				usuario1.contieneReceta(receta1));
	}

	@Test
	public void testBorrarUsuarioDeGrupoSinCambiarHistorialDeUsuarios() throws ExceptionValidacionDatos, ExceptionUsuarioNoPertenceAlGrupo {
		assertEquals("El grupo debería tener 2 usuarios",
				2, grupo1.getUsuarios().size());
		assertEquals("La planificación del usuario 2 debería ser Asado",
				"Asado", usuario2.consultarPlanificacion(fecha1, TipoComida.CENA));

		grupo1.eliminarUsuario(usuario1);
		
		assertEquals("El grupo debería tener 1 usuario",
				1, grupo1.getUsuarios().size());
		assertEquals("La planificacion del usuario 2 no debería haber cambiado si se elimna al usuario del grupo",
				"Asado", usuario2.consultarPlanificacion(fecha1, TipoComida.CENA));
	}
	
	@Test(expected = ExceptionYaExiste.class)
	public void testModificarRecetaSinCambiarHistorial() throws CloneNotSupportedException, ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde, ExceptionValidacionDatos, ExceptionYaExiste, ExceptionIngredienteRestringido, ExceptionRecetaNoCompartidaEnGrupo {
		recetaModificada = usuario1.modificarReceta(receta1, "Asado a las brasas", receta1.getIngredientes(), "...", receta1.getDificultad(), Temporada.VERANO, 400);
		recetaModificada.agregarTipoComida(TipoComida.ALMUERZO);
		planificacion3 = new Planificacion(fecha1, TipoComida.ALMUERZO, recetaModificada);
		usuario1.agregarPlanificacion(planificacion3);
		
		assertEquals("El usuario debería tener 1 receta, la modificada",
				1, usuario1.getRecetas().size());
		assertEquals("La primer receta deberia ser Asado a las brasas",
				"Asado a las brasas", usuario1.getRecetas().get(0).getNombre());
		assertEquals("La planificacion del almuerzo deberia ser Asado a las brasas",
				"Asado a las brasas", usuario1.consultarPlanificacion(fecha1, TipoComida.ALMUERZO));
		assertEquals("La planificacion para la cena no deberia haber cambiado si modifico la receta original y deberia ser Asado",
				"Asado", usuario1.consultarPlanificacion(fecha1, TipoComida.CENA));
	}
}