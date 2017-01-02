var mrpApp = angular.module('mrpApp', []);

var searchCtrl = mrpApp.controller('searchCtrl', ['$rootScope', '$scope', '$http', '$timeout', function($rootScope, $scope, $http, $timeout) {

    $scope.movieName = "";
    $scope.queryTimer = null;

    $scope.onMovieNameUpdate = function() {
    	if (!!$scope.queryTimer) {
    		$timeout.cancel($scope.queryTimer);
    		$scope.queryTimer = null;
    	}
    	$scope.queryTimer = $timeout(function() {
    		$scope._performQuery();
    		$scope.queryTimer = null;
    	}, 1000);
    };

    $scope._performQuery = function() {
    	$rootScope.$broadcast('loading');
    	$http
			.get('http://www.omdbapi.com/?t=' + $scope.movieName + '&r=json')
			.then(function(response) {
				$rootScope.$broadcast('search-result', response.data);
			});
    };

}]);

var searchResultCtrl = mrpApp.controller('searchResultCtrl', ['$scope', function($scope) {

	$scope.movieTitle = "";
	$scope.moviePoster = "";

	$scope.$on('search-result', function(e, data) {
		if (!data || !data.Title) {
			$scope.movieTitle = "Movie not found!";
			$scope.moviePoster = "";
		}
		else {
			$scope.movieTitle = data.Title + " (" + data.Year + ")";
			$scope.moviePoster = data.Poster;
		}
	});

	$scope.$on('loading', function() {
		$scope.movieTitle = "Loading...";
		$scope.moviePoster = "images/loading.gif";
	});

}]);