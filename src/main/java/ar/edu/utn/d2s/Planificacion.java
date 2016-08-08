package ar.edu.utn.d2s;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.*;

import ar.edu.utn.d2s.exceptions.ExceptionPlanificacionSuperior7Dias;
import ar.edu.utn.d2s.exceptions.ExceptionRecetaHorarioNoCorresponde;

@Entity
public class Planificacion {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private static final int DIFERENCIADIASMAXIMA = 7;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "EMAIL")
	private Usuario usuario;

	@Column(name = "DIA")
	private Calendar dia;

	@Enumerated(EnumType.STRING)
	@Column(name = "TIPOCOMIDA")
	private TipoComida tipoComida;

	@OneToOne(targetEntity = Receta.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "RECETA_ID")
	private Receta receta;

	public Planificacion() {
		super();
	}

	public Planificacion(Calendar dia, TipoComida tipoComida, Receta receta) throws ExceptionPlanificacionSuperior7Dias, ExceptionRecetaHorarioNoCorresponde {
		setDia(dia);
		setTipoComida(tipoComida);
		setReceta(receta);
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setDia(Calendar dia) throws ExceptionPlanificacionSuperior7Dias {
		Date ahora = Calendar.getInstance().getTime();
		Date dias = dia.getTime();
		if (getDifferenceDays(ahora, dias) > DIFERENCIADIASMAXIMA) {
			throw new ExceptionPlanificacionSuperior7Dias();
		}
		dia.set(dia.get(Calendar.YEAR), dia.get(Calendar.MONTH), dia.get(Calendar.DATE), 0, 0, 0);
		this.dia = dia;

	}

	private static long getDifferenceDays(Date d1, Date d2) {
		long diff = d2.getTime() - d1.getTime();
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public Calendar getDia() {
		return dia;
	}

	public TipoComida getTipoComida() {
		return tipoComida;
	}

	public void setTipoComida(TipoComida tipoComida) {
		this.tipoComida = tipoComida;
	}

	public Receta getReceta() {
		return receta;
	}

	public void setReceta(Receta receta) throws ExceptionRecetaHorarioNoCorresponde {
		if (!receta.contieneTipoComida(tipoComida)) {
			throw new ExceptionRecetaHorarioNoCorresponde();
		}
		this.receta = receta;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Planificacion)) return false;
		Planificacion c = (Planificacion) o;
		return dia.equals(c.getDia()) &&
				tipoComida.equals(c.getTipoComida()) &&
				receta.equals(c.getReceta());
	}

	@Override
	public int hashCode() {
		return (int) dia.hashCode() * tipoComida.hashCode() * receta.hashCode();
	}
}