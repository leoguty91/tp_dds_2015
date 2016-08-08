package ar.edu.utn.d2s;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.edu.utn.d2s.Usuario;
import ar.edu.utn.d2s.RepositorioUsuarios;
import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionPlanificacionSuperior7Dias;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaHorarioNoCorresponde;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class UsuarioTest {

	private Usuario usuario1, usuario2;
	private RepositorioUsuarios repositorioUsuario;
	private RepositorioRecetas repositorioRecetas;
	private Calendar fecha1;
	private Receta receta1, receta2, receta3;
	private Planificacion planificacion1;
	private Grupo grupo1;
	private List<Ingrediente> ingredientes1, ingredientes2, ingredientes3;

	@Before
	public void inicio() throws ExceptionValidacionDatos, ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde, ExceptionYaExiste, ExceptionIngredienteRestringido, ExceptionUsuarioNoPertenceAlGrupo {
		repositorioUsuario = RepositorioUsuarios.getInstance();
		repositorioRecetas = RepositorioRecetas.getInstance();
		usuario1 = new UsuarioComun("Juan", "juan@hotmail.com", 19);
		usuario1.agregarRestriccion(new Restriccion("Diabetes", new Ingrediente("Azucar")));
		repositorioUsuario.resetearRepositorio();
		repositorioUsuario.agregarUsuario(usuario1);
		grupo1 = new Grupo("Club Antiazucar");
		usuario1.unirAlGrupo(grupo1);
		fecha1 = Calendar.getInstance();
		ingredientes1 = new ArrayList<Ingrediente>();
		ingredientes1.add(new Ingrediente("Vacio", TipoAlimento.CARNES));
		ingredientes1.add(new Ingrediente("Asado", TipoAlimento.CARNES));
		receta1 = new Receta("Asado", ingredientes1, "...", Dificultad.MEDIA, Temporada.PRIMAVERA, 600);
		receta1.agregarTipoComida(TipoComida.ALMUERZO);
		receta1.agregarTipoComida(TipoComida.CENA);
		receta1.setAutor(usuario1);
		repositorioRecetas.resetearRepositorio();
		repositorioRecetas.agregarReceta(receta1);
		usuario1.compartirReceta(grupo1, receta1);
		ingredientes2 = new ArrayList<Ingrediente>();
		ingredientes2.add(new Ingrediente("Harina", TipoAlimento.PASTASPANARROZ));
		ingredientes2.add(new Ingrediente("Manzana", TipoAlimento.FRUTAS));
		ingredientes2.add(new Ingrediente("Azucar", TipoAlimento.DULCES));
		ingredientes2.add(new Ingrediente("Huevos"));
		receta2 = new Receta("Torta de manzana", ingredientes2, "...", Dificultad.MEDIA, Temporada.OTONIO, 300);
		repositorioRecetas.agregarReceta(receta2);
		ingredientes3 = new ArrayList<Ingrediente>();
		ingredientes3.add(new Ingrediente("Pollo"));
		ingredientes3.add(new Ingrediente("Papas"));
		receta3 = new Receta("Pollo al horno con papas", ingredientes3, "...", Dificultad.DIFICIL, Temporada.INVIERNO, 200);
		repositorioRecetas.agregarReceta(receta3);
		planificacion1 = new Planificacion(fecha1, TipoComida.CENA, receta1);
		usuario1.agregarPlanificacion(planificacion1);
	};

	// Entrega 2
	@Test(expected = ExceptionYaExiste.class)
	public void testNoHay2UsuariosConMismoMail() throws ExceptionYaExiste, ExceptionValidacionDatos {
		usuario2 = new UsuarioComun("Juan", "juan@hotmail.com", 24);
		repositorioUsuario.agregarUsuario(usuario2);

		assertEquals(
				"El grupo debería tener 1 usuario cuando se agrega a otro con el mismo mail",
				1, repositorioUsuario.getUsuarios().size());
	};

	@Test(expected = ExceptionValidacionDatos.class)
	public void testValidacionDatosObligatoriosUsuarioNombre() throws ExceptionValidacionDatos, ExceptionYaExiste {
		this.usuario2 = new UsuarioComun("", "juan@hotmail.com", 24);
		this.repositorioUsuario.agregarUsuario(usuario2);
		
		assertEquals("El grupo deberia tener 1 usuario, ya que el segundo no cumple con la validacion",
				1, repositorioUsuario.getUsuarios().size());
	};

	@Test(expected = ExceptionValidacionDatos.class)
	public void testValidacionDatosObligatoriosUsuarioEdad() throws ExceptionValidacionDatos, ExceptionYaExiste {
		this.usuario2 = new UsuarioComun("Juan", "juan@hotmail.com", 17);
		this.repositorioUsuario.agregarUsuario(usuario2);
		
		assertEquals("El grupo deberia tener 1 usuario, ya que el segundo no cumple con la validacion",
				1, repositorioUsuario.getUsuarios().size());
	};

	@Test(expected = ExceptionValidacionDatos.class)
	public void testValidacionDatosObligatoriosUsuarioMail() throws ExceptionValidacionDatos, ExceptionYaExiste {
		this.usuario2 = new UsuarioComun("Juan", "juan@.com", 24);
		this.repositorioUsuario.agregarUsuario(usuario2);
		
		assertEquals("El grupo deberia tener 1 usuario, ya que el segundo no cumple con la validacion",
				1, repositorioUsuario.getUsuarios().size());
	};

	@Test(expected = ExceptionYaExiste.class)
	public void testAgregarPreferencia() throws ExceptionYaExiste {
		usuario1.agregarPreferencia(new Ingrediente("Asado", TipoAlimento.CARNES));
		usuario1.agregarPreferencia(new Ingrediente("Azucar", TipoAlimento.DULCES));
		usuario1.agregarPreferencia(new Ingrediente("Asado", TipoAlimento.CARNES));

		assertEquals("El usuario debería tener 2 preferencias",
				2, usuario1.getPreferencias().size());
	};
}
