package ar.edu.utn.d2s;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
public class PiramideAlimenticia {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="IdPiramide")
	private int id;

	@LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection
    @MapKeyColumn(name="TipoAlimento")
    @Column(name="ComidasDiarias")
    @CollectionTable(name="PIRAMIDE_COMIDASDIARIAS", joinColumns=@JoinColumn(name="ID"))
	private Map<TipoAlimento, Integer> comidasDiarias;

	@LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection
    @MapKeyColumn(name="TipoAlimento")
    @Column(name="ComidasSemanales")
    @CollectionTable(name="PIRAMIDE_COMIDASSEMANALES", joinColumns=@JoinColumn(name="ID"))
	private Map<TipoAlimento, Integer> comidasSemanales;

	public PiramideAlimenticia() {
		setComidasDiarias(new HashMap<TipoAlimento, Integer>());
		setComidasSemanales(new HashMap<TipoAlimento, Integer>());
	}

	public void agregarComidaDiaria(TipoAlimento comida, int cantidad) {
		if (!comidasDiarias.containsKey(comida)) {
			comidasDiarias.put(comida, cantidad);
		}
	}

	public void agregarComidaSemanal(TipoAlimento comida, int cantidad) {
		if (!comidasSemanales.containsKey(comida)) {
			comidasSemanales.put(comida, cantidad);
		}
	}

	public Map<TipoAlimento, Integer> getComidasDiarias() {
		return comidasDiarias;
	}

	private void setComidasDiarias(Map<TipoAlimento, Integer> comidasDiarias) {
		this.comidasDiarias = comidasDiarias;
	}

	public Map<TipoAlimento, Integer> getComidasSemanales() {
		return comidasSemanales;
	}

	private void setComidasSemanales(Map<TipoAlimento, Integer> comidasSemanales) {
		this.comidasSemanales = comidasSemanales;
	}
}