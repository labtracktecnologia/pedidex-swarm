(function(){
    'use strict'

    angular.module('app')
      .service('ClienteService', ClienteService);

    ClienteService.$inject = ['$http'];

    function ClienteService($http) {

        function findAll(filtro, page) {
            return $http.get('http://localhost:8080/api/clientes?filterField=nome&filterValue=' + filtro)
              .then(function(response) {
                return {
                    registros: response.data,
                    total: response.headers['X-Total-Lenght'],
                    pages: ['1', '2'],
                    currentPage: '1'
                }
              });
        }

        function findById(id) {
            return $http.get('http://localhost:8080/api/clientes/' + id)
              .then(function (response) {
                  return response.data;
              });
        }

        function insert(registro) {
            return $http.post('http://localhost:8080/api/clientes', registro)
              .then(function (response) {
                  return response.data;
              });
        }

        function update(registro) {
            return $http.put('http://localhost:8080/api/clientes/' + registro.id, registro)
              .then(function (response) {
                  return response.data;
              });
        }

        function remove(id) {
            return $http.delete('http://localhost:8080/api/clientes/' + id)
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