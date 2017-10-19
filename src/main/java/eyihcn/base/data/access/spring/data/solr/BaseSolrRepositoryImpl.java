package eyihcn.base.data.access.spring.data.solr;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTransactionSynchronizationAdapterBuilder;
import org.springframework.data.solr.core.mapping.SimpleSolrMappingContext;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.repository.query.SolrEntityInformation;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;
import org.springframework.data.solr.repository.support.SolrEntityInformationCreatorImpl;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import eyihcn.base.entity.BaseEntity;

public class BaseSolrRepositoryImpl<T extends BaseEntity<PK>, PK extends Serializable> extends SimpleSolrRepository<T, PK> implements BaseSolrRepository<T, PK> {
	
	private final String solrCollectionName;
	
	public BaseSolrRepositoryImpl(SolrOperations solrOperations, SolrEntityInformation<T, ?> metadata) {
		super(solrOperations, metadata);
		this.solrCollectionName = metadata.getCollectionName();
	}
	
	public BaseSolrRepositoryImpl(SolrOperations solrOperations, Class<T> entityClass) {
		this(solrOperations, new SolrEntityInformationCreatorImpl(new SimpleSolrMappingContext()).getEntityInformation(entityClass));
	}

	@SuppressWarnings("unchecked")
	public <S extends Page<T>> S findList(Criteria criteria, @Nullable Pageable pageable) {

		Assert.notNull(criteria, "criteria can not be  'null'");

		return (S) getSolrOperations().query(solrCollectionName, new SimpleQuery(criteria, pageable), getEntityClass());
	}

	public T findOne(Criteria criteria, @Nullable Pageable pageable) {
		SimpleQuery query = new SimpleQuery(criteria);
		if (null != pageable) {
			query.setPageRequest(pageable);
		}
		Optional<T> queryForObject = getSolrOperations().queryForObject(solrCollectionName, query, getEntityClass());
		return queryForObject.isPresent() ? queryForObject.get() : null;
	}

	public T findOne(Criteria criteria, @Nullable Sort sort) {
		SimpleQuery query = new SimpleQuery(criteria);
		if (null != sort) {
			query.addSort(sort);
		}
		Optional<T> queryForObject = getSolrOperations().queryForObject(solrCollectionName, query, getEntityClass());
		return queryForObject.isPresent() ? queryForObject.get() : null;
	}

	public T findOne(Criteria criteria) {

		return findOne(criteria, (Sort) null);
	}

	public boolean exists(Criteria criteria, @Nullable Pageable pageable) {

		return this.findOne(criteria, pageable) != null;
	}

	public boolean exists(Criteria criteria, @Nullable Sort sort) {

		return this.findOne(criteria, sort) != null;
	}

	public boolean exists(Criteria criteria) {

		return exists(criteria, (Sort) null);
	}

	public long count(Criteria criteria, @Nullable Pageable pageable) {

		SimpleQuery query = new SimpleQuery(criteria);
		if (null != pageable) {
			query.setPageRequest(pageable);
		}
		return getSolrOperations().count(this.solrCollectionName, query);
	}

	public long count(Criteria criteria, @Nullable Sort sort) {

		SimpleQuery query = new SimpleQuery(criteria);
		if (null != sort) {
			query.addSort(sort);
		}
		return getSolrOperations().count(this.solrCollectionName, query);
	}

	public long count(Criteria criteria) {

		return count(criteria, (Sort) null);
	}

	public boolean delete(Criteria criteria, @Nullable Pageable pageable) {
		SimpleQuery query = new SimpleQuery(criteria);
		if (null != pageable) {
			query.setPageRequest(pageable);
		}
		registerTransactionSynchronisationIfSynchronisationActive();
		getSolrOperations().delete(this.solrCollectionName, query);
		commitIfTransactionSynchronisationIsInactive();
		return true;
	}

	public boolean delete(Criteria criteria, @Nullable Sort sort) {
		SimpleQuery query = new SimpleQuery(criteria);
		if (null != sort) {
			query.addSort(sort);
		}
		registerTransactionSynchronisationIfSynchronisationActive();
		getSolrOperations().delete(this.solrCollectionName, query);
		commitIfTransactionSynchronisationIsInactive();
		return true;
	}

	public boolean delete(Criteria criteria) {

		return delete(criteria, (Sort) null);
	}

	private void registerTransactionSynchronisationIfSynchronisationActive() {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			registerTransactionSynchronisationAdapter();
		}
	}

	private void registerTransactionSynchronisationAdapter() {
		TransactionSynchronizationManager
				.registerSynchronization(SolrTransactionSynchronizationAdapterBuilder.forOperations(getSolrOperations()).onCollection(solrCollectionName).withDefaultBehaviour());
	}

	private void commitIfTransactionSynchronisationIsInactive() {
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			getSolrOperations().commit(solrCollectionName);
		}
	}

}
