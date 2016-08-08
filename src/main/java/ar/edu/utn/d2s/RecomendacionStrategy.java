package ar.edu.utn.d2s;

import java.util.List;
import java.util.Map;

public interface RecomendacionStrategy {

	public Map<Receta, Double> recomendar(Usuario usuario, TipoComida tipoComida, List<Receta> recetas, List<Grupo> grupos);

}