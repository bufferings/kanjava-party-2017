$(".button-collapse").sideNav();

angular.module('staffApp', [ 'ngRoute', 'ngAnimate' ])

//
.filter('numberFixedLen', function() {
  return function(n, len) {
    var num = parseInt(n, 10);
    len = parseInt(len, 10);
    if (isNaN(num) || isNaN(len)) {
      return n;
    }
    num = '' + num;
    while (num.length < len) {
      num = '0' + num;
    }
    return num;
  };
})

//
.config(function($routeProvider) {
  $routeProvider.when('/', {
    controller : 'OrderController',
    templateUrl : 'view/orders.html',
    resolve : {
      orders : function($http) {
        return $http.get('/staff/api/orders/waiting');
      }
    }
  }).otherwise({
    redirectTo : '/'
  });
})

//
.controller('OrderController', function($scope, $http, $location, orders) {
  $scope.orders = orders.data;
  $scope.deliver = function(orderGroupId, orderId) {
    $http({
      method : 'POST',
      url : '/staff/api/order-groups/' + orderGroupId + '/' + orderId + '/deliver',
      headers : {
        'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
      }
    }).success(function(data, status, headers, config) {
      for (var i = 0, len = $scope.orders.length; i < len; i++) {
        if ($scope.orders[i].orderId === orderId) {
          $scope.orders.splice(i, 1);
          break;
        }
      }
    }).error(function(data, status, headers, config) {
    });
  };
});
