package ar.edu.utn.d2s;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class FiltroIngrediente extends FiltroAbstract{

	@Id
	@Column(name = "FILTRO_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private Ingrediente ingrediente;

	public FiltroIngrediente(Ingrediente ingrediente) {
		setIngrediente(ingrediente);
	}

	@Override
	public void filtrar(Reportes reporte) {
		List<Receta> recetasFiltradas =
				reporte.getRecetas()
				.stream()
				.filter(r -> r.contieneIngrediente(ingrediente))
				.collect(Collectors.toList());
		reporte.setRecetas(recetasFiltradas);
				
	}

	public Ingrediente getIngrediente() {
		return ingrediente;
	}

	public void setIngrediente(Ingrediente ingrediente) {
		this.ingrediente = ingrediente;
	}

}