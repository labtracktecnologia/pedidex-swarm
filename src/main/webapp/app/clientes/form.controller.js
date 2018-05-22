(function() {
    'use strict'

    angular.module('app')
        .controller('ClienteFormController', ClienteFormController);

    ClienteFormController.$inject = ['ClienteService', '$state', '$stateParams','DialogBuilder'];

    function ClienteFormController(ClienteService, $state, $stateParams, DialogBuilder) {

        var vm = this;
        vm.registro = {};
        vm.error = {};

        vm.salvar = salvar;

        if ($stateParams.id) {
            ClienteService.findById($stateParams.id)
              .then(function (data) {
                vm.registro = data;
              });
        }

        function salvar() {
            ClienteService.insert(vm.registro)
              .then(function(dado){
                DialogBuilder.message('Registro excluído com sucesso!');
                $state.go(-1);
              })
              .catch(function (error) {
                vm.error = error.data;
              });
        }
    }
})();