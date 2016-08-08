package ar.edu.utn.d2s;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ar.edu.utn.d2s.exceptions.ExceptionIngredienteRestringido;
import ar.edu.utn.d2s.exceptions.ExceptionUsuarioNoPertenceAlGrupo;
import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class GrupoTest {
	
	private Usuario usuario1, usuario2;
	private RepositorioUsuarios repositorioUsuarios;
	private RepositorioGrupos repositorioGrupos;
	private Grupo grupo1, grupo2, grupo3;
	private Receta receta1, receta2;
	private ArrayList<Receta> listaRecetas;
	private ArrayList<Ingrediente> ingredientes1, ingredientes2;

	@Before
	public void inicio() throws ExceptionValidacionDatos, ExceptionYaExiste, ExceptionIngredienteRestringido {
		usuario1 = new UsuarioComun("Juan", "juan@hotmail.com", 23);
		usuario2 = new UsuarioComun("Jose", "jose@gmail.com", 19);
		repositorioUsuarios = RepositorioUsuarios.getInstance();
		repositorioUsuarios.resetearRepositorio();
		repositorioUsuarios.agregarUsuario(usuario1);
		repositorioUsuarios.agregarUsuario(usuario2);
		grupo1 = new Grupo("Club vegetariano");
		grupo2 = new Grupo("Aguante carne");
		repositorioGrupos = RepositorioGrupos.getInstance();
		repositorioGrupos.resetearRepositorio();
		repositorioGrupos.agregarGrupo(grupo1);
		repositorioGrupos.agregarGrupo(grupo2);
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
	};

	// Entrega 2
	@Test(expected = ExceptionYaExiste.class)
	public void testNoHayDosGruposConElMismoNombre() throws ExceptionYaExiste {
		grupo3 = new Grupo("Club vegetariano");
		repositorioGrupos.agregarGrupo(grupo3);
		
		assertEquals("El repositorio deberia tener 2 grupos, sin agregar el repetido",
				2, repositorioGrupos.getGrupos().size());
	};

	@Test
	public void testAgregaUsuarioAGrupo() throws ExceptionYaExiste {
		usuario1.unirAlGrupo(grupo1);
		
		assertTrue("El usuario deberia pertenecer al grupo",
				grupo1.contieneUsuario(usuario1));
		assertTrue("El grupo deberia estar en los grupos del usuario",
				usuario1.contieneAlGrupo(grupo1));
	};

	@Test(expected = ExceptionYaExiste.class)
	public void testAgregaUsuarioAlGrupoDosVeces() throws ExceptionYaExiste {
		usuario1.unirAlGrupo(grupo1);
		usuario1.unirAlGrupo(grupo1);
		
		assertEquals("El grupo debería tener 1 usuario",
				1, grupo1.getUsuarios().size());
	};

	@Test(expected = ExceptionUsuarioNoPertenceAlGrupo.class)
	public void testUsuarioMiembroPuedeCompartirReceta() throws ExceptionYaExiste, ExceptionValidacionDatos, ExceptionUsuarioNoPertenceAlGrupo {
		usuario1.unirAlGrupo(grupo1);
		usuario1.compartirReceta(grupo1, receta1);
		usuario2.compartirReceta(grupo1, receta2);

		assertEquals("El grupo debería tener 1 receta, la del primero, el segundo no pertenece al grupo",
				1, grupo1.getRecetas().size());
	};

	@Test
	public void testListarRecetas() throws ExceptionYaExiste, ExceptionValidacionDatos, ExceptionUsuarioNoPertenceAlGrupo {
		usuario1.unirAlGrupo(grupo1);
		usuario2.unirAlGrupo(grupo1);
		usuario1.compartirReceta(grupo1, receta1);
		usuario2.compartirReceta(grupo1, receta2);
		listaRecetas = new ArrayList<Receta>();
		listaRecetas.add(receta1);
		listaRecetas.add(receta2);
		
		assertEquals("Las listas deberían ser iguales",
				listaRecetas, grupo1.getRecetas());
	};

	@Test(expected = ExceptionUsuarioNoPertenceAlGrupo.class)
	public void testElminaUsuarioQueNoEsMiembro() throws ExceptionUsuarioNoPertenceAlGrupo {
		grupo1.eliminarUsuario(usuario1);
	};

	@Test
	public void testEliminaMiembroYSusRecetas() throws ExceptionUsuarioNoPertenceAlGrupo, ExceptionYaExiste, ExceptionIngredienteRestringido, ExceptionValidacionDatos {
		usuario1.unirAlGrupo(grupo1);
		usuario1.compartirReceta(grupo1, receta1);
		usuario1.agregarReceta(receta2);
		usuario1.compartirReceta(grupo1, receta2);

		assertEquals("El grupo debería tener 1 usuario",
				1, grupo1.getUsuarios().size());
		assertEquals("El grupo debería tener 2 recetas",
				2, grupo1.getRecetas().size());

		grupo1.eliminarUsuario(usuario1);

		assertTrue("El grupo no debería tener usuarios",
				grupo1.getUsuarios().isEmpty());
		assertTrue("El grupo no debería tener recetas",
				grupo1.getRecetas().isEmpty());
		
	};
}
