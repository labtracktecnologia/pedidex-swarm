(function() {
    'use strict'

    angular.module('app')
        .controller('PedidoFormController', PedidoFormController);

    PedidoFormController.$inject = ['PedidoService', '$state', '$stateParams','DialogBuilder', 'ClienteService'];

    function PedidoFormController(PedidoService, $state, $stateParams, DialogBuilder, ClienteService) {

        var vm = this;
        vm.registro = {
          emissao: new Date(),
          valorTotal: 0
        };
        vm.error = {};
        vm.clientes = [];

        vm.salvar = salvar;

        ClienteService.findAllOver()
          .then(function(data) {
            vm.clientes = data;
          })

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
                $state.go('pedidosList');
              })
              .catch(function (error) {
                vm.error = error.data;
              });
          } else {
            PedidoService.update(vm.registro)
              .then(function(dado){
                DialogBuilder.message('Registro alterado com sucesso!');
                $state.go('pedidosList');
              })
              .catch(function (error) {
                vm.error = error.data;
              });
          }
        }
    }
})();