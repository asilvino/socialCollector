/**
 * User controllers.
 */
define([], function() {
  'use strict';

  var LoginCtrl = function($scope, $location, userService) {
    $scope.credentials = {};

    $scope.login = function(credentials) {
      userService.loginUser(credentials).then(function(user) {
        $location.path('/users');
      });
    };
  };
  LoginCtrl.$inject = ['$scope', '$location', 'userService'];

  return {
    LoginCtrl: LoginCtrl
  };

});
