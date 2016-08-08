'user strict';

angular
.module('myApp.service', [])

.service('InterceptorHTTP', ['$rootScope', function($rootScope) {
	var service = this;

	service.request = function(config) {
		// Aca se deberia configurar si el sistema usa Tokens o Sessions
		return config;
	};

	service.responseError = function(response) {
		if (response.status >= 400 && response.status < 500) {
			$rootScope.$broadcast('interceptorError', {
				mensaje: response.data.result || 'Error inesperado'
			});
		} else if (response.status >= 500) {
			$rootScope.$broadcast('interceptorError', {
				mensaje: 'Error de servidor, intente de nuevo mas tarde'
			});
		}
		return response;
	};
}]);