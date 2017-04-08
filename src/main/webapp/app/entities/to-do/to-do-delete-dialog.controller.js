(function() {
    'use strict';

    angular
        .module('hipToDoApp')
        .controller('ToDoDeleteController',ToDoDeleteController);

    ToDoDeleteController.$inject = ['$uibModalInstance', 'entity', 'ToDo'];

    function ToDoDeleteController($uibModalInstance, entity, ToDo) {
        var vm = this;

        vm.toDo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ToDo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
