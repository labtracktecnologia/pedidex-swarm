package com.jhonystein.pedidex.rest.resources;

import com.jhonystein.pedidex.model.Produto;
import com.jhonystein.pedidex.rest.AbstractCrudResource;
import com.jhonystein.pedidex.services.AbstractCrudService;
import com.jhonystein.pedidex.services.ProdutoService;
import javax.inject.Inject;
import javax.ws.rs.Path;

@Path("produtos")
public class ProdutoResource extends AbstractCrudResource<Produto> {

    @Inject
    private ProdutoService service;
    
    @Override
    protected AbstractCrudService<Produto> getService() {
        return service;
    }
    
}
