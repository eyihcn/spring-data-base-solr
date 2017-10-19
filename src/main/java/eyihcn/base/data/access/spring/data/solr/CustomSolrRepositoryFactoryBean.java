package eyihcn.base.data.access.spring.data.solr;

import java.io.Serializable;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;
import org.springframework.data.solr.repository.support.SolrRepositoryFactoryBean;

import eyihcn.base.entity.BaseEntity;
import eyihcn.base.spring.data.solr.repository.BaseSolrRepositoryImpl;

public class CustomSolrRepositoryFactoryBean<T extends Repository<S, ID>, S extends BaseEntity<ID>, ID extends Serializable> extends SolrRepositoryFactoryBean<T, S, ID> {


	public CustomSolrRepositoryFactoryBean(Class<T> repositoryInterface) {
		super(repositoryInterface);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected RepositoryFactorySupport doCreateRepositoryFactory() {
		return new CustomSolrRepositoryFactory(getSolrOperations());
	}

	private static class CustomSolrRepositoryFactory<T extends BaseEntity<ID>, ID extends Serializable> extends SolrRepositoryFactory {

		private final SolrOperations solrOperations;

		public CustomSolrRepositoryFactory(SolrOperations solrOperations) {
			super(solrOperations);
			this.solrOperations = solrOperations;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Object getTargetRepository(RepositoryInformation metadata) {
			return new BaseSolrRepositoryImpl<T, ID>(solrOperations, (Class<T>) metadata.getDomainType());
			// return super.getTargetRepository(metadata);
		}


		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return BaseSolrRepositoryImpl.class;
		}
	}
}
