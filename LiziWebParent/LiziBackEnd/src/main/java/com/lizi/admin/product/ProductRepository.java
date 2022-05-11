package com.lizi.admin.product;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.lizi.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
	public Product findByName(String name);

	@Modifying
	@Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")	
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	public Long countById(Integer id);
}
