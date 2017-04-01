var app = angular.module('mainApp', ['ngRoute']);

app.config(function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'main.html'

           
        })
         .when('/home', {
             templateUrl: 'templates/home.html',
             controller: 'HomeCntrl'
         })
        .when('/login', {
            templateUrl: 'templates/login.html'
        })
         .when('/page', {
             templateUrl: 'templates/page.html'
         })
          .when('/contact', {
              templateUrl: 'templates/contact.html',
              controller: 'ContactCntrl'

              
          })
        .when('/home', {
            templateUrl: 'templates/home.html',
            controller: 'HomeCntrl'


        })
        .when('/products', {
            templateUrl: 'templates/products.html',
            controller: 'productCntrl'


        })
         .when('/industries', {
             templateUrl: 'templates/industries.html',
             controller: 'industryCntrl'


         })
        .otherwise({
            redirectTo: '/'
        });
});

app.controller('loginCtrl', function ($scope, $location) {


    $scope.submit = function () {

        $location.path('/login');

        
    };
    $scope.submit1 = function () {
        var uname = $scope.username;
        var password = $scope.password;
        if ($scope.username == 'admin' && $scope.password == 'admin') {
            $location.path('/page');
        } else {
            alert('Wrong Stuff');
        }

    };

});
