package ar.edu.utn.d2s;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FiltroNombre extends FiltroAbstract{

	@Id
	@Column(name = "FILTRO_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String nombre;

	public FiltroNombre(String nombre) {
		setNombre(nombre);
	}

	@Override
	public void filtrar(Reportes reporte) {
		List<Receta> recetasFiltradas =
				reporte.getRecetas()
				.stream()
				.filter(r -> r.getNombre().equals(nombre))
				.collect(Collectors.toList());
		reporte.setRecetas(recetasFiltradas);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}