package ar.edu.utn.d2s;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import ar.edu.utn.d2s.exceptions.ExceptionGrupoInexistente;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class FiltroAbstract implements FiltroStrategy {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected int id;
	
	public FiltroAbstract() {}

	@Override
	public void filtrar(Reportes reporte) throws ExceptionGrupoInexistente {}
}
