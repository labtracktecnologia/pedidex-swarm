(function () {
    'use strict'

    angular.module('app')
        .controller('ClienteFormController', ClienteFormController);

    ClienteFormController.$inject = ['ClienteService', '$state', '$stateParams', 'DialogBuilder'];

    function ClienteFormController(ClienteService, $state, $stateParams, DialogBuilder) {

        var vm = this;
        vm.registro = {};
        vm.error = {};
        vm.titulo = 'Novo Cliente';

        vm.salvar = salvar;

        if ($stateParams.id) {
            ClienteService.findById($stateParams.id)
                .then(function (data) {
                    vm.registro = data;
                    vm.titulo = 'Editando Cliente';
                });
        }

        function salvar() {
            if (!vm.registro.id) {
                ClienteService.insert(vm.registro)
                    .then(function (dado) {
                        DialogBuilder.message('Registro inserido com sucesso!');
                        $state.go("clientesList");
                    })
                    .catch(function (error) {
                        vm.error = error.data;
                    });
            } else {
                ClienteService.update(vm.registro)
                    .then(function (dado) {
                        DialogBuilder.message('Registro alterado com sucesso!');
                        $state.go("clientesList");
                    })
                    .catch(function (error) {
                        vm.error = error.data;
                    });
            }
        }
    }
})();