(function() {
    'use strict';

    angular
        .module('hipToDoApp')
        .controller('ToDoDetailController', ToDoDetailController);

    ToDoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ToDo'];

    function ToDoDetailController($scope, $rootScope, $stateParams, previousState, entity, ToDo) {
        var vm = this;

        vm.toDo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('hipToDoApp:toDoUpdate', function(event, result) {
            vm.toDo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
