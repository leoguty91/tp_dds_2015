package ar.edu.utn.d2s;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class GenerarRecetaTest {

	private Usuario usuario1, usuario2;
	private Receta receta1, receta2;
	private RepositorioRecetas repositorioRecetas;
	private List<Ingrediente> ingredientes1;

	@Before
	public void inicio() throws ExceptionValidacionDatos, CloneNotSupportedException, ExceptionYaExiste, ExceptionIngredienteRestringido {
		repositorioRecetas = RepositorioRecetas.getInstance();
		repositorioRecetas.resetearRepositorio();
		usuario1 = new UsuarioComun("Juan", "juan@hotmail.com", 20);
		ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Vacio", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Asado", TipoAlimento.VEGETALES));
		receta1 = new Receta("Asado", ingredientes1, "...", Dificultad.MEDIA, Temporada.PRIMAVERA, 600);
		receta1.setAutor(usuario1);
		usuario2 = new UsuarioComun("Maria", "maria@hotmail.com", 25);
		receta2 = usuario2.generarRecetaAPartirDeOtra(receta1);
		repositorioRecetas.agregarReceta(receta1);
		repositorioRecetas.agregarReceta(receta2);
	}
	
	// Entrega 3
	@Test(expected = ExceptionYaExiste.class)
	public void testNoPermitirDosRecetasIguales() throws ExceptionValidacionDatos, ExceptionYaExiste {
		this.repositorioRecetas.agregarReceta(receta1);
		
		assertEquals("El repositorio deberia tener 2 recetas",
				2, repositorioRecetas.getRecetas().size());
	}

	@Test(expected = ExceptionValidacionDatos.class)
	public void testValidacionDatosObligatorioRecetaNombre() throws ExceptionValidacionDatos {
		receta1.setNombre("");
		
		assertEquals("El nombre deberia quedar igual al anterior si se setea un nombre vacio",
				"Asado", receta1.getNombre());
	}

	@Test(expected = ExceptionValidacionDatos.class)
	public void testValidacionDatosObligatorioRecetaIngredientes() throws ExceptionValidacionDatos {
		ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Vacio", TipoAlimento.CARNES));
		receta1 = new Receta("Asado", ingredientes1, "...", Dificultad.MEDIA, Temporada.PRIMAVERA, 600);
	}

	@Test(expected = ExceptionValidacionDatos.class)
	public void testValidacionDatosObligatorioRecetaProcedimiento() throws ExceptionValidacionDatos {
		receta1.setProcedimiento("");

		assertEquals("El procedimiento no deberia haber cambiado si es vacio",
				"...", receta1.getProcedimiento());
	}

	@Test(expected = ExceptionValidacionDatos.class)
	public void testValidacionDatosObligatorioRecetaCalorias() throws ExceptionValidacionDatos {
		receta1.setCalorias(0);

		assertEquals("Las calorias no deberian haber cambiado si se le da el valor 0",
				600, receta1.getCalorias());
	}

}
