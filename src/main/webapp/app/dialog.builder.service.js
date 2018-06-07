(function () {
    angular.module('app')
        .service('DialogBuilder', DialogBuilder);

    function DialogBuilder() {
        return {
            confirm: swal.mixin({
                type: 'question',
                showCancelButton: true,
                confirmButtonText: 'Sim, claro!',
                cancelButtonText: 'Não, deixa',
                confirmButtonClass: 'btn btn-success mx-2',
                cancelButtonClass: 'btn btn-danger mx-2',
                buttonsStyling: false
            }),
            message: swal.mixin({
                position: 'top-end',
                type: 'success',
                showConfirmButton: false,
                timer: 1500
            })
        }

    }
})();