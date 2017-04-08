(function() {
    'use strict';

    angular
        .module('hipToDoApp')
        .controller('ToDoDialogController', ToDoDialogController);

    ToDoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ToDo'];

    function ToDoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ToDo) {
        var vm = this;

        vm.toDo = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.toDo.id !== null) {
                ToDo.update(vm.toDo, onSaveSuccess, onSaveError);
            } else {
                ToDo.save(vm.toDo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('hipToDoApp:toDoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.creationDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
