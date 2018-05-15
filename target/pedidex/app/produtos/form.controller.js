(function() {
    'use strict'

    angular.module('app')
        .controller('ProdutoFormController', ProdutoFormController);

    ProdutoFormController.$inject = ['ProdutoService'];

    function ProdutoFormController(ProdutoService) {

        var vm = this;
        vm.registro = {}

        vm.salvar = salvar;

        function salvar() {
            ProdutoService.insert(vm.registro)
              .then(function(dado){
                alert('Produto ' + dado.codigo + ' inserido com sucesso!!!')
                vm.registro = {}
              });
        }
    }
})();