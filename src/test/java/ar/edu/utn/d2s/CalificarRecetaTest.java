package ar.edu.utn.d2s;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class CalificarRecetaTest {

	private Usuario usuario1, usuario2;
	private Grupo grupo1, grupo2;
	private Receta receta1, receta2;
	private List<Ingrediente> ingredientes1, ingredientes2;
	private List<Integer> lista1;

	@Before
	public void inicio() throws ExceptionValidacionDatos, ExceptionYaExiste, ExceptionIngredienteRestringido, ExceptionUsuarioNoPertenceAlGrupo {
		usuario1 = new UsuarioComun("Martin", "martin@gmail.com", 22);
		usuario2 = new UsuarioComun("Juan", "juan@hotmail.com", 24);
		grupo1 = new Grupo("Amigos");
		grupo2 = new Grupo("Carnivoros");
		ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Vacio", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Asado", TipoAlimento.VEGETALES));
		receta1 = new Receta("Asado", ingredientes1, "...", Dificultad.MEDIA, Temporada.PRIMAVERA, 600);
		receta1.setAutor(usuario1);
		ingredientes2 = new ArrayList<Ingrediente>();
		ingredientes2.add(new Ingrediente("Lechuga", TipoAlimento.VEGETALES));
		ingredientes2.add(new Ingrediente("Pan", TipoAlimento.PASTASPANARROZ));
		receta2 = new Receta("Ensalada cesar", ingredientes2, "...", Dificultad.FACIL, Temporada.VERANO, 200);
		receta2.setAutor(usuario2);
		usuario1.unirAlGrupo(grupo1);
		usuario1.compartirReceta(grupo1, receta1);
	};

	// Entrega 3
	@Test(expected = ExceptionUsuarioNoPertenceAlGrupo.class)
	public void testNoPermiticarCalificarRecetaAUsuarioQueNoPertenezcaAlGrupo() throws ExceptionValidacionDatos, ExceptionYaExiste, ExceptionUsuarioNoPertenceAlGrupo {
		usuario2.compartirReceta(grupo1, receta2);
		usuario2.calificarReceta(grupo1, receta2, 5);

		assertEquals("La calificación deberia ser 0",
				0, grupo1.getCalificacion(usuario2, receta2));

		usuario2.unirAlGrupo(grupo1);
		usuario2.compartirReceta(grupo1, receta2);
		usuario2.calificarReceta(grupo1, receta2, 5);

		assertEquals("La calificación deberia ser 5",
				5, grupo1.getCalificacion(usuario2, receta2));
	};

	@Test
	public void testCalificacionVisibleParaMiembrosDelGrupo() throws ExceptionValidacionDatos, ExceptionYaExiste, ExceptionUsuarioNoPertenceAlGrupo {
		usuario1.calificarReceta(grupo1, receta1, 5);
		lista1 = new ArrayList<Integer>();
		lista1.add(5);
		
		assertEquals("La lista para el usuario 1 debería tener su calificacion",
				1, grupo1.getListaCalificaciones(usuario1, receta1).size());
		assertEquals("La lista para el usuario 2 no deberia tener la calificacion por que no pertenece al grupo",
				0, grupo1.getListaCalificaciones(usuario2, receta1).size());
		assertEquals("La lista para el usuario 1 deberia contener un cinco",
				lista1, grupo1.getListaCalificaciones(usuario1, receta1));
		
		usuario2.unirAlGrupo(grupo1);
		
		assertEquals("La lista para el usuario 2 ahora si deberia tener la calificacion",
				1, grupo1.getListaCalificaciones(usuario2, receta1).size());
	};

	@Test
	public void testModificarCalificacion() throws ExceptionValidacionDatos, ExceptionUsuarioNoPertenceAlGrupo {
		usuario1.calificarReceta(grupo1, receta1, 5);

		assertEquals("La calificación del usuario 1 deberia ser cinco",
				5, grupo1.getCalificacion(usuario1, receta1));

		usuario1.modificarCalificacion(grupo1, receta1, 3);
		
		assertEquals("La calificación del usuario 1 debería ser tres",
				3, grupo1.getCalificacion(usuario1, receta1));
	};

	@Test(expected = ExceptionValidacionDatos.class)
	public void testNoSePuedeCalificarMasDeUnaVezUnaReceta() throws ExceptionValidacionDatos, ExceptionUsuarioNoPertenceAlGrupo {
		usuario1.calificarReceta(grupo1, receta1, 5);
		usuario1.calificarReceta(grupo1, receta1, 3);
		
		assertEquals("La lista debería tener 1 sóla calificación",
				1, grupo1.getListaCalificaciones(usuario1, receta1).size());
		assertEquals("La calificación debería ser la primera, es decir, un 5",
				5, grupo1.getCalificacion(usuario1, receta1));
	};

	@Test
	public void testRankingRecetasPorGrupo() throws ExceptionValidacionDatos, ExceptionYaExiste, ExceptionUsuarioNoPertenceAlGrupo {
		usuario2.unirAlGrupo(grupo1);
		usuario2.compartirReceta(grupo1, receta2);
		usuario1.calificarReceta(grupo1, receta1, 1);
		usuario2.calificarReceta(grupo1, receta1, 3);
		usuario1.calificarReceta(grupo1, receta2, 3);
		usuario2.calificarReceta(grupo1, receta2, 5);
		// El ranking de recetas deberia ordenar las recetas de mayor a menor
		// de acuerdo al promedio de estas, y no al orden al que fueron agregadas
		Map<Receta, Double> mapRanking = new LinkedHashMap<Receta, Double>();
		mapRanking.put(receta2, (double) 4);
		mapRanking.put(receta1, (double) 2);

		assertEquals("Los maps deberían ser iguales",
				mapRanking, grupo1.getRankingReceta());
	};

	@Test
	public void testRecetasEnDistintosGruposConCalificacionesSinMezclar() throws ExceptionValidacionDatos, ExceptionYaExiste, ExceptionUsuarioNoPertenceAlGrupo {
		usuario1.unirAlGrupo(grupo2);
		usuario1.compartirReceta(grupo2, receta1);
		usuario1.calificarReceta(grupo1, receta1, 4);
		usuario1.calificarReceta(grupo2, receta1, 5);
		
		assertEquals("La calificacion del usuario 1 y grupo 1 debería ser 4",
				4, grupo1.getCalificacion(usuario1, receta1));
		assertEquals("La calificacion del usuario 1 y grupo 2 debería ser 5",
				5, grupo2.getCalificacion(usuario1, receta1));
	};
}
