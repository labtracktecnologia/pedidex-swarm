package com.jhonystein.pedidex.services;

import com.jhonystein.pedidex.model.Pedido;
import com.jhonystein.pedidex.utils.GenericDao;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PedidoService extends AbstractCrudService<Pedido> {

    private GenericDao<Pedido> dao;
    
    @Override
    protected GenericDao<Pedido> getDao() {
        return dao;
    }
}
