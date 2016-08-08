package ar.edu.utn.d2s;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.edu.utn.d2s.exceptions.ExceptionGrupoInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionPlanificacionSuperior7Dias;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaHorarioNoCorresponde;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class ReportesTest {

	private RepositorioGrupos repositorioGrupos;
	private RepositorioUsuarios repositorioUsuarios;
	private RepositorioRecetas repositorioRecetas;
	private UsuarioComun usuario1, usuario2;
	private ArrayList<Ingrediente> ingredientes1, ingredientes2;
	private Receta receta1, receta2, receta1bis;
	private Grupo grupo1;
	private Calendar fecha1, fecha2, fecha3;
	private Planificacion planificacion1, planificacion2, planificacion3;

	@Before
	public void inicio() throws ExceptionValidacionDatos, ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde, CloneNotSupportedException, ExceptionYaExiste, ExceptionIngredienteRestringido, ExceptionUsuarioNoPertenceAlGrupo {
		repositorioGrupos = RepositorioGrupos.getInstance();
		repositorioUsuarios = RepositorioUsuarios.getInstance();
		repositorioRecetas = RepositorioRecetas.getInstance();
		repositorioGrupos.resetearRepositorio();
		repositorioUsuarios.resetearRepositorio();
		repositorioRecetas.resetearRepositorio();
		usuario1 = new UsuarioComun("juan", "juan@hotmail.com", 20);
		usuario2 = new UsuarioComun("maria", "maria@hotmail.com", 21);
		ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Vacio", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Asado", TipoAlimento.VEGETALES));
		receta1 = new Receta("Asado", ingredientes1, "...", Dificultad.MEDIA, Temporada.PRIMAVERA, 600);
		receta1.agregarTipoComida(TipoComida.ALMUERZO);
		receta1.agregarTipoComida(TipoComida.CENA);
		receta1.setAutor(usuario1);
		ingredientes2 = new ArrayList<Ingrediente>();
		ingredientes2.add(new Ingrediente("Harina", TipoAlimento.PASTASPANARROZ));
		ingredientes2.add(new Ingrediente("Manzana", TipoAlimento.FRUTAS));
		ingredientes2.add(new Ingrediente("Azucar", TipoAlimento.DULCES));
		receta2 = new Receta("Torta de manzana", ingredientes2, "...", Dificultad.MEDIA, Temporada.OTONIO, 300);
		receta2.agregarTipoComida(TipoComida.MERIENDA);
		receta2.setAutor(usuario2);
		grupo1 = new Grupo("Teatro");
		usuario1.unirAlGrupo(grupo1);
		usuario2.unirAlGrupo(grupo1);
		usuario1.compartirReceta(grupo1, receta1);
		receta1bis = usuario2.generarRecetaAPartirDeOtra(receta1);
		repositorioGrupos.agregarGrupo(grupo1);
		repositorioRecetas.agregarReceta(receta1);
		repositorioRecetas.agregarReceta(receta2);
		repositorioRecetas.agregarReceta(receta1bis);
		repositorioUsuarios.agregarUsuario(usuario1);
		repositorioUsuarios.agregarUsuario(usuario2);
		fecha1 = Calendar.getInstance();
		planificacion1 = new Planificacion(fecha1, TipoComida.ALMUERZO, receta1);
		planificacion2 = new Planificacion(fecha1, TipoComida.MERIENDA, receta2);
		planificacion3 = new Planificacion(fecha1, TipoComida.CENA, receta1);
		usuario1.agregarPlanificacion(planificacion1);
		usuario1.agregarPlanificacion(planificacion2);
		usuario2.agregarPlanificacion(planificacion3);
		usuario1.configurarRepositorios(repositorioGrupos.getGrupos(), repositorioUsuarios.getUsuarios(), repositorioRecetas.getRecetas());
	};

	// Entrega 4
	@Test
	public void testReportePorPeriodo() throws ExceptionYaExiste, ExceptionGrupoInexistente {
		fecha2 = Calendar.getInstance();
		fecha2.add(Calendar.DAY_OF_MONTH, -1);
		fecha3 = Calendar.getInstance();
		fecha3.add(Calendar.DAY_OF_MONTH, 1);
		FiltroPeriodo filtro = new FiltroPeriodo(fecha2, fecha3);
		usuario1.agregarFiltroReporte(filtro);
		List<Receta> listaRecetas = new ArrayList<Receta>();
		// 2 recetas planificadas del usuario1, 1 receta del usuario2
		listaRecetas.add(receta1);
		listaRecetas.add(receta2);
		listaRecetas.add(receta1);

		assertEquals("Las listas deberían ser iguales",
				listaRecetas, usuario1.getReporte());
	};

	@Test
	public void testReportePorIngrediente() throws ExceptionYaExiste, ExceptionGrupoInexistente {
		FiltroIngrediente filtro = new FiltroIngrediente(new Ingrediente("Azucar", TipoAlimento.DULCES));
		usuario1.agregarFiltroReporte(filtro);
		List<Receta> listaRecetas = new ArrayList<Receta>();
		listaRecetas.add(receta2);

		assertEquals("Las listas deberían ser iguales",
				listaRecetas, usuario1.getReporte());
	};

	@Test
	public void testReportePorCalorias() throws ExceptionYaExiste, ExceptionGrupoInexistente {
		FiltroCalorias filtro = new FiltroCalorias(200, 400);
		usuario1.agregarFiltroReporte(filtro);
		List<Receta> listaRecetas = new ArrayList<Receta>();
		listaRecetas.add(receta2);

		assertEquals("Las listas deberían ser iguales",
				listaRecetas, usuario1.getReporte());
	};

	@Test
	public void testReportePorNombre() throws ExceptionYaExiste, ExceptionGrupoInexistente {
		FiltroNombre filtro = new FiltroNombre("Asado");
		usuario1.agregarFiltroReporte(filtro);
		List<Receta> listaRecetas = new ArrayList<Receta>();
		// La receta 'Asado' del usuario1 y la receta 'Asado' del usuario2
		listaRecetas.add(receta1);
		listaRecetas.add(receta1bis);

		assertEquals("Las listas deberían ser iguales",
				listaRecetas, usuario1.getReporte());
	};

	@Test
	public void testReportePorGrupo() throws ExceptionYaExiste, ExceptionGrupoInexistente {
		FiltroGrupo filtro = new FiltroGrupo("Teatro");
		usuario1.agregarFiltroReporte(filtro);
		List<Receta> listaRecetas = new ArrayList<Receta>();
		listaRecetas.add(receta1);

		assertEquals("Las listas deberían ser iguales",
				listaRecetas, usuario1.getReporte());
	};

	@Test
	public void testReportePorUsuario() throws ExceptionYaExiste, ExceptionGrupoInexistente {
		FiltroUsuario filtro = new FiltroUsuario(usuario1);
		usuario1.agregarFiltroReporte(filtro);
		List<Receta> listaRecetas = new ArrayList<Receta>();
		listaRecetas.add(receta1);

		assertEquals("Las listas deberían ser iguales",
				listaRecetas, usuario1.getReporte());
	};

	@Test
	public void testReporteVariosFiltros() throws ExceptionYaExiste, ExceptionGrupoInexistente {
		FiltroNombre filtro1 = new FiltroNombre("Asado");
		FiltroUsuario filtro2 = new FiltroUsuario(usuario2);
		usuario1.agregarFiltroReporte(filtro1);
		usuario1.agregarFiltroReporte(filtro2);
		List<Receta> listaRecetas = new ArrayList<Receta>();
		listaRecetas.add(receta1bis);
		// Se filtran las recetas 'Asado' y que sean del usuario2
		assertEquals("Las listas deberían ser iguales",
				listaRecetas, usuario1.getReporte());
	};

	@Test
	public void testReporteRecetasNuevas() throws ExceptionYaExiste, ExceptionGrupoInexistente {
		FiltroRecetasNuevas filtro = new FiltroRecetasNuevas(usuario1);
		usuario1.agregarFiltroReporte(filtro);
		List<Receta> listaRecetas = new ArrayList<Receta>();
		listaRecetas.add(receta1);
		// Debería traer las recetas de los grupos a los que pertenece
		assertEquals("Las listas deberían ser iguales",
				listaRecetas, usuario1.getReporte());
	};
}