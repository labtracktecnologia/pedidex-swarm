package com.jhonystein.pedidex.utils;

import com.jhonystein.pedidex.model.Entidade;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Classe utilitária para facilitação das chamadas mais simples à JPA utilizando CriteriaBuilder.
 * @author mauricio.guzinski e pietro.biasuz
 * @param <T> entidade utilizada na consulta
 */
public class JpaCriteriaHelper<T extends Entidade> {

    public enum ComparatorOperator { EQUAL, NOT_EQUAL, LIKE, LIKE_IGNORE_CASE, BETWEEN, GREATER_THAN, LESS_THAN, IN };
    public enum LogicalOperator { AND, OR };
    public enum OrderDirection { ASC, DESC };

    private static final Integer DEFAULT_PAGE_SIZE = 50;

    private final EntityManager em;

    private final CriteriaBuilder criteriaBuilder;

    private final List<WhereEntry> wheres = new ArrayList<>();

    private final List<OrderEntry> orders = new ArrayList<>();

    private Integer pageSize = DEFAULT_PAGE_SIZE;

    private Integer pageNumber;

    private final Class<T> entityClass;

    private final List<String> directFetches;

    private final List<ListFetch<?>> listFetches;
    
    private final Map<Path<?>, From<?, ?>> joinsMap = new HashMap<>();

    private class ListFetch<E> {
        private final String attribute;
        private final Class<E> clazz;

        public ListFetch(String attribute, Class<E> clazz) {
            this.attribute = attribute;
            this.clazz = clazz;
        }
    }

    /**
     * Define nome entradas da cláusula WHERE
     *
     */
    private class WhereEntry {

        private final List<String> fieldNames;

        private final ComparatorOperator comparatorOperator;

        private final Object valueIni;

        private final Object valueEnd;

        private final LogicalOperator logicalOperator;

        public WhereEntry(List<String> fieldNames
                ,ComparatorOperator comparatorOperator
                ,Object valueIni
                ,Object valueEnd
                ,LogicalOperator logicalOperator) {
            this.fieldNames          = fieldNames;
            this.comparatorOperator = comparatorOperator;
            this.valueIni           = valueIni;
            this.valueEnd           = valueEnd;
            this.logicalOperator    = logicalOperator;
        }
    }

    /**
     * Define nome do campo a ser ordenado, assim como a direção em que deve ser ordenado
     *
     */
    private class OrderEntry {

        private final List<String> fieldNames;

        private OrderDirection order;

        public OrderEntry(List<String> fieldNames, OrderDirection order) {
            this.fieldNames = fieldNames;
            this.order = order;
        }
    }

    private JpaCriteriaHelper( EntityManager em, Class<T> entityClass ) {
        this.em               = em;
        this.entityClass      = entityClass;
        this.criteriaBuilder  = em.getCriteriaBuilder();
        this.directFetches = new ArrayList<>();
        this.listFetches = new ArrayList<>();
    }

    /**
     * Cria o objeto de consulta para executar a query
     * @param <X> entidade utilizada na consulta
     * @param em EntityManager
     * @param entityClazz Classe de destino
     * @return objeto de consulta
     */
    public static <X extends Entidade> JpaCriteriaHelper<X> select( EntityManager em, Class<X> entityClazz ) {
        return new JpaCriteriaHelper<>( em, entityClazz );
    }

    /**
     * Inclui uma clausula WHERE com operador {@link ComparatorOperator.EQUAL} implicito
     * @param fieldName fieldName Nome da propriedade
     * @param value Valor
     * @return objeto de consulta
     */
    public JpaCriteriaHelper<T> where( String fieldName, Object value ) {
        return where(fieldName, ComparatorOperator.EQUAL, value);
    }

    /**
     * Inclui uma clausula WHERE com operador {@link ComparatorOperator.EQUAL} implicito
     * @param fieldNames fieldName Nome da propriedade
     * @param value Valor
     * @return objeto de consulta
     */
    public JpaCriteriaHelper<T> where( List<String> fieldNames, Object value ) {
        return where(fieldNames, ComparatorOperator.EQUAL, value);
    }



    /**
     * Inclui uma clausula WHERE
     * @param fieldName Nome da propriedade
     * @param comparator Comparador <b>(Para {@link ComparatorOperator.GREATER_THAN} e {@link ComparatorOperator.GREATER_THAN}
     * é necessário que valor complemente {@link Comparable})</b>
     * @param value Valor
     * @return objeto de consulta
     */
    public JpaCriteriaHelper<T> where( String fieldName, ComparatorOperator comparator, Object value ) {
        addTowhere(Arrays.asList(fieldName), comparator, value, null, null);
        return this;
    }

    public JpaCriteriaHelper<T> where( List<String> fieldNames, ComparatorOperator comparator, Object value ) {
        addTowhere(fieldNames, comparator, value, null, null);
        return this;
    }

    /**
     * Inclui uma clausula WHERE de BETWEEN
     * @param fieldName Nome da propriedade
     * @param comparator Comparador BETWEEN (apenas este é aceito para este método)
     * @param valueIni Valor inicial <b>(necessita implementar {@link Comparable})</b>
     * @param valueEnd Valor final <b>(necessita implementar {@link Comparable})</b>
     * @return objeto de consulta
     */
    @SuppressWarnings({ "rawtypes" }) // TODO: tentar resolver este warning
    public JpaCriteriaHelper<T> where( String fieldName, ComparatorOperator comparator, Comparable valueIni, Comparable valueEnd ) {
        addTowhere(Arrays.asList(fieldName), comparator, valueIni, valueEnd, null);
        return this;
    }

    /**
     * Inclui uma clausula WHERE com um operador AND
     * @param fieldName Nome da propriedade
     * @param value Valor
     * @return objeto de consulta
     */
    public JpaCriteriaHelper<T> and( String fieldName, Object value ) {
        addTowhere(Arrays.asList(fieldName), ComparatorOperator.EQUAL, value, null, LogicalOperator.AND);
        return this;
    }
    
    public JpaCriteriaHelper<T> and( List<String> fieldNames, Object value ) {
        addTowhere(fieldNames, ComparatorOperator.EQUAL, value, null, LogicalOperator.AND);
        return this;
    }

    /**
     * Inclui uma clausula WHERE de BETWEEN após um operador AND
     * @param fieldName Nome da propriedade
     * @param comparator Comparador BETWEEN (apenas este é aceito para este método)
     * @param valueIni Valor inicial <b>(necessita implementar {@link Comparable})</b>
     * @param valueEnd Valor final <b>(necessita implementar {@link Comparable})</b>
     * @return objeto de consulta
     */
    @SuppressWarnings({ "rawtypes" }) // TODO: tentar resolver este warning
    public JpaCriteriaHelper<T> and( String fieldName, ComparatorOperator comparator, Comparable valueIni, Comparable valueEnd ) {
        wheres.add( new WhereEntry(Arrays.asList(fieldName), comparator, valueIni, valueEnd, LogicalOperator.AND) );
        return this;
    }

    /**
     * Inclui uma clausulaWHERE após um operador AND
     * @param fieldName Nome da propriedade
     * @param comparator Comparador <b>(Para {@link ComparatorOperator.GREATER_THAN} e {@link ComparatorOperator.GREATER_THAN}
     * é necessário que valor complemente {@link Comparable})</b>
     * @param value Valor
     * @return objeto de consulta
     */
    public JpaCriteriaHelper<T> and( String fieldName, ComparatorOperator comparator, Object value ) {
        wheres.add( new WhereEntry(Arrays.asList(fieldName), comparator, value, null, LogicalOperator.AND) );
        return this;
    }

    /**
     * Inclui uma clausula WHERE com um operador AND
     * @param fieldName Nome da propriedade
     * @param value Valor
     * @return objeto de consulta
     */
    public JpaCriteriaHelper<T> or( String fieldName, Object value ) {
        addTowhere(Arrays.asList(fieldName), ComparatorOperator.EQUAL, value, null, LogicalOperator.OR);
        return this;
    }

    /**
     * Inclui uma clausula WHERE de BETWEEN após um operador OR
     * @param fieldName Nome da propriedade
     * @param comparator Comparador BETWEEN (apenas este é aceito para este método)
     * @param valueIni Valor inicial <b>(necessita implementar {@link Comparable})</b>
     * @param valueEnd Valor final <b>(necessita implementar {@link Comparable})</b>
     * @return objeto de consulta
     */
    @SuppressWarnings({ "rawtypes" }) // TODO: tentar resolver este warning
    public JpaCriteriaHelper<T> or( String fieldName, ComparatorOperator comparator, Comparable valueIni, Comparable valueEnd ) {
        wheres.add( new WhereEntry(Arrays.asList(fieldName), comparator, valueIni, valueEnd, LogicalOperator.OR) );
        return this;
    }

    /**
     * Inclui uma clausula WHERE após um operador OR
     * @param fieldName Nome da propriedade
     * @param comparator Comparador <b>(Para {@link ComparatorOperator.GREATER_THAN} e {@link ComparatorOperator.GREATER_THAN}
     * é necessário que valor complemente {@link Comparable})</b>
     * @param value Valor
     * @return objeto de consulta
     */
    public JpaCriteriaHelper<T> or( String fieldName, ComparatorOperator comparator, Object value ) {
        wheres.add( new WhereEntry(Arrays.asList(fieldName), comparator, value, null, LogicalOperator.OR) );
        return this;
    }

    public JpaCriteriaHelper<T> or( List<String> fieldNames, ComparatorOperator comparator, Object value ) {
        wheres.add( new WhereEntry(fieldNames, comparator, value, null, LogicalOperator.OR) );
        return this;
    }

    public JpaCriteriaHelper<T> or( List<String> fieldNames, Object value ) {
        wheres.add( new WhereEntry(fieldNames, ComparatorOperator.EQUAL, value, null, LogicalOperator.OR) );
        return this;
    }

    /**
     * Inclui clausula ORDER BY
     * @param fieldNames Nome da propriedade
     * @return objeto de consulta
     */
    public JpaCriteriaHelper<T> orderBy( String ... fieldNames ) {
        orders.add( new OrderEntry(Arrays.asList(fieldNames), OrderDirection.ASC) );
        return this;
    }

    /**
     * Define a ultima clausula ORDER BY como ascedente
     * @return objeto de consulta
     */
    public JpaCriteriaHelper<T> asc() {
        if ( ! orders.isEmpty() ) {
            throw new RuntimeException("Nenhum cláusula ORDER BY definida");
        }
        orders.get( orders.size() - 1 ).order = OrderDirection.ASC;
        return this;
    }

    /**
     * Define a ultima clausula ORDER BY como descedente
     * @return objeto de consulta
     */
    public JpaCriteriaHelper<T> desc() {
        if ( orders.isEmpty() ) {
            throw new RuntimeException("Nenhum cláusula ORDER BY definida");
        }
        orders.get( orders.size() - 1 ).order = OrderDirection.DESC;
        return this;
    }

    /**
     * Obtem lista com os resultados
     * @return Lista de resultados
     */
    public List<T> getResults() {
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root                   = criteriaQuery.from(entityClass);
        setupQuery(criteriaQuery, root);

        // ORDER BY
        if ( ! orders.isEmpty() ) {
            ArrayList<Order> jpaOrders = new ArrayList<>();
            orders.forEach((orderField) -> {
                if ( orderField.order.equals(OrderDirection.ASC) ) {
                    jpaOrders.add( criteriaBuilder.asc(getPath(orderField.fieldNames, root)));
                } else {
                    jpaOrders.add( criteriaBuilder.desc(getPath(orderField.fieldNames, root)));
                }
            });
            criteriaQuery.orderBy( jpaOrders );
        }

        if ( pageNumber != null ) {
            return em.createQuery(criteriaQuery).setFirstResult( (pageNumber - 1) * pageSize ).setMaxResults(pageSize)
                            .getResultList();
        } else {
            return em.createQuery(criteriaQuery).getResultList();
        }
    }

    public void delete() {
        CriteriaDelete<T> criteriaDelete = criteriaBuilder.createCriteriaDelete(entityClass);

        Root<T> root = criteriaDelete.from(entityClass);

        if (!wheres.isEmpty()) {
            criteriaDelete.where( getPredicates(root, wheres) );
        }

        em.createQuery(criteriaDelete).executeUpdate();
    }

    private void setupQuery(CriteriaQuery<T> criteriaQuery, Root<T> root) {
        // SELECT
        criteriaQuery.select(root);

        //FETCH JOINS
        directFetch(root);

        listFetch(root);

        // WHERE
        if ( ! wheres.isEmpty() ) {
            criteriaQuery.where( getPredicates(root, wheres) );
        }
    }

    private void listFetch(Root<T> root) {
        listFetches.stream().map((listFetch) -> root.getModel().getList(listFetch.attribute, listFetch.clazz)).forEachOrdered((listAttribute) -> {
            root.fetch(listAttribute);
        });
    }

    private void directFetch(Root<T> root) {
        directFetches.forEach((fetch) -> {
            root.fetch(fetch);
        });
    }

    /**
     * Define o tamanho das páginas, em caso de paginação (tamanho padrão: 50)
     * @param pageSize Número de registros por página
     * @return objeto de consulta
     */
    public JpaCriteriaHelper<T> setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Define a pagina que serah retornada
     * <b>Se a página for informada, ativa a paginação de resultados</b>
     * Por padrão, não será efetuada paginação dos resultados
     * @param pageNumber Número da página (informe <b>null</b> para desativar a página)
     * @return objeto de consulta
     */
    public JpaCriteriaHelper<T> page( Integer pageNumber ) {
        this.pageNumber = pageNumber;
        return this;
    }

    /**
     * Obtem apenas o primeiro registro do resultado da consulta
     * @return O primeiro objeto retornado da consulta ou <b>null</b> se a consulta não retornar resultados
     */
    public T getFirstResult() {
        List<T> resultList = this.setPageSize(1).page(1).getResults();
        if ( resultList.isEmpty() ) {
            return null;
        } else {
            return resultList.get(0);
        }
    }

    /**
     * Obtém apenas o primeiro registro do resultado da cpnsulta
     * @return O primeiro objeto retornado da consulta ou <b>null</b> se a consulta não retornar resultados
     */
    public Optional<T> getFirstResultOpt() {
        T firstResult = getFirstResult();

        if (firstResult == null) {
            return Optional.empty();
        } else {
            return Optional.of(firstResult);
        }
    }

    /**
     * Obtem unico registro do resultado da consulta
     * @return O primeiro objeto retornado da consulta
     */
    public T getSingleResult() {
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root                   = criteriaQuery.from(entityClass);
        setupQuery(criteriaQuery, root);

        return em.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Verifica se a consulta retorna algum resultado
     * @return <li><b>true</b>: existem registros
     *         <li><b>false</b>: não existem registros
     */
    public boolean exists() {
        return this.getFirstResult() != null;
    }

    /**
     * Efetua a contagem dos registros da consulta
     * @return numero de registros retornados pela consulta
     */
    public long count() {
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<T>                 rootCount = criteriaQuery.from(entityClass);

        criteriaQuery.select( criteriaBuilder.count( rootCount ) );

        if ( ! wheres.isEmpty() ) {
            criteriaQuery.where( getPredicates(rootCount, wheres) );
        }

        return em.createQuery( criteriaQuery ).getSingleResult();
    }

    private void addTowhere( List<String> fieldNames, ComparatorOperator comparator, Object valueIni, Object valueEnd, LogicalOperator logicalOperator ) {
        if ( ( comparator.equals(ComparatorOperator.GREATER_THAN) || comparator.equals(ComparatorOperator.LESS_THAN) )
                && ! (valueIni instanceof Comparable) ) {
            throw new RuntimeException("Para os tipos de operador "
                    + ComparatorOperator.GREATER_THAN + " e " + ComparatorOperator.LESS_THAN
                    + " é necessário que o objeto de valor implemente " + Comparable.class.getName() + ".");
        }

        if ( comparator.equals(ComparatorOperator.IN) && ! (valueIni instanceof Collection) ) {
            throw new RuntimeException("Para o tipo de operador " + ComparatorOperator.IN
                    + " é necessário que o objeto de valor implemente " + Collection.class.getName() + ".");
        }

        if ( valueEnd != null && ! comparator.equals( ComparatorOperator.BETWEEN ) ) {
            throw new RuntimeException("Quando informados dois valores, é obrigatório o uso de " + ComparatorOperator.BETWEEN);
        }

        if ( logicalOperator == null ) {
            logicalOperator = LogicalOperator.AND;
        }

        wheres.add( new WhereEntry(fieldNames, comparator, valueIni, valueEnd, logicalOperator) );
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) // TODO: tentar retirar estes warnings
    private Predicate[] getPredicates( Root<T> root, List<WhereEntry> wheres ) {
        List<Predicate> predicates = new ArrayList<>();
        Predicate predMaster = null;

        for (WhereEntry whereEntry : wheres) {
            Predicate predicate;

            // --- OPERADOR DE COMPARAÇÃO ---
            Path path = getPath(whereEntry.fieldNames, root);
            switch (whereEntry.comparatorOperator) {
                case EQUAL:
                    if ( whereEntry.valueIni == null ) {
                        predicate = criteriaBuilder.isNull(path);
                    } else {
                        predicate = criteriaBuilder.equal(path, whereEntry.valueIni);
                    }
                    break;
                case NOT_EQUAL:
                    if ( whereEntry.valueIni == null ) {
                        predicate = criteriaBuilder.isNotNull(path);
                    } else {
                        predicate = criteriaBuilder.notEqual(path, whereEntry.valueIni);
                    }
                    break;
                case GREATER_THAN:
                    predicate = criteriaBuilder.greaterThan(path, (Comparable) whereEntry.valueIni);
                    break;
                case LESS_THAN:
                    predicate = criteriaBuilder.lessThan(path, (Comparable) whereEntry.valueIni);
                    break;
                case LIKE:
                    predicate = criteriaBuilder.like(path, whereEntry.valueIni.toString());
                    break;
                case LIKE_IGNORE_CASE:
                    predicate = criteriaBuilder.like( criteriaBuilder.upper(path), whereEntry.valueIni.toString().toUpperCase() );
                    break;
                case IN:
                    predicate = path.in( (Collection) whereEntry.valueIni);
                    break;
                case BETWEEN:
                    predicate = criteriaBuilder.between(path, (Comparable) whereEntry.valueIni, (Comparable) whereEntry.valueEnd);
                    break;
                default:
                    throw new RuntimeException("Tipo de operador de comparação não conhecido: " + whereEntry.comparatorOperator);
            }

            if ( predMaster == null ) {
                predMaster = predicate;
            } else {
                // --- OPERADOR LÓGICO ---
                if ( whereEntry.logicalOperator != null ) {
                    switch ( whereEntry.logicalOperator ) {
                        case AND:
                            predMaster = criteriaBuilder.and( predMaster, predicate );
                            break;
                        case OR:
                            predMaster = criteriaBuilder.or( predMaster, predicate );
                            break;
                        default:
                            throw new RuntimeException("Tipo de operador lógico não conhecido: " + whereEntry.comparatorOperator);
                    }
                }
            }

        }
        predicates.add( predMaster );

        return predicates.toArray(new Predicate[] {});
    }

    // TODO: testar se estah fazendo JOIN corretamente para multiplos niveis
    private Path<?> getPath(List<String> fieldNames, Root<T> root) {
        javax.persistence.criteria.Path<?> entity = root;
        
        for (String fieldName : fieldNames) {
            Path<Object> fieldAsPath = entity.get(fieldName);
            if ( Collection.class.isAssignableFrom( fieldAsPath.getJavaType() ) ) {
                if ( ! joinsMap.containsKey(fieldAsPath) ) {
                    joinsMap.put(fieldAsPath, ((From<?, ?>) entity).join(fieldName));
                }
                entity = joinsMap.get(fieldAsPath);
            } else {
                entity = entity.get(fieldName);
            }
        }

        return entity;
    }

    public JpaCriteriaHelper<T> fetch(String property) {
        this.directFetches.add(property);

        return this;
    }

    public <E> JpaCriteriaHelper<T> fetch(String fetch, Class<E> clazz) {
        this.listFetches.add(new ListFetch<>(fetch, clazz));

        return this;
    }

    public static <T extends Entidade> JpaCriteriaHelper<T> create(EntityManager em, Class<T> entityClazz) {
        return new JpaCriteriaHelper<>( em, entityClazz );
    }

}
