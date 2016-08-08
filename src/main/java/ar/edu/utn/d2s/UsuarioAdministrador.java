package ar.edu.utn.d2s;

import java.util.Map;

import javax.persistence.Entity;

import ar.edu.utn.d2s.exceptions.ExceptionValidacionDatos;

@Entity
public class UsuarioAdministrador extends Usuario {

	protected UsuarioAdministrador() {
		super();
	}

	public UsuarioAdministrador(String nombre, String mail, int edad)
			throws ExceptionValidacionDatos {
		super(nombre, mail, edad);
	}

	@Override
	public Map<Receta, Long> getEstadisticasPorEstacionYSemana(Mes mes, Semana semana, Temporada temporada) {
		return super.getEstadistica().getPorTemporadaYSemana(mes, semana, temporada);
	}

	@Override
	public Map<Receta, Long> getEstadisticasPorEstacionYMes(Mes mes, Temporada temporada) {
		return super.getEstadistica().getPorTemporadaYMes(mes, temporada);
	}

	@Override
	public Map<Receta, Long> getEstadisticasPorRecetasMasCopiadas() {
		return super.getEstadistica().getPorRecetasMasCopiadas();
	}
}