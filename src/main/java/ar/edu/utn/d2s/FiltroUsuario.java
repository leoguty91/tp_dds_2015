package ar.edu.utn.d2s;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FiltroUsuario extends FiltroAbstract{

	@Id
	@Column(name = "FILTRO_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private Usuario usuario;

	public FiltroUsuario(Usuario usuario) {
		setUsuario(usuario);
	}

	@Override
	public void filtrar(Reportes reporte) {
		List<Receta> recetasFiltradas =
				reporte.getRecetas()
				.stream()
				.filter(r -> usuario.equals(r.getAutor()))
				.collect(Collectors.toList());
		reporte.setRecetas(recetasFiltradas);
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}