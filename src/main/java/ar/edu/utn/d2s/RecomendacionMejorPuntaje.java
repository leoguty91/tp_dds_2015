package ar.edu.utn.d2s;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecomendacionMejorPuntaje implements RecomendacionStrategy{

	@Override
	public Map<Receta, Double> recomendar(Usuario usuario, TipoComida tipoComida, List<Receta> recetas, List<Grupo> grupos) {
		Map<Receta, Double> mapRecomendacion = new LinkedHashMap<Receta, Double>();
		mapRecomendacion = recetas
				.stream()
				.filter(r -> r.contieneTipoComida(tipoComida))
				.collect(Collectors.toMap(r -> r, r -> getPuntajeGeneral(usuario, r, grupos)));
		return mapRecomendacion;
	}

	private Double getPromedioGrupos(Receta receta, List<Grupo> grupos) {
		Double puntaje = (double) 0;
		if (grupos.stream().anyMatch(g -> g.contieneReceta(receta))) {
			puntaje += grupos
					.stream()
					.filter(g -> g.contieneReceta(receta))
					.mapToDouble(g -> g.getPromedio(receta))
					.average()
					.getAsDouble();
		}
		return puntaje;
	}

	private Double getPuntajeGeneral(Usuario usuario, Receta receta, List<Grupo> grupos) {
		Double puntaje = (double) 0;
		puntaje += getPuntajePreferencia(usuario, receta);
		puntaje += getPromedioGrupos(receta, grupos);
		return puntaje;
	}

	private Double getPuntajePreferencia(Usuario usuario, Receta receta) {
		Double puntaje = (double) 0;
		if (usuario.getPreferencias().size() > 0) {
			puntaje += usuario.getPreferencias()
					.stream()
					.filter(p -> receta.contieneIngrediente(p))
					.count();
		}
		return puntaje;
	}
}