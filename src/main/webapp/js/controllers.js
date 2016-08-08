'use strict';

angular
.module('myApp.controllers', [])
.controller('loginCtrl', ['$scope', '$location', '$cookieStore', 'Login', function($scope, $location, $cookieStore, Login) {
	$scope.errors = [];
	$scope.success = [];
	$scope.login = function() {
		var registro = {
				usuario: $scope.usuario.email,
				password: $scope.usuario.password
		};
		Login.post(registro).then(function(data) {
			if (data.status >= 200 && data.status < 300) {
				$cookieStore.put('user', $scope.usuario.email);
				$location.path('/');
			}
		}, function errorCallback(response) {
			console.log(response);
		});
	};
	$scope.unloggin = function() {
		$cookieStore.remove('user');
		$location.path('/login');
	};
	$scope.userIsLoggin = function() {
		return angular.isDefined($cookieStore.get('user')) && $cookieStore.get('user') !== null;
	};
	$scope.$on('interceptorError', function(event, data) {
		$scope.errors.push(data.mensaje);
	});
	$scope.$on('interceptorSuccess', function(event, data) {
		$scope.success.push(data.mensaje);
	});
}])

.controller('principalCtrl', ['$scope', '$cookieStore', 'Usuarios', 'Recetas', function($scope, $cookieStore, Usuarios, Recetas) {
	$scope.usuario = Usuarios.one($cookieStore.get('user')).get().$object;
	$scope.iniciaDatos = function() {
		$scope.ultimasRecetas = Recetas.getList({ usuario: $cookieStore.get('user'), ultimasRecetas: true }).$object;
	}
	$scope.iniciaDatos();
}])

.controller('recetasCtrl', ['$rootScope', '$scope', '$routeParams', '$location', '$cookieStore', 'Recetas', 'Restangular', 'puedeEditar', function($rootScope, $scope, $routeParams, $location, $cookieStore, Recetas, Restangular, puedeEditar) {
	$scope.puedeEditar = puedeEditar || false;
	$scope.receta = { ingredientes: [] };
	$scope.contieneReceta = false;
	$scope.agregaIngrediente = function() {
		if ($scope.ingrediente != '' && $scope.receta.ingredientes.indexOf($scope.ingrediente) == -1) {
			$scope.receta.ingredientes.push($scope.ingrediente);
			$scope.ingrediente = '';
		}
	};
	$scope.seleccionaIngrediente = function(ingrediente) {
		$scope.ingrediente = ingrediente;
	};
	$scope.eliminaIngrediente = function() {
		if ($scope.ingrediente != '' && $scope.receta.ingredientes.indexOf($scope.ingrediente) != -1) {
			$scope.receta.ingredientes.splice($scope.receta.ingredientes.indexOf($scope.ingrediente), 1);
			$scope.ingrediente = '';
		}
	};
	$scope.iniciaDatos = function() {
		if ($routeParams.recetaId) {
			Recetas.one($routeParams.recetaId).get().then(function(data) {
				$scope.receta = data.data;
				$scope.contieneReceta = (data.data.autor == $cookieStore.get('user')) ? true : false;
			});
		} else {
			$scope.recetasPropias = Recetas.getList({ autor: $cookieStore.get('user') }).$object;
			$scope.ultimasRecetas = Recetas.getList({ usuario: $cookieStore.get('user'), ultimasRecetas: true }).$object;
		}
	};
	$scope.save = function(receta) {
		if ($routeParams.recetaId && receta.autor == $cookieStore.get('user')) {
			Restangular.copy(receta).put().then(function(data) {
				$scope.iniciaDatos();
				/*if (data.result) {
					$rootScope.$broadcast('interceptorSuccess', {
						mensaje: data.result
					});
				}*/
			});
		} else {
			receta.autor = $cookieStore.get('user');
			Recetas.post(receta).then(function(data) {
				$scope.iniciaDatos();
				/*if (data.result) {
					$rootScope.$broadcast('interceptorSuccess', {
						mensaje: data.result
					});
				}*/
			});

		}
		$location.path('/recetas');
	}
	$scope.eliminaReceta = function(receta) {
		if (receta.id && receta.autor == $cookieStore.get('user')) {
			receta.remove().then(function() {
				$scope.iniciaDatos();
				/*for (var int = 0; int < $scope.recetasPropias.length; int++) {
					if ($scope.recetasPropias[int].id === receta.id) {
						$scope.recetasPropias.splice(int, 1);
						break;
					}
				}
				for (var int2 = 0; int2 < $scope.ultimasRecetas.length; int2++) {
					if ($scope.ultimasRecetas[int2].id === receta.id) {
						$scope.ultimasRecetas.splice(int2, 1);
						break;
					}
				}*/
			});
		}
	};
	$scope.iniciaDatos();
}])

.controller('usuarioCtrl', ['$rootScope', '$scope', '$location', '$cookieStore', 'Usuarios', 'Restangular', function($rootScope, $scope, $location, $cookieStore, Usuarios, Restangular) {
	$scope.usuario = { preferencias: [], condiciones: [] }
	$scope.agregaPreferencia = function() {
		if ($scope.preferencia != '' && $scope.usuario.preferencias.indexOf($scope.preferencia) == -1) {
			$scope.usuario.preferencias.push($scope.preferencia);
			$scope.preferencia = '';
		}
	};
	$scope.seleccionaPreferencia = function(preferencia) {
		$scope.preferencia = preferencia;
	};
	$scope.eliminaPreferencia = function() {
		if ($scope.preferencia != '' && $scope.usuario.preferencias.indexOf($scope.preferencia) != -1) {
			$scope.usuario.preferencias.splice($scope.usuario.preferencias.indexOf($scope.preferencia), 1);
			$scope.preferencia = '';
		}
	};
	$scope.agregarCondicion = function() {
		if ($scope.condicion != '' && $scope.usuario.condiciones.indexOf($scope.condicion) == -1) {
			$scope.usuario.condiciones.push($scope.condicion);
		}
	};
	$scope.seleccionaCondicion = function(condicion) {
		$scope.condicion = condicion;
	};
	$scope.eliminaCondicion = function() {
		if ($scope.condicion != '' && $scope.usuario.condiciones.indexOf($scope.condicion) != -1) {
			$scope.usuario.condiciones.splice($scope.usuario.condiciones.indexOf($scope.condicion), 1);
		}
	}
	$scope.iniciaDatos = function() {
		if ($scope.userIsLoggin()) {
			$scope.usuario = Usuarios.one($cookieStore.get('user')).get().$object;
		}
	}
	$scope.save = function(usuario) {
		if (!$scope.userIsLoggin()) {
			Usuarios.post(usuario).then(function(data) {
				$scope.iniciaDatos();
				/*if (data.result) {
					$rootScope.$broadcast('interceptorSuccess', {
						mensaje: data.result
					});
				}*/
			});
		} else {
			Restangular.copy(usuario).put().then(function(data) {
				$scope.iniciaDatos();
				/*if (data.result) {
					$rootScope.$broadcast('interceptorSuccess', {
						mensaje: data.result
					});
				}*/
			});
		}
		$location.path('/');
	}
	$scope.userIsLoggin = function() {
		return angular.isDefined($cookieStore.get('user')) && $cookieStore.get('user') !== null;
	};
	$scope.iniciaDatos();
}])

.controller('gruposCtrl', ['$scope', '$routeParams', '$cookieStore', 'Grupos', 'Usuarios', 'Recetas', 'Calificaciones', 'Restangular', function($scope, $routeParams, $cookieStore, Grupos, Usuarios, Recetas, Calificaciones, Restangular) {
	$scope.usuario = $cookieStore.get('user');
	$scope.user = Usuarios.one($cookieStore.get('user')).get().$object;
	$scope.iniciaDatos = function() {
		$scope.perteneceAlGrupo = false;
		if ($routeParams.nombreGrupo) {
			$scope.grupo = {};
			$scope.grupo.nombre = $routeParams.nombreGrupo;
			$scope.recetas = Recetas.getList({ grupo: $routeParams.nombreGrupo }).$object;
			$scope.usuarios = Usuarios.getList({ grupo: $routeParams.nombreGrupo }).$object;
			Usuarios.getList({ grupo: $routeParams.nombreGrupo }).then(function(data) {
				data.data.forEach(function(usuario) {
					if (usuario.email == $cookieStore.get('user')) {
						$scope.perteneceAlGrupo = true;
						$scope.cargaRecetasPropias();
					}
				});
			});
		} else {
			$scope.gruposGeneral = Grupos.getList().$object;
			$scope.gruposPropios = Grupos.getList({ usuario: $cookieStore.get('user') }).$object;
		}
	};
	$scope.agregaUsuario = function() {
		if ($routeParams.nombreGrupo) {
			Grupos.one($routeParams.nombreGrupo).post('usuarios', { email: $cookieStore.get('user') }).then(function(data) {
				$scope.iniciaDatos();
				/*$scope.perteneceAlGrupo = true;
				$scope.cargaRecetasPropias();
				var usuario = {
						nombre: $scope.user.nombre,
						email: $scope.usuario
				}
				$scope.usuarios.push(usuario);*/
			});
		}
	};
	$scope.eliminaUsuario = function() {
		if ($routeParams.nombreGrupo) {
			Grupos.one($routeParams.nombreGrupo).one('usuarios', $cookieStore.get('user')).remove().then(function(data) {
				$scope.iniciaDatos();
				/*$scope.perteneceAlGrupo = false;
				for (var int = 0; int < $scope.usuarios.length; int++) {
					if ($scope.usuarios[int].email === $scope.usuario) {
						$scope.usuarios.splice(int, 1);
						break;
					}
				}*/
			});
		}
	};
	$scope.agregaReceta = function(receta) {
		if ($routeParams.nombreGrupo) {
			if ($scope.recetas.indexOf(receta) < 0) {
				//$scope.recetas.push(receta);
				Grupos.one($routeParams.nombreGrupo).post('recetas', { recetaId: receta.id }).then(function(data) {
					$scope.iniciaDatos();
					console.log('Se compartio la Receta en el Grupo');
				});
			}
		}
	};
	$scope.eliminaReceta = function(idReceta) {
		if ($routeParams.nombreGrupo) {
			Grupos.one($routeParams.nombreGrupo).one('recetas', idReceta.id).remove().then(function(data) {
				$scope.iniciaDatos();
				/*for (var int = 0; int < $scope.recetas.length; int++) {
					if ($scope.recetas[int].id === idReceta.id) {
						$scope.recetas.splice(int, 1);
						break;
					}
				}*/
				console.log('Se elimino la Receta del Grupo');
			});
		}
	};
	$scope.cargaRecetasPropias = function() {
		$scope.recetasPropias = Recetas.getList({ autor: $cookieStore.get('user') }).$object;
	};
	$scope.mostrarCalificacion = function(recetaId) {
		Calificaciones.getList({ grupo: $routeParams.nombreGrupo, usuario: $cookieStore.get('user'), receta: recetaId }).then(function(data) {
			$scope.calificacion = data.data[0];
		});
		$('#modalCalificacion').modal('show');
	};
	$scope.guardarCalificacion = function(calificacion) {
		if ($scope.calificacion.calificacion > 0 && $scope.calificacion.calificacion < 6) {
			if ($scope.calificacion.id == 0) {
				var c = {
						grupo: $routeParams.nombreGrupo,
						receta: $scope.calificacion.receta,
						calificacion: $scope.calificacion.calificacion,
						usuario: $cookieStore.get('user')
				}
				Calificaciones.post(c).then(function(data) {
					$scope.iniciaDatos();
					console.log('Se guardo la Calificacion');
				});
			} else {
				Restangular.copy(calificacion).put().then(function(data) {
					$scope.iniciaDatos();
					console.log('Se actualizo la Calificacion');
				});
			}
		}
		$('#modalCalificacion').modal('hide');
	};
	$scope.iniciaDatos();
}])

.controller('planificarCtrl', ['$rootScope', '$scope', '$location', '$routeParams', '$cookieStore', 'Restangular', 'Recetas', 'Planificaciones', function($rootScope, $scope, $location, $routeParams, $cookieStore, Restangular, Recetas, Planificaciones) {
	$scope.planificacion = { receta: { id: '' } };
	$scope.iniciaDatos = function() {
		$scope.recetasGeneral = Recetas.getList({ usuario: $cookieStore.get('user') }).$object;
		if ($routeParams.planificacionId) {
			$scope.planificacion = Planificaciones.one($routeParams.planificacionId).get().$object;
		} else if ($routeParams.recetaId) {
			$scope.planificacion.receta.id = $routeParams.recetaId;
		} else {
			$scope.planificaciones = Planificaciones.getList({ usuario: $cookieStore.get('user') }).$object;
		}
	};
	$scope.guardar = function(planificacion) {
		var receta = {
				id: planificacion.receta.id,
				nombre: planificacion.receta.nombre
		}
		planificacion.receta = receta;
		planificacion.usuario = $cookieStore.get('user');
		if (!$routeParams.planificacionId) {
			Planificaciones.post(planificacion).then(function(data) {
				$scope.iniciaDatos();
				/*if (data.result) {
					$rootScope.$broadcast('interceptorSuccess', {
						mensaje: data.result
					});
				}*/
			});
		} else {
			Restangular.copy(planificacion).put().then(function(data) {
				$scope.iniciaDatos();
				/*if (data.result) {
					$rootScope.$broadcast('interceptorSuccess', {
						mensaje: 'Su planificacion se actualizo correctamente'
					});
				}*/
			});
		}
		$location.path('/planificaciones');
	};
	$scope.eliminaPlanificacion = function(planificacion) {
		if (planificacion.id) {
			Planificaciones.one(planificacion.id).remove().then(function(data) {
				$scope.iniciaDatos();
				/*for (var int = 0; int < $scope.planificaciones.length; int++) {
					if ($scope.planificaciones[int].id === planificacion.id) {
						$scope.planificaciones.splice(int, 1);
						break;
					}
				}*/
			});
		}
	}
	$scope.iniciaDatos();
}])

.controller('estadisticasCtrl', ['$scope', '$cookieStore', 'Estadisticas', function($scope, $cookieStore, Estadisticas) {
	$scope.filtro = { mes: '', semana: '', temporada: '' }
	$scope.iniciaDatos = function() {
		$scope.recetasMasCopiadas = Estadisticas.getList({ recetasMasCopiadas: true, usuario: $cookieStore.get('user') }).$object;
	};
	$scope.filtrar = function() {
		$scope.filtros = {};
		if ($scope.filtro.mes) { $scope.filtros.mes = $scope.filtro.mes; }
		if ($scope.filtro.mes && $scope.filtro.semana) { $scope.filtros.semana = $scope.filtro.semana; }
		if ($scope.filtro.temporada) { $scope.filtros.temporada = $scope.filtro.temporada; }
		$scope.recetasFiltradas = Estadisticas.getList({ mes: $scope.filtros.mes, semana: $scope.filtros.semana, temporada: $scope.filtros.temporada, usuario: $cookieStore.get('user') }).$object;
	};
	$scope.iniciaDatos();
}])

.controller('reportesCtrl', ['$scope', '$cookieStore', 'Reportes', function($scope, $cookieStore, Reportes) {
	$scope.filtro = { desde: '', hasta: '', ingredientes: '', calorias: '', receta: '', grupo: '', usuario: '' };
	$scope.filtrar = function() {
		$scope.filtros = {};
		if ($scope.filtro.desde) { $scope.filtros.desde = $scope.filtro.desde; }
		if ($scope.filtro.hasta) { $scope.filtros.hasta = $scope.filtro.hasta; }
		if ($scope.filtro.ingredientes) { $scope.filtros.ingredientes = $scope.filtro.ingredientes; }
		if ($scope.filtro.calorias) { $scope.filtros.calorias = $scope.filtro.calorias; }
		if ($scope.filtro.receta) { $scope.filtros.receta = $scope.filtro.receta; }
		if ($scope.filtro.grupo) { $scope.filtros.grupo = $scope.filtro.grupo; }
		if ($scope.filtro.usuario) { $scope.filtros.usuario = $scope.filtro.usuario; }
		$scope.recetasFiltradas = Reportes.getList({ user: $cookieStore.get('user'), desde: $scope.filtros.desde, hasta: $scope.filtros.hasta, ingredientes: $scope.filtros.ingredientes, calorias: $scope.filtros.calorias, receta: $scope.filtros.receta, grupo: $scope.filtros.grupo, usuario: $scope.filtros.usuario }).$object;
	};
	$scope.filtrarRecetasNuevas = function(data) {
		$scope.recetasNuevas = Reportes.getList({ user:$cookieStore.get('user'), recetasNuevas: true }).$object;
	};
}])

.controller('recomendacionCtrl', ['$scope', '$cookieStore', 'Recomendacion', function($scope, $cookieStore, Recomendacion) {
	$scope.recomendacionPuntaje = function() {
		$scope.recetas = Recomendacion.getList({ usuario: $cookieStore.get('user'), tipo: 'puntaje', tipoComida: $scope.tipoComida }).$object;
	};
	$scope.recomendacionPiramide = function() {
		$scope.recetas = Recomendacion.getList({ usuario: $cookieStore.get('user'), tipo: 'piramide', tipoComida: $scope.tipoComida }).$object;
	};
}]);