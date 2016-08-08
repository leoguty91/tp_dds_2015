'use strict';

angular
.module('myApp.factory', [])

.factory('Usuarios', ['Restangular', function(Restangular) {
	return Restangular.service('usuarios');
}])

.factory('Recetas', ['Restangular', function(Restangular) {
	return Restangular.service('recetas');
}])

.factory('Grupos', ['Restangular', function(Restangular) {
	return Restangular.service('grupos');
}])

.factory('Planificaciones', ['Restangular', function(Restangular) {
	return Restangular.service('planificaciones');
}])

.factory('Calificaciones', ['Restangular', function(Restangular) {
	return Restangular.service('calificaciones');
}])

.factory('Estadisticas', ['Restangular', function(Restangular) {
	return Restangular.service('estadisticas');
}])

.factory('Reportes', ['Restangular', function(Restangular) {
	return Restangular.service('reportes');
}])

.factory('Recomendacion', ['Restangular', function(Restangular) {
	return Restangular.service('recomendacion');
}])

.factory('Login', ['Restangular', function(Restangular) {
	return Restangular.service('login');
}]);