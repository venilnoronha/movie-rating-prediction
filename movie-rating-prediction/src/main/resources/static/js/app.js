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

    $scope.connect = function(){
    	var socket = new SockJS('/mrp-websocket');
    	var stompClient = Stomp.over(socket);
    	stompClient.connect({}, function (frame) {
    		console.log('Connected: ' + frame);
    		stompClient.subscribe('/stream/verbatim', function(verbatim) {
    			verbatimData = JSON.parse(verbatim.body);
    			$rootScope.$broadcast('new-verbatim', verbatimData);
    		});
    	});
    }

    $scope.connect();

}]);

var searchResultCtrl = mrpApp.controller('searchResultCtrl', ['$scope', '$timeout', function($scope, $timeout) {

	$scope.movieTitle = "";
	$scope.moviePoster = "";
    $scope.verbatimList = [];

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

    $scope.$on('new-verbatim', function(e, verbatim) {
    	$scope.$apply(function() {
    		$scope.verbatimList.push(verbatim);
    		var verbatimEl = document.getElementById('verbatim');
    		$timeout(function() {
    			verbatimEl.scrollTop = verbatimEl.scrollHeight;
    		});
    	});
    });
    
}]);

mrpApp.directive('verbatim', function() {
	return {
		restrict: 'A',
		scope: {
			verbatimData : '=model'
		},
		templateUrl: '/verbatim.html'
	};
});