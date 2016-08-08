package ar.edu.utn.d2s;

import javax.persistence.MappedSuperclass;

import ar.edu.utn.d2s.exceptions.ExceptionGrupoInexistente;

@MappedSuperclass
public interface FiltroStrategy {

	public void filtrar(Reportes reporte) throws ExceptionGrupoInexistente;

}