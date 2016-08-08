package ar.edu.utn.d2s;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FiltroRecetasNuevas extends FiltroAbstract{

	@Id
	@Column(name = "FILTRO_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private Usuario usuario;

	public FiltroRecetasNuevas(Usuario usuario) {
		setUsuario(usuario);
	}

	@Override
	public void filtrar(Reportes reporte) {
		List<Receta> recetasFiltradas =
				getUsuario().getGrupos()
				.stream()
				.flatMap(g -> g.getRecetas().stream())
				.filter(r -> reporte.getRecetas().contains(r))
				.collect(Collectors.toList());
		reporte.setRecetas(recetasFiltradas);
	}

	private Usuario getUsuario() {
		return usuario;
	}

	private void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}