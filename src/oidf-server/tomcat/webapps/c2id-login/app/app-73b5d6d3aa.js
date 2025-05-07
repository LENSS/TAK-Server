(function() {
    'use strict';

    angular
        .module('C2idLoginApp', [
            'ngStorage',
            'tmh.dynamicLocale',
            'pascalprecht.translate',
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngRoute',
            'ngCacheBuster',
            'angular-loading-bar'
        ])
		.config(config)
        .run(run);

    config.$inject = ['$routeProvider', '$locationProvider', 'httpRequestInterceptorCacheBusterProvider'];
    run.$inject = ['$rootScope', '$location', 'Base64', 'translationHandler', 'VERSION', 'DEBUG_INFO_ENABLED'];

    function config($routeProvider, $locationProvider, httpRequestInterceptorCacheBusterProvider) {
		//Cache everything except rest api requests
        httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api.*/], true);
		$locationProvider.html5Mode(true).hashPrefix('!');

		$routeProvider
			.when('/', {
				templateUrl: 'app/home/home.html',
				controller: 'HomeController'
			})
			.when('/login', {
				templateUrl: 'app/login/login.html',
				controller: 'LoginController'
			})
			.when('/consent', {
				templateUrl: 'app/consent/consent.html',
				controller: 'ConsentController'
			})
			.when('/err/:msg?', {
				templateUrl: 'app/error/error.html',
				controller: 'ErrorController'
			})
			.otherwise({
				redirectTo: '/'
			});
    }

    function run($rootScope, $location, Base64, translationHandler, VERSION, DEBUG_INFO_ENABLED) {
		$rootScope.VERSION = VERSION;
		$rootScope.DEBUG_INFO_ENABLED = DEBUG_INFO_ENABLED;
		translationHandler.initialize();

		$rootScope.$on('event:switch-action', function(event, c2idResponse) {
			$rootScope.metadata = angular.copy($location.search()) || {};
			$rootScope.consentedScopes = ['openid'];
			$rootScope.consentedClaims = [];
			$rootScope.metadata.authzResponse = c2idResponse;
			$rootScope.authenticationError = false;
			console.log("Received '" + c2idResponse.type + "' message: ", c2idResponse);

			if (c2idResponse.type === "auth") {
				$location.path('/login').search('authSessionId', c2idResponse.sid);
			} else if (c2idResponse.type === "consent") {
				if (c2idResponse.sub_session) {
					// Set/update subject session cookie
					$rootScope.authenticatedUser = c2idResponse.sub_session.data;
				}
				if (c2idResponse.scope && c2idResponse.scope.consented) {
					$rootScope.consentedScopes = $rootScope.consentedScopes.concat(angular.copy(c2idResponse.scope.consented));
				}
				if (c2idResponse.claims && c2idResponse.claims.consented) {
					$rootScope.consentedClaims = $rootScope.consentedClaims.concat(angular.copy(c2idResponse.claims.consented.voluntary));
					$rootScope.consentedClaims = $rootScope.consentedClaims.concat(c2idResponse.claims.consented.essential);
				}
				$location.path('/consent').search('authSessionId', c2idResponse.sid);
			} else if (c2idResponse.type === "response") {
				// Relays the final OpenID authentication response back to the client
				if (c2idResponse.mode === "query" || c2idResponse.mode === "fragment" || c2idResponse.mode === "query.jwt" || c2idResponse.mode === "fragment.jwt") {
					console.log("Redirection URI: " + c2idResponse.parameters.uri);
					window.location.href = c2idResponse.parameters.uri;
				} else if (c2idResponse.mode === "form_post" || c2idResponse.mode === "form_post.jwt") {
					submitForm(c2idResponse.parameters);
				}
			} else {
				handleAuthError($rootScope, $location, Base64, c2idResponse.error_description);
			}
		});

		$rootScope.$on('event:auth-error', function(event, msg) {
			handleAuthError($rootScope, $location, Base64, msg);
		});
    }

	function submitForm(params) {
		var form1 = document.getElementById('Form1');
		form1.setAttribute("action", params.uri);
		for (var el in params.form) {
			var hidden = document.createElement('input');
			hidden.type = "hidden";
			hidden.name = el;
			hidden.value = params.form[el];
			form1.appendChild(hidden);
		}
		form1.submit();
	}

	function handleAuthError($rootScope, $location, Base64, message) {
		var msg = message || "Authentication failed.";
		console.error(msg);
		$location.path('/err/' + Base64.encode(msg));
		$rootScope.authenticationError = true;
	}
})();

(function() {
    'use strict';

    angular
        .module('C2idLoginApp')
        .factory('Auth', Auth);

    Auth.$inject = ['$resource'];

    function Auth ($resource) {
		return $resource('api', {}, {
			'pingServer': {
				url: 'api/meta',
				method: 'GET'
			},
			'initAuthRequest': {
				url: 'api/initAuthRequest',
				method: 'POST'
			},
			'authenticateSubject': {
				url: 'api/authenticateSubject',
				method: 'POST'
			},
			'verifySubject': {
				url: 'api/verifySubject',
				method: 'GET'
			},
			'logoutSession': {
				url: 'api/logoutSession',
				method: 'POST'
			},
			'logoutSessionConfirm': {
				url: 'api/logoutSessionConfirm',
				method: 'PUT'
			},
			'updateAuthRequest': {
				url: 'api/updateAuthRequest/:authSessionId',
				method: 'PUT'
			},
			'cancelAuthRequest': {
				url: 'api/cancelAuthRequest/:authSessionId',
				method: 'DELETE'
			},
			'logoutSubject': {
				url: 'api/logoutSubject/:sid',
				method: 'DELETE'
			}
		});
    }
})();

(function() {
    'use strict';

    angular
        .module('C2idLoginApp')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$rootScope', '$scope', '$location', 'Auth'];

    function LoginController ($rootScope, $scope, $location, Auth) {
		$scope.fieldErrors = {};
        $scope.authenticationError = false;

		var handleLdapAuthHttpError = function (err) {
			if (err.data && err.data.fieldErrors) {
				angular.forEach(err.data.fieldErrors, function (val) {
					$scope.fieldErrors[val.field] = "error." + val.message;
				});
			}
			$scope.authenticationError = true;
		};

        $scope.login = function (valid) {
			if (valid) {
				var authSessionId = $location.search().authSessionId;
				if (!authSessionId) {
					$rootScope.$broadcast('event:auth-error', 'The authorisation session identifier is missing.');
				} else {
					Auth.authenticateSubject({
						authSessionId: authSessionId
					}, {
						username: $scope.username,
						password: $scope.password
					}).$promise.then(function (c2idResponse) {
						$scope.authenticationError = false;
						$rootScope.authenticatedUser = c2idResponse.data;
						$rootScope.metadata.authenticatedUser = c2idResponse.data;
						$rootScope.$broadcast('event:switch-action', c2idResponse);
					}).catch(function (err) {
						handleLdapAuthHttpError(err);
					});
				}
			} else {
				handleLdapAuthHttpError({});
			}
        };

        $scope.cancel = function () {
            $scope.authenticationError = false;
			var exit = $location.search().redirect_uri;
			var delim = exit.indexOf("?") < 0 ? "?" : "&";
			window.location.href = exit + delim + "state=" + $location.search().state + "&error=access_denied" +
					"&error_description=The user canceled the authentication.";
        };
    }
})();

(function () {
    'use strict';

    angular
        .module('C2idLoginApp')
        .factory('JhiLanguageService', JhiLanguageService);

    JhiLanguageService.$inject = ['$q', '$http', '$translate', 'LANGUAGES'];

    function JhiLanguageService ($q, $http, $translate, LANGUAGES) {
        var service = {
            getAll: getAll,
            getCurrent: getCurrent
        };

        return service;

        function getAll () {
            var deferred = $q.defer();
            deferred.resolve(LANGUAGES);
            return deferred.promise;
        }

        function getCurrent () {
            var deferred = $q.defer();
            var language = $translate.storage().get('NG_TRANSLATE_LANG_KEY');

            deferred.resolve(language);

            return deferred.promise;
        }
    }
})();

(function() {
    'use strict';

    angular
        .module('C2idLoginApp')
        .filter('findLanguageFromKey', findLanguageFromKey);

    function findLanguageFromKey() {
        return findLanguageFromKeyFilter;

        function findLanguageFromKeyFilter(lang) {
            return {
                'ca': 'Català',
                'cs': 'Český',
                'da': 'Dansk',
                'de': 'Deutsch',
                'el': 'Ελληνικά',
                'en': 'English',
                'es': 'Español',
                'et': 'Eesti',
                'fr': 'Français',
                'gl': 'Galego',
                'hu': 'Magyar',
                'hi': 'हिंदी',
                'hy': 'Հայերեն',
                'it': 'Italiano',
                'ja': '日本語',
                'ko': '한국어',
                'mr': 'मराठी',
                'nl': 'Nederlands',
                'pl': 'Polski',
                'pt-br': 'Português (Brasil)',
                'pt-pt': 'Português',
                'ro': 'Română',
                'ru': 'Русский',
                'sk': 'Slovenský',
                'sr': 'Srpski',
                'sv': 'Svenska',
                'ta': 'தமிழ்',
                'th': 'ไทย',
                'tr': 'Türkçe',
                'vi': 'Tiếng Việt',
                'zh-cn': '中文（简体）',
                'zh-tw': '繁體中文'
            }[lang];
        }
    }
})();

(function() {
    'use strict';

    angular
        .module('C2idLoginApp')
        .controller('JhiLanguageController', JhiLanguageController);

    JhiLanguageController.$inject = ['$translate', 'JhiLanguageService', 'tmhDynamicLocale'];

    function JhiLanguageController ($translate, JhiLanguageService, tmhDynamicLocale) {
        var vm = this;

        vm.changeLanguage = changeLanguage;
        vm.languages = null;

        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });

        function changeLanguage (languageKey) {
            $translate.use(languageKey);
            tmhDynamicLocale.set(languageKey);
        }
    }
})();

(function () {
    'use strict';

    angular
        .module('C2idLoginApp')

        /*
         Languages codes are ISO_639-1 codes, see http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
         They are written in English to avoid character encoding issues (not a perfect solution)
         */
        .constant('LANGUAGES', [
            'en'
        ]
    );
})();

(function() {
    'use strict';

    angular
        .module('C2idLoginApp')
        .directive('navbar', Navbar);

    function Navbar () {
		return {
			restrict: 'AE',
			templateUrl: 'app/home/navbar.html'
		};
    }
})();

(function() {
    'use strict';

    angular
        .module('C2idLoginApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$rootScope', '$scope', '$location', '$cookies', 'Auth'];

    function HomeController ($rootScope, $scope, $location, $cookies, Auth) {
		$scope.noconnection = true;
		$scope.hasQueryString = true;

		if ($location.search().response_type) {
			var qs = $location.absUrl();
			Auth.initAuthRequest({
				qs: qs.substring(qs.indexOf("?") + 1)
			}, function (c2idResponse) {
				$rootScope.$broadcast('event:switch-action', c2idResponse);
			}, function (err) {
				$rootScope.$broadcast('event:auth-error', err.data.message);
			});
		} else {
			$scope.hasQueryString = false;
			if ($scope.DEBUG_INFO_ENABLED) {
				Auth.pingServer(function (data) {
					$scope.noconnection = false;
					$rootScope.metadata = data;
				}, function () {
					$scope.noconnection = true;
				});
			}
			Auth.verifySubject(function (c2idResponse) {
				$rootScope.authenticatedUser = c2idResponse.data;
			}, function (err) {
				$scope.hasQueryString = false;
			});
		}

		$scope.logout = function () {
			Auth.logoutSession({qs: ""}, function (c2idResponse) {
				if (c2idResponse.type && c2idResponse.type === "confirm") {
					$scope.logoutPrompt = true;
					$scope.logoutError = false;
				} else if (c2idResponse.type && c2idResponse.type === "error" || c2idResponse.error) {
					$scope.logoutPrompt = false;
					$scope.logoutError = c2idResponse.error_description || c2idResponse.error;
				}
			}, function (err) {
				$scope.logoutError = err.data.message;
				console.error(err.data.message);
			});
		};

		$scope.logoutConfirm = function () {
			Auth.logoutSessionConfirm({qs: ""}, function (c2idResponse) {
				if (c2idResponse.type && c2idResponse.type === "end") {
					if (c2idResponse.post_logout_redirect_uri) {
						window.location.href = c2idResponse.post_logout_redirect_uri;
					} else {
						$scope.logoutEnd = true;
						$scope.logoutPrompt = false;
						$scope.logoutError = false;
						delete $rootScope.authenticatedUser;
					}
				} else {
					console.log(c2idResponse);
				}
			}, function (err) {
				$scope.logoutError = err.data.message;
				console.error(err.data.message);
			});
		};

		$scope.logoutCancel = function () {
			$scope.logoutPrompt = false;
		};
    }
})();

(function() {
    'use strict';

    angular
        .module('C2idLoginApp')
        .controller('ErrorController', ErrorController);

    ErrorController.$inject = ['$scope', '$routeParams', 'Base64'];

    function ErrorController ($scope, $routeParams, Base64) {
		$scope.errorMessage = Base64.decode($routeParams.msg);
    }
})();

(function() {
    'use strict';

    angular
        .module('C2idLoginApp')
        .controller('ConsentController', ConsentController);

    ConsentController.$inject = ['$rootScope', '$scope', '$location', '$cookies', 'Auth'];

    function ConsentController ($rootScope, $scope, $location, $cookies, Auth) {

		$scope.addRemoveScopes = function (scope, isChecked) {
			if (isChecked) {
				$rootScope.consentedScopes.push(scope);
			} else {
				$rootScope.consentedScopes = $rootScope.consentedScopes.filter(function (value) {
					return value !== scope;
				});
			}
		};

		$scope.addRemoveClaims = function (claim, isChecked) {
			if (isChecked) {
				$rootScope.consentedClaims.push(claim);
			} else {
				$rootScope.consentedClaims = $rootScope.consentedClaims.filter(function (value) {
					return value !== claim;
				});
			}
		};

		$scope.submitConsent = function () {
			Auth.updateAuthRequest({
				authSessionId: $location.search().authSessionId
			}, {
				scope: $rootScope.consentedScopes,
				claims: $rootScope.consentedClaims
			}, function (c2idResponse) {
				$rootScope.$broadcast('event:switch-action', c2idResponse);
			}, function (err) {
				$rootScope.$broadcast('event:auth-error', err.data.message);
			});
		};

		$scope.denyAuthorization = function () {
			Auth.cancelAuthRequest({
				authSessionId: $location.search().authSessionId
			}, {}, function (c2idResponse) {
				$rootScope.$broadcast('event:switch-action', c2idResponse);
			}, function (err) {
				$rootScope.$broadcast('event:auth-error', err.data.message);
			});
		};

		$scope.logoutSubject = function () {
			Auth.logoutSubject(function () {
				$rootScope.authenticatedUser = null;
				$scope.authenticationError = false;
				var qs = $location.absUrl();
				Auth.initAuthRequest({
					qs: qs.substring(qs.indexOf("?") + 1)
				}, function (c2idResponse) {
					$rootScope.$broadcast('event:switch-action', c2idResponse);
				}, function (err) {
					$rootScope.$broadcast('event:auth-error', err.data.message);
				});
			}, function (err) {
				console.error(err.data);
			});
		};
    }
})();

(function() {
    'use strict';

    angular
        .module('C2idLoginApp')
        .factory('translationStorageProvider', translationStorageProvider);

    translationStorageProvider.$inject = ['$cookies', '$log', 'LANGUAGES'];

    function translationStorageProvider($cookies, $log, LANGUAGES) {
        return {
            get: get,
            put: put
        };

        function get(name) {
            if (LANGUAGES.indexOf($cookies.getObject(name)) === -1) {
                $log.info('Resetting invalid cookie language "' + $cookies.getObject(name) + '" to preferred language "en"');
                $cookies.putObject(name, 'en');
            }
            return $cookies.getObject(name);
        }

        function put(name, value) {
            $cookies.putObject(name, value);
        }
    }
})();

(function() {
    'use strict';

    angular
        .module('C2idLoginApp')
        .factory('translationHandler', translationHandler)
        .config(translationConfig);

    translationConfig.$inject = ['$translateProvider', 'tmhDynamicLocaleProvider'];
    translationHandler.$inject = ['$rootScope', '$window', '$translate'];

    function translationHandler($rootScope, $window, $translate) {
        return {
            initialize: initialize,
            updateTitle: updateTitle
        };

        function initialize() {
            // if the current translation changes, update the window title
            var translateChangeSuccess = $rootScope.$on('$translateChangeSuccess', function() {
                updateTitle();
            });

            $rootScope.$on('$destroy', function () {
                if(angular.isDefined(translateChangeSuccess) && translateChangeSuccess !== null){
                    translateChangeSuccess();
                }
            });
        }

        // update the window title using params in the following
        // precedence
        // 1. titleKey parameter
        // 2. $state.$current.data.pageTitle (current state page title)
        // 3. 'global.title'
        function updateTitle(titleKey) {
            $translate(titleKey || 'global.title').then(function (title) {
                $window.document.title = title;
            }).catch(function () {});
        }
    }

    function translationConfig($translateProvider, tmhDynamicLocaleProvider) {
        // Initialize angular-translate
        $translateProvider.useLoader('$translateStaticFilesLoader', {
			prefix: 'i18n/',
			suffix: '.json'
        });

        $translateProvider.preferredLanguage('en');
        $translateProvider.useStorage('translationStorageProvider');
        $translateProvider.useSanitizeValueStrategy('escaped');
        $translateProvider.addInterpolation('$translateMessageFormatInterpolation');

        tmhDynamicLocaleProvider.localeLocationPattern('i18n/angular-locale_{{locale}}.js');
        tmhDynamicLocaleProvider.useCookieStorage();
        tmhDynamicLocaleProvider.storageKey('NG_TRANSLATE_LANG_KEY');
    }
})();

(function() {
    'use strict';

    angular
        .module('C2idLoginApp')
        .config(localStorageConfig);

    localStorageConfig.$inject = ['$localStorageProvider', '$sessionStorageProvider'];

    function localStorageConfig($localStorageProvider, $sessionStorageProvider) {
        $localStorageProvider.setKeyPrefix('c2id-');
        $sessionStorageProvider.setKeyPrefix('c2id-');
    }
})();

(function() {
    'use strict';

    angular
        .module('C2idLoginApp')
        .config(compileServiceConfig);

    compileServiceConfig.$inject = ['$compileProvider','DEBUG_INFO_ENABLED'];

    function compileServiceConfig($compileProvider,DEBUG_INFO_ENABLED) {
        // disable debug data on prod profile to improve performance
        $compileProvider.debugInfoEnabled(DEBUG_INFO_ENABLED);

        /*
        If you wish to debug an application with this information
        then you should open up a debug console in the browser
        then call this method directly in this console:

		angular.reloadWithDebugInfo();
		*/
    }
})();

(function() {
    /*jshint bitwise: false*/
    'use strict';

    angular
        .module('C2idLoginApp')
        .factory('Base64', Base64);

    function Base64 () {
        var keyStr = 'ABCDEFGHIJKLMNOP' +
            'QRSTUVWXYZabcdef' +
            'ghijklmnopqrstuv' +
            'wxyz0123456789+/' +
            '=';

        var service = {
            decode : decode,
            encode : encode
        };

        return service;

        function encode (input) {
            var output = '',
                chr1, chr2, chr3,
                enc1, enc2, enc3, enc4,
                i = 0;

            while (i < input.length) {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);

                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;

                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }

                output = output +
                    keyStr.charAt(enc1) +
                    keyStr.charAt(enc2) +
                    keyStr.charAt(enc3) +
                    keyStr.charAt(enc4);
            }

            return output;
        }

        function decode (input) {
            var output = '',
                chr1, chr2, chr3,
                enc1, enc2, enc3, enc4,
                i = 0;

            // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, '');

            while (i < input.length) {
                enc1 = keyStr.indexOf(input.charAt(i++));
                enc2 = keyStr.indexOf(input.charAt(i++));
                enc3 = keyStr.indexOf(input.charAt(i++));
                enc4 = keyStr.indexOf(input.charAt(i++));

                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;

                output = output + String.fromCharCode(chr1);

                if (enc3 !== 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if (enc4 !== 64) {
                    output = output + String.fromCharCode(chr3);
                }
            }

            return output;
        }
    }
})();
(function () {
    'use strict';
    // DO NOT EDIT THIS FILE, EDIT THE GULP TASK NGCONSTANT SETTINGS INSTEAD WHICH GENERATES THIS FILE
    angular
        .module('C2idLoginApp')
        .constant('VERSION', "3.4.5")
        .constant('DEBUG_INFO_ENABLED', false)
;
})();
(function(){'use strict';angular.module('C2idLoginApp').run(['$templateCache', function($templateCache) {$templateCache.put('app/consent/consent.html','<div ng-cloak><div class="row"><div class="col-md-12 text-center"><div class="text-center" ng-show="authenticatedUser"><img class="img-circle" width="50" height="50" alt="Profile picture" src="data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCABQAFADASIAAhEBAxEB/8QAGwABAAIDAQEAAAAAAAAAAAAAAAEGBAUHAgP/xAAtEAABAwIEBAYBBQAAAAAAAAABAAIDBBEFITGBEkFRYQYTQnGRwVIiMqGx8P/EABQBAQAAAAAAAAAAAAAAAAAAAAD/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwDreSlN03QRzU2TdN0CybL7QUdTOLwwSvb1a0kfOimejqYBeaCVg6uabfOiD4bJsm6boGybJum6AiIgK0YBgrPKbU1jeJzs2MIyA6nutBhcAqcRp4nD9Ln5jsM7fC6H7IAAAAFgAhAIsdFKIK3j2CMdE+po28L25uYBYO9h1VWXTVz3F4BTYlURNyaHZDoDnZBiIiICIiDMwaUQ4rTSO0D7Z8r5fa6DsuYjVXPAMWZVxMhncBUtyzP7+/ug3abKEQTsqBjkomxape0i3Fw362AH0rNj2LMo4nRQuDqlwtYem/NUo63OpQSiIgbpui9QxvmlZHGCXuNgEH2oKOaun8uAX5ucdArhh2CUtGA4tEs35vGh7DksjC6JlBStiZm7VzralZqBum6Ig1WI4LS1gc4NEUx9bBqe45qn19HNQz+XOLc2uGhXRFh4pRMr6V0T8natdbQoOf7puvU0b4ZXxyAh7TYheUBb/wAIUvmVMlQ4ZRjhb7nX/d1oFdPCkYZhLXfm9x+Db6QblETZAUKVCCVCnZQgqfi+l4KmOoaMpBwu9xp/H9Kvq6eK4+PCXOPoe1w3NvtUtB//2Q=="><h3 class="media-heading">{{authenticatedUser.name}}</h3><h5><tt>{{authenticatedUser.email}}</tt></h5><a href="" ng-click="logoutSubject()">{{\'global.logout\' | translate}}</a></div><form id="Form1" method="post"></form><h4 ng-show="metadata.authzResponse.client"><strong>{{metadata.authzResponse.client.name}}</strong> (#{{metadata.authzResponse.client.client_id}}) <span translate="consent.subtitle">wants permission to access your account</span>:</h4><div class="panel panel-default" ng-show="metadata.authzResponse.scope"><div class="panel-heading"><span translate="consent.scope">Scope</span></div><div class="panel-body"><div ng-show="metadata.authzResponse.scope.new.length" ng-init="scopeValues[\'openid\'] = true"><strong translate="consent.new">Newly requested</strong>: &nbsp; <span ng-repeat="scope in metadata.authzResponse.scope.new"><label><input type="checkbox" ng-model="scopeValues[scope]" ng-disabled="scope === \'openid\'" ng-click="addRemoveScopes(scope, !scopeValues[scope])"> <tt>{{scope}}</tt></label>&nbsp;</span></div><div ng-show="metadata.authzResponse.scope.consented.length"><strong translate="consent.previous">Previously consented</strong>: &nbsp; <span ng-repeat="scope in metadata.authzResponse.scope.consented"><label><input type="checkbox" ng-init="scopeValues[scope] = true" ng-model="scopeValues[scope]" ng-disabled="scope === \'openid\'" ng-click="addRemoveScopes(scope, !scopeValues[scope])"> <tt>{{scope}}</tt></label>&nbsp;</span></div></div></div><div class="panel panel-default" ng-show="metadata.authzResponse.claims"><div class="panel-heading"><span translate="consent.claims">Claims</span></div><div class="panel-body"><div ng-show="metadata.authzResponse.claims.new.essential.length"><strong translate="consent.new">Newly requested</strong> ({{\'consent.essential\' | translate}}): &nbsp; <span ng-repeat="claim in metadata.authzResponse.claims.new.essential"><label><input type="checkbox" ng-model="claimValues[claim]" ng-click="addRemoveClaims(claim, !claimValues[claim])"> <tt>{{claim}}</tt></label>&nbsp;</span></div><div ng-show="metadata.authzResponse.claims.new.voluntary.length"><strong translate="consent.new">Newly requested</strong> ({{\'consent.voluntary\' | translate}}): &nbsp; <span ng-repeat="claim in metadata.authzResponse.claims.new.voluntary"><label><input type="checkbox" ng-model="claimValues[claim]" ng-click="addRemoveClaims(claim, !claimValues[claim])"> <tt>{{claim}}</tt></label>&nbsp;</span></div><div ng-show="metadata.authzResponse.claims.consented.essential.length"><strong translate="consent.previous">Previously consented</strong> ({{\'consent.essential\' | translate}}): &nbsp; <span ng-repeat="claim in metadata.authzResponse.claims.consented.essential"><label><input type="checkbox" ng-init="claimValues[claim] = true" ng-model="claimValues[claim]" ng-click="addRemoveClaims(claim, !claimValues[claim])"> <tt>{{claim}}</tt></label>&nbsp;</span></div><div ng-show="metadata.authzResponse.claims.consented.voluntary.length"><strong translate="consent.previous">Previously consented</strong> ({{\'consent.voluntary\' | translate}}): &nbsp; <span ng-repeat="claim in metadata.authzResponse.claims.consented.voluntary"><label><input type="checkbox" ng-init="claimValues[claim] = true" ng-model="claimValues[claim]" ng-click="addRemoveClaims(claim, !claimValues[claim])"> <tt>{{claim}}</tt></label>&nbsp;</span></div></div></div><div class="row" ng-show="metadata.authzResponse.scope"><div class="col-xs-6"><button type="submit" class="btn btn-primary btn-block" ng-click="submitConsent()" translate="consent.form.button">Authorize client</button></div><div class="col-xs-6"><button type="reset" class="btn btn-primary btn-block" ng-click="denyAuthorization()" translate="global.cancel">Cancel</button></div></div></div></div><div class="voffset5 text-center" ng-if="DEBUG_INFO_ENABLED"><a href="" class="btn btn-default" ng-click="showMetadata = !showMetadata">show debug info</a><pre class="text-left voffset2" ng-show="showMetadata"><code>{{metadata | json}}</code></pre></div></div>');
$templateCache.put('app/error/error.html','<div ng-cloak><div class="row"><div class="col-md-12 text-center"><h1 translate="error.title">Invalid request</h1><div class="voffset4" ng-show="errorMessage"><div class="alert alert-danger"><h4>{{errorMessage}}</h4></div></div></div></div></div>');
$templateCache.put('app/home/home.html','<div ng-cloak><div class="row"><div class="col-md-12 text-center" ng-hide="hasQueryString" ng-cloak><div ng-hide="authenticatedUser"><h1 translate="global.title">Login with Connect2id</h1><div class="alert alert-danger voffset5" ng-hide="logoutEnd"><h4 translate="global.messages.badrequest">400 - Invalid OpenID Connect authentication request: Missing query string.</h4></div><div ng-show="logoutEnd" class="alert alert-success"><h4 translate="logout.success">You have been logged out!</h4></div></div><div class="text-center" ng-show="authenticatedUser"><h1 translate="logout.title">Logout from Connect2id</h1><img class="img-circle" width="50" height="50" alt="Profile picture" src="data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCABQAFADASIAAhEBAxEB/8QAGwABAAIDAQEAAAAAAAAAAAAAAAEGBAUHAgP/xAAtEAABAwIEBAYBBQAAAAAAAAABAAIDBBEFITGBEkFRYQYTQnGRwVIiMqGx8P/EABQBAQAAAAAAAAAAAAAAAAAAAAD/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwDreSlN03QRzU2TdN0CybL7QUdTOLwwSvb1a0kfOimejqYBeaCVg6uabfOiD4bJsm6boGybJum6AiIgK0YBgrPKbU1jeJzs2MIyA6nutBhcAqcRp4nD9Ln5jsM7fC6H7IAAAAFgAhAIsdFKIK3j2CMdE+po28L25uYBYO9h1VWXTVz3F4BTYlURNyaHZDoDnZBiIiICIiDMwaUQ4rTSO0D7Z8r5fa6DsuYjVXPAMWZVxMhncBUtyzP7+/ug3abKEQTsqBjkomxape0i3Fw362AH0rNj2LMo4nRQuDqlwtYem/NUo63OpQSiIgbpui9QxvmlZHGCXuNgEH2oKOaun8uAX5ucdArhh2CUtGA4tEs35vGh7DksjC6JlBStiZm7VzralZqBum6Ig1WI4LS1gc4NEUx9bBqe45qn19HNQz+XOLc2uGhXRFh4pRMr6V0T8natdbQoOf7puvU0b4ZXxyAh7TYheUBb/wAIUvmVMlQ4ZRjhb7nX/d1oFdPCkYZhLXfm9x+Db6QblETZAUKVCCVCnZQgqfi+l4KmOoaMpBwu9xp/H9Kvq6eK4+PCXOPoe1w3NvtUtB//2Q=="><h3 class="media-heading">{{authenticatedUser.name}}</h3><h5><tt>{{authenticatedUser.email}}</tt></h5><div ng-show="logoutError" class="alert alert-danger"><p translate="logout.{{logoutError}}">Error</p></div><a href="" class="btn btn-info" ng-click="logout()" ng-hide="logoutPrompt">{{\'global.logout\' | translate}}</a><div ng-show="logoutPrompt"><h4 translate="logout.prompt">Do you wish to log out of the OpenID provider?</h4><a href="" class="btn btn-success" ng-click="logoutConfirm()">{{\'global.yes\' | translate}}</a> &nbsp; <a href="" class="btn btn-danger" ng-click="logoutCancel()">{{\'global.no\' | translate}}</a></div></div><div class="panel panel-default voffset5" ng-if="DEBUG_INFO_ENABLED"><div class="panel-heading"><h4 ng-hide="noconnection" translate="global.messages.connected" translate-values="{host: \'{{metadata.issuer}}\'}">Connect2id server found on {{metadata.issuer}}.</h4><h4 ng-show="noconnection" translate="global.messages.noconnection">Failed to connect to a Connect2id server.</h4></div><div class="panel-body"><a href="" class="btn btn-default" ng-click="showMetadata = !showMetadata">show debug info</a><pre class="text-left voffset2" ng-show="showMetadata"><code>{{metadata | json}}</code></pre></div></div></div></div></div>');
$templateCache.put('app/home/navbar.html','<nav class="navbar navbar-default" role="navigation" ng-cloak ng-show="DEBUG_INFO_ENABLED"><div class="container"><div class="navbar-header voffset2"><div class="navbar-brand logo"><p class="label label-primary" translate="home.subtitle" ng-show="DEBUG_INFO_ENABLED">development mode</p></div></div><div class="navbar-collapse"><div class="navbar-right voffset3"><span class="navbar-version">v{{VERSION}}</span></div></div></div></nav>');
$templateCache.put('app/login/login.html','<div ng-cloak><div class="row"><div class="col-md-12 text-center"><h1 translate="login.title">Login</h1></div></div><div class="row"><div class="col-sm-8 col-sm-offset-2 voffset3 text-center"><div class="alert alert-danger" ng-show="authenticationError"><p translate="error.authentication"><strong>Failed to log in!</strong> Please check your credentials and try again.</p></div></div></div><div class="row"><div class="col-sm-6 col-sm-offset-3"><form class="form" role="form" name="loginForm" ng-submit="login(loginForm.$valid)"><div class="form-group"><label for="username" translate="login.form.username">Login</label> <input type="text" class="form-control" ng-model="username" placeholder="{{\'login.form.username.placeholder\'| translate}}" required autofocus> <span class="label label-danger" translate="{{fieldErrors.username}}"></span></div><div class="form-group"><label for="password" translate="login.form.password">Password</label> <input type="password" class="form-control" ng-model="password" placeholder="{{\'login.form.password.placeholder\'| translate}}" required> <span class="label label-danger" translate="{{fieldErrors.password}}"></span></div><p class="help-block" translate="login.hint">Hint: Enter username "alice" and password "secret".</p><div class="row"><div class="col-xs-6"><button type="submit" class="btn btn-primary btn-block" translate="global.login">Login</button></div><div class="col-xs-6"><button type="reset" class="btn btn-primary btn-block" ng-click="cancel()" translate="global.cancel">Cancel</button></div></div></form></div></div><div class="voffset5 text-center" ng-if="DEBUG_INFO_ENABLED"><a href="" class="btn btn-default" ng-click="showMetadata = !showMetadata">show debug info</a><pre class="text-left voffset2" ng-show="showMetadata"><code>{{metadata | json}}</code></pre></div></div>');}]);})();
//# sourceMappingURL=app-73b5d6d3aa.js.map
