(function(){
    'use strict'
  
    angular.module('app')
      .controller('ProdutoListController', ProdutoListController);
    
    ProdutoListController.$inject = ['ProdutoService', 'DialogBuilder']
    
    function ProdutoListController(ProdutoService, DialogBuilder) {
        var vm = this;
        vm.registros = [];
        vm.filtro = '';
  
        vm.atualizar = load;
        vm.resetFilter = function () {
            vm.filtro = '';
            load();
        }
    
        function load() {
            ProdutoService.findAll(vm.filtro)
              .then(function (dados) {
                  vm.registros = dados
              });
        }
  
        vm.excluir = function(item) {
            DialogBuilder.confirm('Tem certeza que deseja remover o registro?')
                .then(function (result) {
                    if (result.value) {
                        ProdutoService.remove(item.id)
                            .then(function () {
                                load();
                                DialogBuilder.message('Registro excluído com sucesso!');
                            });
                    } else {
                        DialogBuilder.message({
                            title: 'Exclusão cancelada pelo usuário!',
                            type: 'error'
                        });
                    }
                });
        };
        load();
    }
})();