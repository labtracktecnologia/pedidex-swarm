(function() {
    'use strict'

    angular.module('app')
        .controller('ProdutoFormController', ProdutoFormController);

    ProdutoFormController.$inject = ['ProdutoService', '$state', '$stateParams','DialogBuilder'];

    function ProdutoFormController(ProdutoService, $state, $stateParams, DialogBuilder) {

        var vm = this;
        vm.registro = {};
        vm.error = {};

        vm.salvar = salvar;

        if ($stateParams.id) {
            ProdutoService.findById($stateParams.id)
              .then(function (data) {
                vm.registro = data;
              });
        }

        function salvar() {
            ProdutoService.insert(vm.registro)
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