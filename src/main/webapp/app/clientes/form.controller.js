(function() {
    'use strict'

    angular.module('app')
        .controller('ClienteFormController', ClienteFormController);

    ClienteFormController.$inject = ['ClienteService'];

    function ClienteFormController(ClienteService) {

        var vm = this;
        vm.registro = {}

        vm.salvar = salvar;

        function salvar() {
            ClienteService.insert(vm.registro)
              .then(function(dado){
                alert('Cliente ' + dado.nome + ' inserido com sucesso!!!')
                vm.registro = {}
              });
        }
    }
})();