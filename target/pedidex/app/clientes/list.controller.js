(function(){
    'use strict'
  
    angular.module('app')
      .controller('ClienteListController', ClienteListController);
    
    ClienteListController.$inject = ['ClienteService', 'DialogBuilder']
    
    function ClienteListController(ClienteService, DialogBuilder) {
        var vm = this;
        vm.data = {};
        vm.filtro = '';
  
        vm.atualizar = load;
        vm.resetFiltro = function() {
            vm.filtro = '';
            load();
        }

        function load() {
            ClienteService.findAll(vm.filtro)
              .then(function (dados) {
                  vm.data = dados
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