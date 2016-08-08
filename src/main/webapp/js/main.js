'use strict';

angular
.module('myApp', [
    'myApp.constant',
	'myApp.controllers',
	'myApp.directives',
	'myApp.factory',
	'myApp.service',
//	'ngMockE2E', // COMENTAR ESTA LINEA PARA PROBAR CON SERVIDOR
	'ngRoute',
	'ngCookies',
	'mgcrea.ngStrap',
	'restangular'
])

.config(['$routeProvider', function($routeProvider) {
	$routeProvider
		.when('/', {
			templateUrl: 'templates/principal.html',
			controller: 'principalCtrl'
		})
		.when('/login', {
			templateUrl: 'templates/login.html',
			controller: 'loginCtrl'
		})
		.when('/registrarUsuario', {
			templateUrl: 'templates/registrar.usuario.html',
			controller: 'usuarioCtrl'
		})
		.when('/modificarPerfil', {
			templateUrl: 'templates/perfil.usuario.html',
			controller: 'usuarioCtrl'
		})
		.when('/recetas', {
			templateUrl: 'templates/recetas.html',
			controller: 'recetasCtrl',
			resolve: { puedeEditar: [function() { return true; }] }
		}) 
		.when('/recetaNueva', {
			template: '<datos-receta>Nueva receta</datos-receta>',
			controller: 'recetasCtrl',
			resolve: { puedeEditar: [function() { return true; }] }
		})
		.when('/recetaModifica/:recetaId', {
			template: '<datos-receta>Modifica receta</datos-receta>',
			controller: 'recetasCtrl',
			resolve: { puedeEditar: [function() { return true; }] }
		})
		.when('/receta/:recetaId', {
			templateUrl: 'templates/receta.html',
			controller: 'recetasCtrl',
			resolve: { puedeEditar: [function() { return false; }] }
		})
		.when('/recetaExistente', {
			templateUrl: 'templates/receta.existente.html',
			controller: 'recetasCtrl',
			resolve: { puedeEditar: [function() { return true; }] }
		})
		.when('/grupos', {
			templateUrl: 'templates/grupos.html',
			controller: 'gruposCtrl'
		})
		.when('/grupo/:nombreGrupo', {
			templateUrl: 'templates/grupo.html',
			controller: 'gruposCtrl'
		})
		.when('/planificaciones', {
			templateUrl: 'templates/planificaciones.html',
			controller: 'planificarCtrl'
		})
		.when('/planificacionNueva', {
			templateUrl: 'templates/planificacion.html',
			controller: 'planificarCtrl'
		})
		.when('/planificacion/:planificacionId', {
			templateUrl: 'templates/planificacion.html',
			controller: 'planificarCtrl'
		})
		.when('/planificacionReceta/:recetaId', {
			templateUrl: 'templates/planificacion.html',
			controller: 'planificarCtrl'
		})
		.when('/estadisticas', {
			templateUrl: 'templates/estadisticas.html',
			controller: 'estadisticasCtrl'
		})
		.when('/reportes', {
			templateUrl: 'templates/reportes.html',
			controller: 'reportesCtrl'
		})
		.when('/recomendaciones', {
			templateUrl: 'templates/recomendaciones.html',
			controller: 'recomendacionCtrl'
		})
		.otherwise({
			redirectTo: '/'
		});
}])

.config(['RestangularProvider', function(RestangularProvider) {
	RestangularProvider.setBaseUrl('http://localhost:8080/tpd2s/rest');
	RestangularProvider.setFullResponse(true);
}])

.config(['$httpProvider', function($httpProvider) {
	$httpProvider.defaults.transformResponse.push(function(responseData){
        convertDateStringsToDates(responseData);
        return responseData;
    });
}])

.config(['$httpProvider', function($httpProvider) {
	$httpProvider.interceptors.push('InterceptorHTTP');
}])

.config(['$datepickerProvider', function($datepickerProvider) {
	angular.extend($datepickerProvider.defaults, {
		dateFormat: 'dd/MM/yyyy'
	})
}])

// COMENTAR TOD0 ESTE BLOQUE PARA PROBAR CON SERVIDOR
/*
.run(['$httpBackend', function($httpBackend) {
	$httpBackend.when('GET', /templates\//).passThrough();
	$httpBackend.when('POST', /login/).respond(200, { 'result': 'OK'}, {});
	//$httpBackend.when('POST', /login/).respond(400, { 'Result': 'El usuario/constraseña es incorrecto' }, {});
	$httpBackend.when('GET', /usuarios\/.+m$/).respond(200, {
		"nombre": "Juan",
		"email": "juan@hotmail.com",
		"fechaNacimiento": "1990-01-01T12:00:00-03:00",
		"altura": "",
		"sexo": "Masculino",
		"preferencias": [ "Azucar" ],
		"rutina": 2,
		"condiciones": [ "Diabetes" ]
	}, {});
	$httpBackend.when('GET', /usuarios\?/).respond(200, 
		[{
			"nombre": "juan",
			"email": "juan@hotmail.com"
		}, {
			"nombre": "leo",
			"email": "leo@hotmail.com"
		}], {});
	$httpBackend.when('PUT', /usuarios/).respond(200, { 'result': 'OK' }, {});
	$httpBackend.when('POST', /(usuarios)$/).respond(201, { 'result': 'OK' }, {});
	$httpBackend.when('GET', /(recetas\?|recetas$)/).respond(200,
			[{
				"id": 1,
				"autor": "maria@hotmail.com",
				"nombre": "Pollo al horno con papas",
				"ingredientes": [ "Pollo", "Papas"],
				"procedimiento": "...",
				"dificultad": "MEDIA",
				"temporada": "PRIMAVERA",
				"calorias": 600
			}, {
				"id": 2,
				"autor": "leo@hotmail.com",
				"nombre": "Milanesas con pure",
				"ingredientes": [ "Nalga", "Papas"],
				"procedimiento": "...",
				"dificultad": "DIFICIL",
				"temporada": "INVIERNO",
				"calorias": 800
			}], {});
	$httpBackend.when('GET', /recetas\//).respond(200, {
		"id": 1,
		"autor": "juan@hotmail.com",
		"nombre": "Asado",
		"ingredientes": [ "Asado", "Vacio"],
		"procedimiento": "...",
		"dificultad": "MEDIA",
		"temporada": "PRIMAVERA",
		"calorias": 600 }, {});
	$httpBackend.when('POST', /recetas/).respond(201, { 'result': 'OK' }, {});
	$httpBackend.when('PUT', /recetas/).respond(200, { 'result': 'OK' }, {});
	$httpBackend.when('DELETE', /recetas/).respond(200, { 'result': 'OK' }, {});
	$httpBackend.when('GET', /grupos/).respond(200, 
		[{
			"nombre": "Club vegetarianos"
		}, {
			"nombre": "Equipo de futbol"
		}], {});
	$httpBackend.when('DELETE', /grupos\/.*\/usuarios\//).respond(200, { 'result': 'OK' }, {});
	$httpBackend.when('GET', /planificaciones\//).respond(200, {
		"id": 1,
		"fecha": "2015-05-24T12:00:00-03:00",
		"receta": {
			"id": 2,
			"nombre": "Milanesas con pure"
		},
		"tipoComida": "ALMUERZO"
	}, {});
	$httpBackend.when('GET', /planificaciones.+m$/).respond(200,
		[{
			"id": 1,
			"fecha": "2015-05-24T12:00:00-03:00",
			"usuario": "a@a.com",
			"receta": {
				"id": 1,
				"nombre": "Milanesas"
			},
			"tipoComida": "ALMUERZO"
		}, {
			"id": 2,
			"fecha": "2015-05-24T12:00:00-03:00",
			"usuario": "a@a.com",
			"receta": {
				"id": 2,
				"nombre": "Café con leche"
			},
			"tipoComida": "MERIENDA"
		}, {
			"id": 3,
			"fecha": "2015-05-25T12:00:00-03:00",
			"usuario": "a@a.com",
			"receta": {
				"id": 2,
				"nombre": "Café con leche"
			},
			"tipoComida": "DESAYUNO"
		}, {
			"id": 4,
			"fecha": "2015-05-26T12:00:00-03:00",
			"usuario": "a@a.com",
			"receta": {
				"id": 3,
				"nombre": "Asado"
			},
			"tipoComida": "CENA"
		}], {});
	$httpBackend.when('POST', /planificaciones/).respond(201, { 'result': 'OK' }, {});
	$httpBackend.when('PUT', /planificaciones/).respond(201, { 'result': 'OK' }, {});
	$httpBackend.when('DELETE', /planificaciones/).respond(200, { 'result': 'OK' }, {});
	$httpBackend.when('GET', /calificaciones/).respond(200, [{
		"id": 1,
		"grupo": "Club vegetarianos",
		"receta": 1,
		"usuario": "juan@hotmail.com",
		"calificacion": 4
	}], {});
	$httpBackend.when('PUT', /calificaciones/).respond(201, { 'result': 'OK' }, {});
	$httpBackend.when('GET', /estadisticas/).respond(200,
			[{
				"id": 1,
				"nombre": "Asado",
				"puntaje": 100
			}, {
				"id": 2,
				"nombre": "Pollo al horno con papas",
				"puntaje": 90
			}, {
				"id": 3,
				"nombre": "Milanesas con pure",
				"puntaje": 80
			}], {});
	$httpBackend.when('GET', /reportes/).respond(200,
			[{
				"id": "1",
				"nombre": "Asado",
				"autor": "juan@hotmail.com"
			}, {
				"id": "2",
				"nombre": "Pollo al horno con papas",
				"autor": "maria@hotmail.com"
			}], {});
	$httpBackend.when('GET', /recomendacion\?tipo=puntaje/).respond(200,
			[{
				"id": 1,
				"nombre": "Asado",
				"puntaje": 100
			}, {
				"id": 2,
				"nombre": "Pollo al horno con papas",
				"puntaje": 90
			}, {
				"id": 3,
				"nombre": "Milanesas con pure",
				"puntaje": 80
			}], {});
	$httpBackend.when('GET', /recomendacion\?tipo=piramide/).respond(200,
			[{
				"id": 1,
				"nombre": "Ensalada cesar",
				"puntaje": 70
			}, {
				"id": 2,
				"nombre": "Ensalada de hojas verdes",
				"puntaje": 40
			}, {
				"id": 3,
				"nombre": "Sushi",
				"puntaje": 20
			}], {});
}])
*/
// FIN BLOQUE

.run(['$rootScope', '$cookies', '$location', function($rootScope, $cookies, $location) {
	$rootScope.$on( "$routeChangeStart", function(event, next, current) {
		if ($cookies.get('user') == null) {
			if (next.templateUrl === "templates/registrar.usuario.html") {
				$location.path("/registrarUsuario");
			} else if (next.templateUrl !== "templates/login.html") {
				$location.path("/login"); 
			}
		}
	});
}]);

var regexIso8601 = /(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d\.\d+)|(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d)|(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d)/;

function convertDateStringsToDates(input) {
    // Ignore things that aren't objects.
    if (typeof input !== "object") return input;

    for (var key in input) {
        if (!input.hasOwnProperty(key)) continue;

        var value = input[key];
        var match;
        // Check for string properties which look like dates.
        // TODO: Improve this regex to better match ISO 8601 date strings.
        if (typeof value === "string" && (match = value.match(regexIso8601))) {
            // Assume that Date.parse can parse ISO 8601 strings, or has been shimmed in older browsers to do so.
            var milliseconds = Date.parse(match[0]);
            if (!isNaN(milliseconds)) {
                input[key] = new Date(milliseconds);
            }
        } else if (typeof value === "object") {
            // Recurse into object
            convertDateStringsToDates(value);
        }
    }
}