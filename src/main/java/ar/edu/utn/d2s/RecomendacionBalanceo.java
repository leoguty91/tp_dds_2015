package ar.edu.utn.d2s;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecomendacionBalanceo implements RecomendacionStrategy{

	private static final int UNASEMANA = 7;

	@Override
	public Map<Receta, Double> recomendar(Usuario usuario, TipoComida tipoComida, List<Receta> recetas, List<Grupo> grupos) {
		Map<Receta, Double> recomendacionMap = new LinkedHashMap<Receta, Double>();
		recomendacionMap = recetas
				.stream()
				.filter(r -> r.contieneTipoComida(tipoComida))
				.collect(Collectors.toMap(r -> r, r -> getPuntaje(usuario, r)));
		return recomendacionMap;
	}

	private Double getPuntaje (Usuario usuario, Receta receta) {
		double puntaje = 0;
		puntaje += getPuntajeSemana(usuario, receta);
		puntaje += getPuntajeDia(usuario, receta);
		return puntaje;
	}

	private Double getPuntajeDia(Usuario usuario, Receta receta) {
		double total = 0;
		Calendar desde = Calendar.getInstance();
		desde.set(desde.get(Calendar.YEAR), desde.get(Calendar.MONTH), desde.get(Calendar.DATE), 0, 0, 0);
		Calendar hasta = desde;
		desde.add(Calendar.SECOND, -1);
		hasta.add(Calendar.SECOND, +1);
		Map<TipoAlimento, Integer> comidasDiarias = usuario.getPiramide().getComidasDiarias();
		if (comidasDiarias.size() > 0) {
			for (Map.Entry<TipoAlimento, Integer> entry : comidasDiarias.entrySet()) {
				total += getSubtotal(entry, usuario, receta, desde, hasta);
			}
		}
		return (double) total;
	}

	private Double getPuntajeSemana(Usuario usuario, Receta receta) {
		double total = 0;
		Calendar desde = Calendar.getInstance();
		desde.set(desde.get(Calendar.YEAR), desde.get(Calendar.MONTH), desde.get(Calendar.DATE), 0, 0, 0);
		desde.add(Calendar.DATE, - UNASEMANA);
		Calendar hasta = Calendar.getInstance();
		Map<TipoAlimento, Integer> comidasSemanales = usuario.getPiramide().getComidasSemanales();
		if (comidasSemanales.size() > 0) {
			for (Map.Entry<TipoAlimento, Integer> entry : comidasSemanales.entrySet()) {
				total += getSubtotal(entry, usuario, receta, desde, hasta);
			}
		}
		return (double) total;
	}

	private int getCantidadEnPlanificaciones(Usuario usuario, TipoAlimento tipoAlimento, Calendar desde, Calendar hasta) {
		return usuario.getPlanificaciones()
				.stream()
				.filter(p ->
				p.getReceta().contieneTipoAlimento(tipoAlimento) &&
				p.getDia().after(desde) && p.getDia().before(hasta))
				.map(p -> p.getReceta())
				.reduce(0,
						(sum, r) -> sum += r.getCantidadTipoAlimento(tipoAlimento),
						(sum1, sum2) -> sum1 + sum2);
	}

	private double getSubtotal(Map.Entry<TipoAlimento, Integer> entry, Usuario usuario, Receta receta, Calendar desde, Calendar hasta) {
		int subtotal = 0;
		int cantidadEnPiramide = entry.getValue();
		int cantidadEnReceta = receta.getCantidadTipoAlimento(entry.getKey());
		int cantidadEnPlanificaciones = getCantidadEnPlanificaciones(usuario, entry.getKey(), desde, hasta);
		if (cantidadEnPiramide > cantidadEnPlanificaciones) {
			subtotal += cantidadEnReceta;
		}
		return (double) subtotal;
	}
}