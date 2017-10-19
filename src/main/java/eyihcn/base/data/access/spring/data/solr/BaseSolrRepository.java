package eyihcn.base.data.access.spring.data.solr;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.repository.SolrCrudRepository;

import eyihcn.base.entity.BaseEntity;

@NoRepositoryBean
public interface BaseSolrRepository <T extends BaseEntity<PK>, PK extends Serializable> extends SolrCrudRepository<T, PK> {

	<S extends Page<T>> S findList(Criteria criteria, Pageable pageable);

	T findOne(Criteria criteria, Pageable pageable);

	T findOne(Criteria criteria, Sort sort);

	T findOne(Criteria criteria);

	boolean exists(Criteria criteria, Pageable pageable);

	boolean exists(Criteria criteria, Sort sort);

	boolean exists(Criteria criteria);

	long count(Criteria criteria, Pageable pageable);

	long count(Criteria criteria, Sort sort);

	long count(Criteria criteria);

	boolean delete(Criteria criteria, Pageable pageable);

	boolean delete(Criteria criteria, Sort sort);

	boolean delete(Criteria criteria);

}
