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
    controller : 'OrderItemController',
    templateUrl : 'view/order-items.html',
    resolve : {
      orderItems : function($http) {
        return $http.get('/staff/api/order-items/');
      }
    }
  }).otherwise({
    redirectTo : '/'
  });
})

//
.controller('OrderItemController', function($scope, $http, $location, $route, orderItems) {
  $scope.orderItems = orderItems.data;
  $scope.deliver = function(orderItemId) {
    $http({
      method : 'POST',
      url : '/staff/api/order-items/' + orderItemId + '/deliver',
      headers : {
        'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
      }
    }).success(function(data, status, headers, config) {
      for (var i = 0, len = $scope.orderItems.length; i < len; i++) {
        if ($scope.orderItems[i].orderItemId === orderItemId) {
          $scope.orderItems.splice(i, 1);
          break;
        }
      }
      Materialize.toast('お届けしました。', 4000)
    }).error(function(data, status, headers, config) {
      Materialize.toast('お届けできませんでした。', 4000)
      $route.reload();
    });
  };
});
