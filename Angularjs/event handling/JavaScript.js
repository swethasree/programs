var app = angular.module("myModule", [])
                 .controller("myController",function($scope)
                 {
                     var tech = [
                         { name: "C#", likes:0, dislikes:0 },
                         { name: "C#", likes:0, dislikes:0 },
                         { name: "Java", likes:0, dislikes:0 },
                         { name: "sql", likes: 0, dislikes:0 },
                         { name: "oracle", likes:0, dislikes:0 },
                     ];
                     $scope.tech = "tech";
                     $scope.incrementLikes = function (technologies)
                     {
                         technologies.likes++;
                     }
                     $scope.incrementdisLikes = function (technologies)
                     {
                         technologies.dislikes++;
                     }
                 });