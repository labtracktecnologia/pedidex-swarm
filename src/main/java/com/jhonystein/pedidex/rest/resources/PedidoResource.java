package com.jhonystein.pedidex.rest.resources;

import com.jhonystein.pedidex.model.Pedido;
import com.jhonystein.pedidex.rest.AbstractCrudResource;
import com.jhonystein.pedidex.services.AbstractCrudService;
import com.jhonystein.pedidex.services.PedidoService;
import javax.inject.Inject;
import javax.ws.rs.Path;

@Path("pedidos")
public class PedidoResource extends AbstractCrudResource<Pedido> {

    @Inject
    private PedidoService service;
    
    @Override
    protected AbstractCrudService<Pedido> getService() {
        return service;
    }
    
}
