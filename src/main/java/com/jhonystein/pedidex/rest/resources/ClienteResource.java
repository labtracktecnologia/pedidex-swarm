package com.jhonystein.pedidex.rest.resources;

import com.jhonystein.pedidex.model.Cliente;
import com.jhonystein.pedidex.rest.AbstractCrudResource;
import com.jhonystein.pedidex.services.AbstractCrudService;
import com.jhonystein.pedidex.services.ClienteService;
import javax.inject.Inject;
import javax.ws.rs.Path;

@Path("clientes")
public class ClienteResource extends AbstractCrudResource<Cliente> {

    @Inject
    private ClienteService service;

    @Override
    protected AbstractCrudService<Cliente> getService() {
        return service;
    }

}
