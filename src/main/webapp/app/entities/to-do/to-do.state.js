(function() {
    'use strict';

    angular
        .module('hipToDoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('to-do', {
            parent: 'entity',
            url: '/to-do',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'hipToDoApp.toDo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/to-do/to-dos.html',
                    controller: 'ToDoController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('toDo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('to-do-detail', {
            parent: 'to-do',
            url: '/to-do/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'hipToDoApp.toDo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/to-do/to-do-detail.html',
                    controller: 'ToDoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('toDo');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ToDo', function($stateParams, ToDo) {
                    return ToDo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'to-do',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('to-do-detail.edit', {
            parent: 'to-do-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/to-do/to-do-dialog.html',
                    controller: 'ToDoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ToDo', function(ToDo) {
                            return ToDo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('to-do.new', {
            parent: 'to-do',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/to-do/to-do-dialog.html',
                    controller: 'ToDoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                todoName: null,
                                todoDescription: null,
                                creationDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('to-do', null, { reload: 'to-do' });
                }, function() {
                    $state.go('to-do');
                });
            }]
        })
        .state('to-do.edit', {
            parent: 'to-do',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/to-do/to-do-dialog.html',
                    controller: 'ToDoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ToDo', function(ToDo) {
                            return ToDo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('to-do', null, { reload: 'to-do' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('to-do.delete', {
            parent: 'to-do',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/to-do/to-do-delete-dialog.html',
                    controller: 'ToDoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ToDo', function(ToDo) {
                            return ToDo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('to-do', null, { reload: 'to-do' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
