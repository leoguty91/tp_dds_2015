package ar.edu.utn.d2s;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FiltroPeriodo extends FiltroAbstract{
	
	@Id
	@Column(name = "FILTRO_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private Calendar desde, hasta;
	
	public FiltroPeriodo(Calendar desde, Calendar hasta) {
		desde.set(desde.get(Calendar.YEAR), desde.get(Calendar.MONTH), desde.get(Calendar.DATE), 0, 0, 0);
		hasta.set(hasta.get(Calendar.YEAR), hasta.get(Calendar.MONTH), hasta.get(Calendar.DATE), 0, 0, 0);
		setFecha1(desde);
		setFecha2(hasta);
	}

	@Override
	public void filtrar(Reportes reporte) {
		List<Receta> recetaFiltradas =
				getPlanificaciones(reporte.getUsuarios())
				.stream()
				.filter(pc ->
				pc.getDia().after(desde) && pc.getDia().before(hasta) &&
				reporte.getRecetas().contains(pc.getReceta()))
				.map(pc -> pc.getReceta())
				.collect(Collectors.toList());
		reporte.setRecetas(recetaFiltradas);
	}

	public List<Planificacion> getPlanificaciones(List<Usuario> usuarios) {
		return usuarios
				.stream()
				.flatMap(u -> u.getPlanificaciones().stream())
				.collect(Collectors.toList());
	}

	public Calendar getFecha1() {
		return desde;
	}

	public void setFecha1(Calendar fecha1) {
		this.desde = fecha1;
	}

	public Calendar getFecha2() {
		return hasta;
	}

	public void setFecha2(Calendar fecha2) {
		this.hasta = fecha2;
	}
}