(function() {
    'use strict'

    angular.module('app')
        .controller('PedidoFormController', PedidoFormController);

    PedidoFormController.$inject = ['PedidoService', '$state', '$stateParams','DialogBuilder'];

    function PedidoFormController(PedidoService, $state, $stateParams, DialogBuilder) {

        var vm = this;
        vm.registro = {};
        vm.error = {};

        vm.salvar = salvar;

        if ($stateParams.id) {
            PedidoService.findById($stateParams.id)
              .then(function (data) {
                vm.registro = data;
              });
        }

        function salvar() {
          if (!vm.registro.id) {
            PedidoService.insert(vm.registro)
              .then(function(dado){
                DialogBuilder.message('Registro incluído com sucesso!');
                $state.go(-1);
              })
              .catch(function (error) {
                vm.error = error.data;
              });
          } else {
            PedidoService.update(vm.registro)
              .then(function(dado){
                DialogBuilder.message('Registro alterado com sucesso!');
                $state.go(-1);
              })
              .catch(function (error) {
                vm.error = error.data;
              });
          }
        }
    }
})();