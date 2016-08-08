package ar.edu.utn.d2s;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Estadisticas {

	private List<Usuario> usuarios = new ArrayList<Usuario>();
	private List<Receta> recetas = new ArrayList<Receta>();

	public Estadisticas() {}

	public void configurarRepositorios(List<Usuario> usuarios, List<Receta> recetas) {
		this.usuarios.addAll(usuarios);
		this.recetas.addAll(recetas);
	}

	private Map<Receta, Long> filtrarPorTemporada(Calendar desde, Calendar hasta, Temporada temporada) {
		Map<Receta, Long> mapEstadisticas = new HashMap<Receta, Long>();
		
		mapEstadisticas = getPlanificaciones()
		.stream()
		.filter(p ->
		p.getDia().after(desde) &&
		p.getDia().before(hasta) &&
		p.getReceta().getTemporada().equals(temporada))
		.map(p -> p.getReceta())
		.collect(Collectors.groupingBy(r -> r, Collectors.counting()));

		return mapEstadisticas;
	}

	private List<Planificacion> getPlanificaciones() {
		return usuarios
				.stream()
				.flatMap(u -> u.getPlanificaciones().stream())
				.map(pc -> pc)
				.collect(Collectors.toList());
	}

	public Map<Receta, Long> getPorTemporadaYMes(Mes mes, Temporada temporada) {
		Calendar desde = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), mes.getMes(), 1);
		Calendar hasta = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), mes.getMes() + 1, 1);

		return filtrarPorTemporada(desde, hasta, temporada);
	}

	public Map<Receta, Long> getPorTemporadaYSemana(Mes mes, Semana semana, Temporada temporada) {
		Calendar desde = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), mes.getMes(), semana.getDesde());
		Calendar hasta = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), mes.getMes(), semana.getHasta() +1);

		return filtrarPorTemporada(desde, hasta, temporada);
	}

	public Map<Receta, Long> getPorRecetasMasCopiadas() {
		Map<Receta, Long> mapEstadisticas = recetas
				.stream()
				.filter(r -> r.getCopias() > 0)
				.collect(Collectors.toMap(r -> r, r -> r.getCopias()));

		return mapEstadisticas;
	}
}