package ar.edu.utn.d2s;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.d2s.exceptions.ExceptionGrupoInexistente;
import ar.edu.utn.d2s.exceptions.ExceptionYaExiste;

public class Reportes {
	
	private Usuario usuario;
	private List<Grupo> grupos = new ArrayList<Grupo>();
	private List<Usuario> usuarios = new ArrayList<Usuario>();
	private List<Receta> recetas = new ArrayList<Receta>();
	private List<FiltroStrategy> filtros = new ArrayList<FiltroStrategy>();

	public void agregarFiltro(FiltroStrategy filtro) throws ExceptionYaExiste {
		if (contieneFiltro(filtro)) {
			throw new ExceptionYaExiste("Filtro." + filtro.getClass().getName());
		}
		filtros.add(filtro);
	}

	public void configurarRepositorios(List<Grupo> grupos, List<Usuario> usuarios, List<Receta> recetas) {
		this.grupos.addAll(grupos);
		this.usuarios.addAll(usuarios);
		this.recetas.addAll(recetas);
	}

	public boolean contieneFiltro(FiltroStrategy filtro) {
		return filtros.contains(filtro);
	}

	public List<Receta> getRecetasFiltradas() throws ExceptionGrupoInexistente {
		for (FiltroStrategy filtro : this.filtros) {
			filtro.filtrar(this);
		}
		return recetas;
	}

	public List<Receta> getRecetas() {
		return recetas;
	}

	public void setRecetas(List<Receta> recetas) {
		this.recetas = recetas;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Grupo> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<Grupo> grupos) {
		this.grupos = grupos;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

}