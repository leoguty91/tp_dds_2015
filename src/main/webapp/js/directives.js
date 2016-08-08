'use strict';

angular
.module('myApp.directives', [])

.directive('datosUsuario', [function() {
	return {
		restrict: 'E',
		templateUrl: 'templates/directive.usuario.html'
	};
}])
.directive('datosReceta', [function() {
	return {
		restrict: 'E',
		transclude: true,
		templateUrl: 'templates/directive.receta.html'
	}
}]);