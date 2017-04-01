var myApp = angular
    .module("myModule", [])
    .controller("myController", function ($scope) {
        
        var emp = {
            name: "swetha",
            salary: 1234,
            position: "software"


        };
       
        $scope.emp = emp;
    });