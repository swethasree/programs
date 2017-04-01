var myApp = angular.module("myModule", [])
                    .controller("myController", function ($scope) {
                        var emp = [
                            { firstName: "swetha", lastName: "thummishetty", gender: "female", salary: "12345" },
                            { firstName: "raj", lastName: "thadem", gender: "male", salary: "3456" },
                              { firstName: "santhosh", lastName: "thummishetty", gender: "male", salary: "3556" },
                                { firstName: "rajkumar", lastName: "thadems", gender: "male", salary: "34563" }

                        ];
                        $scope.emp = emp;
                    });