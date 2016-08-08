package ar.edu.utn.d2s;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FiltroCalorias extends FiltroAbstract{

	@Id
	@Column(name = "FILTRO_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private int caloriasMinimas, caloriasMaximas;

	public FiltroCalorias(int minimo, int maximo) {
		setCaloriasMinimas(minimo);
		setCaloriasMaximas(maximo);
	}

	@Override
	public void filtrar(Reportes reporte) {
		List<Receta> recetasFiltradas =
				reporte.getRecetas()
				.stream()
				.filter(r ->
				r.getCalorias() >= getCaloriasMinimas() &&
				r.getCalorias() <= getCaloriasMaximas())
				.collect(Collectors.toList());
		reporte.setRecetas(recetasFiltradas);
	}

	public int getCaloriasMaximas() {
		return caloriasMaximas;
	}

	public void setCaloriasMaximas(int caloriasMaximas) {
		this.caloriasMaximas = caloriasMaximas;
	}

	public int getCaloriasMinimas() {
		return caloriasMinimas;
	}

	public void setCaloriasMinimas(int caloriasMinimas) {
		this.caloriasMinimas = caloriasMinimas;
	}
}