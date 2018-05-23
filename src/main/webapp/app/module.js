(function(){

    angular.module('app', [
        'ui.router'
    ]);

    angular.module('app').config(AppConfig);

    AppConfig.$inject = ['$stateProvider'];

    function AppConfig($stateProvider) {
        $stateProvider
            .state({
                name: 'clientesList',
                url: '/clientes',
                templateUrl: '/views/clientes/list.html',
                controller: 'ClienteListController',
                controllerAs: 'vm'
            })
            .state({
                name: 'clientesNovo',
                url: '/clientes/novo',
                templateUrl: '/views/clientes/form.html',
                controller: 'ClienteFormController',
                controllerAs: 'vm'
            })
            .state({
                name: 'clientesEditar',
                url: '/clientes/{id}',
                templateUrl: '/views/clientes/form.html',
                controller: 'ClienteFormController',
                controllerAs: 'vm'
            })
            .state({
                name: 'produtosList',
                url: '/produtos',
                templateUrl: '/views/produtos/list.html',
                controller: 'ProdutoListController',
                controllerAs: 'vm'
            })
            .state({
                name: 'produtosNovo',
                url: '/produtos/novo',
                templateUrl: '/views/produtos/form.html',
                controller: 'ProdutoFormController',
                controllerAs: 'vm'
            })
            .state({
                name: 'produtosEditar',
                url: '/produtos/{id}',
                templateUrl: '/views/produtos/form.html',
                controller: 'ProdutoFormController',
                controllerAs: 'vm'
            })
            .state({
                name: 'pedidosList',
                url: '/pedidos',
                templateUrl: '/views/pedidos/list.html',
                controller: 'PedidoListController',
                controllerAs: 'vm'
            })
            .state({
                name: 'pedidosNovo',
                url: '/pedidos/novo',
                templateUrl: '/views/pedidos/form.html',
                controller: 'PedidoFormController',
                controllerAs: 'vm'
            })
            .state({
                name: 'pedidosEditar',
                url: '/pedidos/{id}',
                templateUrl: '/views/pedidos/form.html',
                controller: 'PedidoFormController',
                controllerAs: 'vm'
            });
    }
})();
