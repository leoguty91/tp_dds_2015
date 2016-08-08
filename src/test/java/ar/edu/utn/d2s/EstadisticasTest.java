package ar.edu.utn.d2s;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionPlanificacionSuperior7Dias;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaHorarioNoCorresponde;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class EstadisticasTest {

	private RepositorioGrupos repositorioGrupos;
	private RepositorioUsuarios repositorioUsuarios;
	private RepositorioRecetas repositorioRecetas;
	private UsuarioComun usuario1, usuario2;
	private UsuarioAdministrador usuario3;
	private List<Ingrediente> ingredientes1, ingredientes2, ingredientes3;
	private Receta receta1, receta2, receta3, receta1bis;
	private Grupo grupo1;
	private Calendar fecha1;
	private Planificacion planificacion1, planificacion2, planificacion3;

	@Before
	public void inicio() throws ExceptionValidacionDatos, CloneNotSupportedException, ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde, ExceptionYaExiste, ExceptionIngredienteRestringido, ExceptionUsuarioNoPertenceAlGrupo {
		repositorioGrupos = RepositorioGrupos.getInstance();
		repositorioUsuarios = RepositorioUsuarios.getInstance();
		repositorioRecetas = RepositorioRecetas.getInstance();
		repositorioGrupos.resetearRepositorio();
		repositorioUsuarios.resetearRepositorio();
		repositorioRecetas.resetearRepositorio();
		usuario1 = new UsuarioComun("juan", "juan@hotmail.com", 20);
		usuario2 = new UsuarioComun("maria", "maria@hotmail.com", 21);
		usuario3 = new UsuarioAdministrador("leo", "leo@hotmail.com", 24);
		ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Vacio", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Asado", TipoAlimento.CARNES));
		receta1 = new Receta("Asado", ingredientes1, "...", Dificultad.MEDIA, Temporada.PRIMAVERA, 600);
		receta1.agregarTipoComida(TipoComida.ALMUERZO);
		receta1.agregarTipoComida(TipoComida.CENA);
		receta1.setAutor(usuario1);
		ingredientes2 = new ArrayList<Ingrediente>();
		ingredientes2.add(new Ingrediente("Harina", TipoAlimento.PASTASPANARROZ));
		ingredientes2.add(new Ingrediente("Manzana", TipoAlimento.FRUTAS));
		ingredientes2.add(new Ingrediente("Azucar", TipoAlimento.DULCES));
		receta2 = new Receta("Torta de manzana", ingredientes2, "...", Dificultad.MEDIA, Temporada.VERANO, 300);
		receta2.agregarTipoComida(TipoComida.MERIENDA);
		receta2.agregarTipoComida(TipoComida.DESAYUNO);
		receta2.setAutor(usuario1);
		ingredientes3 = new ArrayList<Ingrediente>();
		ingredientes3.add(new Ingrediente("Pollo", TipoAlimento.CARNES));
		ingredientes3.add(new Ingrediente("Papas", TipoAlimento.CARNES));
		receta3 = new Receta("Pollo al horno con papas", ingredientes3, "...", Dificultad.DIFICIL, Temporada.VERANO, 200);
		receta3.agregarTipoComida(TipoComida.ALMUERZO);
		receta3.setAutor(usuario1);
		grupo1 = new Grupo("Teatro");
		usuario1.unirAlGrupo(grupo1);
		usuario2.unirAlGrupo(grupo1);
		usuario1.compartirReceta(grupo1, receta1);
		receta1bis = usuario2.generarRecetaAPartirDeOtra(receta1);
		repositorioRecetas.agregarReceta(receta1);
		repositorioRecetas.agregarReceta(receta2);
		repositorioRecetas.agregarReceta(receta3);
		repositorioRecetas.agregarReceta(receta1bis);
		repositorioUsuarios.agregarUsuario(usuario1);
		repositorioUsuarios.agregarUsuario(usuario2);
		repositorioUsuarios.agregarUsuario(usuario3);
		fecha1 = Calendar.getInstance();
		planificacion1 = new Planificacion(fecha1, TipoComida.ALMUERZO, receta1);
		planificacion2 = new Planificacion(fecha1, TipoComida.MERIENDA, receta2);
		planificacion3 = new Planificacion(fecha1, TipoComida.CENA, receta1);
		usuario1.agregarPlanificacion(planificacion1);
		usuario1.agregarPlanificacion(planificacion2);
		usuario2.agregarPlanificacion(planificacion3);
		usuario3.configurarRepositorios(repositorioGrupos.getGrupos(), repositorioUsuarios.getUsuarios(), repositorioRecetas.getRecetas());
	};

	//@Ignore // Recordar poner el mes / semana en curso para este test
	@Test
	public void testEstadisticasPorEstacionYSemana() throws ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde, ExceptionIngredienteRestringido {
		Planificacion planifica1 = new Planificacion(fecha1, TipoComida.DESAYUNO, receta2);
		usuario1.agregarPlanificacion(planifica1);
		Planificacion planifica2 = new Planificacion(fecha1, TipoComida.DESAYUNO, receta2);
		usuario2.agregarPlanificacion(planifica2);
		Planificacion planifica3 = new Planificacion(fecha1, TipoComida.ALMUERZO, receta3);
		usuario1.agregarPlanificacion(planifica3);
		Map<Receta, Long> mapEstadisticas = new HashMap<Receta, Long>();
		mapEstadisticas.put(receta3, (long) 1);
		mapEstadisticas.put(receta2, (long) 3);
		// La receta2 y receta3 son del tipo Verano
		// el usuario1 tiene 3 planificaciones(2 receta2 y 1 receta3)
		// el usuario2 tiene 1 planificacion(1 receta2)
		assertEquals("El map de Estadisticas tiene que ser igual",
				mapEstadisticas, usuario3.getEstadisticasPorEstacionYSemana(Mes.FEBRERO, Semana.TERCERA, Temporada.VERANO));
	};

	//@Ignore // Recordar poner el mes en curso para este test
	@Test
	public void testEstadisticasPorEstacionYMes() throws ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde, ExceptionIngredienteRestringido {
		Planificacion planifica1 = new Planificacion(fecha1, TipoComida.ALMUERZO, receta1bis);
		usuario2.agregarPlanificacion(planifica1);
		Map<Receta, Long> mapEstadisticas = new HashMap<Receta, Long>();
		mapEstadisticas.put(receta1, (long) 2);
		mapEstadisticas.put(receta1bis, (long) 1);
		// Las receta1 y receta1bis son del tipo Primavera
		// El usuario1 tiene 1 planificacion (receta1)
		// El usuario2 tiene 2 planificaciones (receta1 y receta1bis)
		assertEquals("El map de Estadisticas tiene que ser igual",
				mapEstadisticas, usuario3.getEstadisticasPorEstacionYMes(Mes.FEBRERO, Temporada.PRIMAVERA));
	};

	@Test
	public void testEstadisticasRankingRecetasMasCopiadas() {
		Map<Receta, Long> mapEstadisticas = new HashMap<Receta, Long>();
		mapEstadisticas.put(receta1, (long) 1);
		// Solo la receta1 se copio
		assertEquals("El map de Estadisticas tiene que ser igual",
				mapEstadisticas, usuario3.getEstadisticasPorRecetasMasCopiadas());

	};
}
