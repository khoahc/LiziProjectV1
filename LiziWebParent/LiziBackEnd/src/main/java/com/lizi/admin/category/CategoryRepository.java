package com.lizi.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.lizi.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
	@Query("SELECT c FROM Category c WHERE c.name = :name")
	public Category getCategoryByName(@Param("name") String name);	
	
	public Long countById(Integer id);
	
	@Query("SELECT c FROM Category c WHERE CONCAT(c.id, ' ', c.name) LIKE %?1%")
	public Page<Category> findAll(String keyword, Pageable pageable);
	
	@Modifying
	@Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")	//id: 1, enabled: 2
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> findRootCategories();
}
