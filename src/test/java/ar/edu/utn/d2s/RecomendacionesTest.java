package ar.edu.utn.d2s;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionPlanificacionSuperior7Dias;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaHorarioNoCorresponde;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class RecomendacionesTest {

	private RepositorioRecetas rRecetas = RepositorioRecetas.getInstance();
	private RepositorioGrupos rGrupos = RepositorioGrupos.getInstance();
	private Usuario usuario1, usuario2;
	private List<Ingrediente> ingredientes1, ingredientes2, ingredientes3, ingredientes4, ingredientes5;
	private Receta receta1, receta2, receta3, receta4, receta5;
	private Grupo grupo1;
	private Calendar fecha1;
	private Planificacion planificacion1, planificacion2, planificacion3;
	private PiramideAlimenticia piramide1 = new PiramideAlimenticia();
	private RecomendacionMejorPuntaje rPuntaje = new RecomendacionMejorPuntaje();
	private RecomendacionBalanceo rBalanceo = new RecomendacionBalanceo();

	@Before
	public void inicio() throws ExceptionValidacionDatos, ExceptionYaExiste, ExceptionIngredienteRestringido, ExceptionUsuarioNoPertenceAlGrupo {
		usuario1 = new UsuarioComun("juan", "juan@hotmail.com", 20);
		usuario2 = new UsuarioComun("maria", "maria@hotmail.com", 21);
		usuario1.agregarPreferencia(new Ingrediente("Azucar", TipoAlimento.DULCES));
		grupo1 = new Grupo("Teatro");
		usuario1.unirAlGrupo(grupo1);
		ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Vacio", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Asado", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Chorizo", TipoAlimento.CARNES));
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
		receta2.setAutor(usuario1);
		ingredientes3 = new ArrayList<Ingrediente>();
		ingredientes3.add(new Ingrediente("Leche", TipoAlimento.LACTEOS));
		ingredientes3.add(new Ingrediente("Harina", TipoAlimento.PASTASPANARROZ));
		ingredientes3.add(new Ingrediente("Azucar", TipoAlimento.DULCES));
		receta3 = new Receta("Flan", ingredientes3, "...", Dificultad.FACIL, Temporada.VERANO, 200);
		receta3.agregarTipoComida(TipoComida.MERIENDA);
		receta3.setAutor(usuario2);
		ingredientes4 = new ArrayList<Ingrediente>();
		ingredientes4.add(new Ingrediente("Pollo", TipoAlimento.CARNES));
		ingredientes4.add(new Ingrediente("Papas", TipoAlimento.VEGETALES));
		receta4 = new Receta("Pollo al horno", ingredientes4, "...", Dificultad.DIFICIL, Temporada.INVIERNO, 200);
		receta4.agregarTipoComida(TipoComida.ALMUERZO);
		receta4.agregarTipoComida(TipoComida.CENA);
		receta4.setAutor(usuario2);
		ingredientes5 = new ArrayList<Ingrediente>();
		ingredientes5.add(new Ingrediente("Lechuga", TipoAlimento.VEGETALES));
		ingredientes5.add(new Ingrediente("Pan", TipoAlimento.PASTASPANARROZ));
		receta5 = new Receta("Ensalada cesar", ingredientes5, "...", Dificultad.FACIL, Temporada.VERANO, 200);
		receta5.agregarTipoComida(TipoComida.CENA);
		receta5.setAutor(usuario2);
		rRecetas.resetearRepositorio();
		rRecetas.agregarReceta(receta1);
		rRecetas.agregarReceta(receta2);
		rRecetas.agregarReceta(receta3);
		rRecetas.agregarReceta(receta4);
		rRecetas.agregarReceta(receta5);
		rGrupos.resetearRepositorio();
		rGrupos.agregarGrupo(grupo1);
		usuario1.compartirReceta(grupo1, receta1);
		usuario1.calificarReceta(grupo1, receta1, 3);
		usuario1.compartirReceta(grupo1, receta2);
		usuario1.calificarReceta(grupo1, receta2, 1);
		piramide1.agregarComidaDiaria(TipoAlimento.FRUTAS, 1);
		piramide1.agregarComidaDiaria(TipoAlimento.LACTEOS, 1);
		piramide1.agregarComidaSemanal(TipoAlimento.PASTASPANARROZ, 2);
		piramide1.agregarComidaSemanal(TipoAlimento.PESCADOS, 3);
		piramide1.agregarComidaSemanal(TipoAlimento.FRUTOSSECOS, 2);
		piramide1.agregarComidaSemanal(TipoAlimento.CARNES, 4);
		piramide1.agregarComidaSemanal(TipoAlimento.VEGETALES, 1);
		usuario1.setPiramide(piramide1);
	}

	@Test
	public void testPorDiaHorario() {
		Map<Receta, Double> mapRecomendaciones = new LinkedHashMap<Receta, Double>();
		// La receta1 tiene una calificacion de 3 en el grupo1 pero no es una merienda
		// por lo tanto no debería recomendarla.
		// La receta2 tiene una calificacion de 1 en el grupo1 y tiene 1 ingrediente
		// que coincide con las preferencias del usuario1
		mapRecomendaciones.put(receta2, (double) 2);
		// La receta3 tiene 1 ingrediente que coincide con las preferencias del usuario1
		mapRecomendaciones.put(receta3, (double) 1);
		
		assertEquals("Los maps deberían ser iguales",
				mapRecomendaciones, rPuntaje.recomendar(usuario1, TipoComida.MERIENDA, rRecetas.getRecetas(), rGrupos.getGrupos()));
	}

	@Test
	public void testPorBalanceo() throws ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde, ExceptionIngredienteRestringido {
		fecha1 = Calendar.getInstance();
		planificacion1 = new Planificacion(fecha1, TipoComida.ALMUERZO, receta1);
		planificacion2 = new Planificacion(fecha1, TipoComida.CENA, receta1);
		planificacion3 = new Planificacion(fecha1, TipoComida.MERIENDA, receta2);
		usuario1.agregarPlanificacion(planificacion1);
		usuario1.agregarPlanificacion(planificacion2);
		usuario1.agregarPlanificacion(planificacion3);
		assertEquals("El repositorio deberia tener 5 recetas",
				5, rRecetas.getRecetas().size());
		Map<Receta, Double> mapRecomendaciones1 = new LinkedHashMap<Receta, Double>();
		// La receta2 y receta3 no tienen el tipo de comida Cena, por lo tanto no debería mostrarlas
		// La receta5 tiene 1 vegetal y 1 pan (pastaspanarroz), ambos están en la pirámide
		// Y el usuario no tiene planificaciones (de esos ingredientes), por lo tanto suman 1 punto cada uno (2)
		mapRecomendaciones1.put(receta5, (double) 2);
		// La receta4 tiene 1 carne y 1 vegetal, la carne no debería contarla por que ya
		// completó con respecto a la pirámide, y el vegetal le da un puntaje de 1
		mapRecomendaciones1.put(receta4, (double) 1);
		// La receta1 tiene 3 carnes, y el usuario tiene planificaciones donde tiene 6 carnes
		// y su piramide tiene 4 carnes, por lo tanto el puntaje debe ser 0
		mapRecomendaciones1.put(receta1, (double) 0);
		assertEquals("Los maps deberían ser iguales",
				mapRecomendaciones1, rBalanceo.recomendar(usuario1, TipoComida.CENA, rRecetas.getRecetas(), rGrupos.getGrupos()));

		Map<Receta, Double> mapRecomendaciones2 = new LinkedHashMap<Receta, Double>();
		// La receta2 tiene 1 harina (pastaspanarroz) como comida semanal y 1 fruta como diaria
		// por lo tanto debería tener un puntaje de 2
		mapRecomendaciones2.put(receta2, (double) 2);
		// La receta3 tiene 1 lacteo como comida diaria y 1 harina, por lo tanto
		// su puntaje debería ser 2
		mapRecomendaciones2.put(receta3, (double) 2);
		assertEquals("Los maps deberían ser iguales",
				mapRecomendaciones2, rBalanceo.recomendar(usuario1, TipoComida.MERIENDA, rRecetas.getRecetas(), rGrupos.getGrupos()));
	}
}
