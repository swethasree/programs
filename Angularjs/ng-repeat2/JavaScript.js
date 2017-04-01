var myApp = angular.module("myModule", [])
                    .controller("myController", function ($scope) {
                        var countries = [
                           {
                               name: "UK",
                               cities: [

                                      { name: "London" },
                                      { name: "Fremont" },
                                      { name: "Carrelton" }, ]
                           },
                            {
                                name: "INDIA",
                                cities: [

                                       { name: "London" },
                                       { name: "Fremont" },
                                       { name: "Carrelton" }, ]
                            },
                             {
                                 name: "USA",
                                 cities: [

                                        { name: "London" },
                                        { name: "Fremont" },
                                        { name: "Carrelton" }, ]
                             },
                              {
                                  name: "CANNADA",
                                  cities: [

                                         { name: "London" },
                                         { name: "Fremont" },
                                         { name: "Carrelton" }, ]
                              }];

                        $scope.countries = countries;



                                   
                               
                           
                        
                    });