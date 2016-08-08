package ar.edu.utn.d2s;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionPlanificacionSuperior7Dias;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaHorarioNoCorresponde;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class PlanificacionTest {
	
	private Calendar fecha1, fecha2;
	private Receta receta1, receta2, receta3, receta4;
	private Planificacion planificacion1, planificacion2;
	private Usuario usuario1, usuario2;
	private RepositorioRecetas repositorioRecetas;
	private Grupo grupo;
	private ArrayList<Ingrediente> ingredientes1, ingredientes2, ingredientes3, ingredientes4;

	@Before
	public void inicio() throws ExceptionValidacionDatos, ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde, ExceptionYaExiste, ExceptionIngredienteRestringido, ExceptionUsuarioNoPertenceAlGrupo {
		usuario1 = new UsuarioComun("Juan", "juan@hotmail.com", 19);
		usuario2 = new UsuarioComun("Maria", "maria@hotmail.com", 20);
		grupo = new Grupo("Grupo Amigos");
		usuario1.unirAlGrupo(grupo);
		usuario2.unirAlGrupo(grupo);
		usuario1.agregarRestriccion(new Restriccion("Diabetes", new Ingrediente("Azucar")));
		fecha1 = Calendar.getInstance();
		ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Pollo", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Papas", TipoAlimento.CARNES));
		receta1 = new Receta("Pollo al horno con papas", ingredientes1, "...", Dificultad.DIFICIL, Temporada.INVIERNO, 200);
		receta1.agregarTipoComida(TipoComida.ALMUERZO);
		receta1.agregarTipoComida(TipoComida.CENA);
		receta1.setAutor(usuario2);
		planificacion1 = new Planificacion(fecha1, TipoComida.CENA, receta1);
		usuario1.agregarPlanificacion(planificacion1);
		ingredientes2 = new ArrayList<Ingrediente>();
		ingredientes2.add(new Ingrediente("Lechuga", TipoAlimento.VEGETALES));
		ingredientes2.add(new Ingrediente("Pan", TipoAlimento.PASTASPANARROZ));
		receta2 = new Receta("Ensalada cesar", ingredientes2, "...", Dificultad.FACIL, Temporada.VERANO, 200);
		receta2.agregarTipoComida(TipoComida.CENA);
		receta2.setAutor(usuario2);
		ingredientes3 = new ArrayList<Ingrediente>();
		ingredientes3.add(new Ingrediente("Harina", TipoAlimento.PASTASPANARROZ));
		ingredientes3.add(new Ingrediente("Manzana", TipoAlimento.FRUTAS));
		ingredientes3.add(new Ingrediente("Azucar", TipoAlimento.DULCES));
		receta3 = new Receta("Torta de manzana", ingredientes3, "...", Dificultad.MEDIA, Temporada.OTONIO, 300);
		receta3.agregarTipoComida(TipoComida.MERIENDA);
		receta3.setAutor(usuario2);
		ingredientes4 = new ArrayList<Ingrediente>();
		ingredientes4.add(new Ingrediente("Vacio", TipoAlimento.CARNES));
		ingredientes4.add(new Ingrediente("Asado", TipoAlimento.VEGETALES));
		receta4 = new Receta("Asado", ingredientes4, "...", Dificultad.MEDIA, Temporada.PRIMAVERA, 600);
		receta4.setAutor(usuario2);
		usuario2.compartirReceta(grupo, receta4);
		repositorioRecetas = RepositorioRecetas.getInstance();
		repositorioRecetas.resetearRepositorio();
		repositorioRecetas.agregarReceta(receta1);
		repositorioRecetas.agregarReceta(receta3);
	}

	// Entrega 2
	@Test
	public void testConsultaPlanificacion() throws ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde, ExceptionValidacionDatos, ExceptionIngredienteRestringido {
		assertEquals("El usuario no deberia tener planificacion en el almuerzo",
				"Nada", usuario1.consultarPlanificacion(fecha1, TipoComida.ALMUERZO));

		planificacion2 = new Planificacion(fecha1, TipoComida.ALMUERZO, receta1);
		usuario1.agregarPlanificacion(planificacion2);
		
		assertEquals("El usuario deberia tener la receta planificada en el almuerzo",
				receta1.getNombre(), usuario1.consultarPlanificacion(fecha1, TipoComida.ALMUERZO));
	}

	@Test
	public void testConsultaReplanificacion() throws ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde, ExceptionValidacionDatos, ExceptionIngredienteRestringido {
		assertEquals("La receta en la planificacion para la cena deberia ser la receta1",
				receta1.getNombre(), usuario1.consultarPlanificacion(fecha1, TipoComida.CENA));
		
		planificacion2 = new Planificacion(fecha1, TipoComida.CENA, receta2);
		usuario1.agregarPlanificacion(planificacion2);
		
		assertEquals("La receta en la replanificacion deberia ser la receta2",
				receta2.getNombre(), usuario1.consultarPlanificacion(fecha1, TipoComida.CENA));
	}

	@Test(expected = ExceptionPlanificacionSuperior7Dias.class)
	public void testNoPlanificaMasDeUnaSemana() throws ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde {
		fecha2 = Calendar.getInstance();
		fecha2.add(Calendar.DAY_OF_MONTH, 9);
		planificacion2 = new Planificacion(fecha2, TipoComida.ALMUERZO, receta1);
	}

	@Test(expected = ExceptionRecetaHorarioNoCorresponde.class)
	public void testNoPlanificaRecetaHorarioNoCorrespondiente() throws ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde {
		this.planificacion2 = new Planificacion(fecha1, TipoComida.DESAYUNO, receta1);
	}

	@Test
	public void testListarRecetas() throws ExceptionYaExiste, ExceptionIngredienteRestringido {
		this.usuario1.agregarReceta(receta2);
		// 2 recetas en el repositorio - 1 por restriccion + 1 compartida en grupo + 1 perteneciente al propio usuario
		assertEquals("El usuario debería tener 3 recetas",
				3, usuario1.listarTodasLasRecetas(repositorioRecetas.getRecetas()).size());
	}
}