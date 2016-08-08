package ar.edu.utn.d2s;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import ar.edu.utn.d2s.exceptions.ExceptionGrupoInexistente;

@Entity
public class FiltroGrupo extends FiltroAbstract{

	@Id
	@Column(name = "FILTRO_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String grupo;

	public FiltroGrupo(String grupo) {
		setGrupo(grupo);
	}

	@Override
	public void filtrar(Reportes reporte) throws ExceptionGrupoInexistente {
		List<Receta> recetasFiltradas =
				getRecetas(reporte.getGrupos(), getGrupo())
				.stream()
				.filter(r -> reporte.getRecetas().contains(r))
				.distinct()
				.collect(Collectors.toList());
		reporte.setRecetas(recetasFiltradas);
	}

	private List<Receta> getRecetas(List<Grupo> grupos, String grupo) {
		return grupos
				.stream()
				.filter(g -> g.getNombre().equals(grupo))
				.flatMap(g -> g.getRecetas().stream())
				.collect(Collectors.toList());
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
}