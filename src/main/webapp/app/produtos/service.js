(function(){
    'use strict'

    angular.module('app')
      .service('ProdutoService', ProdutoService);

    ProdutoService.$inject = ['$http'];

    function ProdutoService($http) {

        function findAll(filtro) {
            return $http.get('http://localhost:8080/api/produtos?filterField=descricao&filterValue=' + filtro)
              .then(function(response) {
                return response.data;
              });
        }

        function findById(id) {
            return $http.get('http://localhost:8080/api/produtos/' + id)
              .then(function (response) {
                  return response.data;
              });
        }

        function insert(registro) {
            return $http.post('http://localhost:8080/api/produtos', registro)
              .then(function (response) {
                  return response.data;
              });
        }

        function update(registro) {
            return $http.put('http://localhost:8080/api/produtos/' + registro.id, registro)
              .then(function (response) {
                  return response.data;
              });
        }

        function remove(id) {
            return $http.delete('http://localhost:8080/api/produtos/' + id)
              .then(function (response) {
                  return response.data;
              });
        }

        return {
            findAll: findAll,
            findById: findById,
            insert: insert,
            update: update,
            remove: remove
        }
    }

})();