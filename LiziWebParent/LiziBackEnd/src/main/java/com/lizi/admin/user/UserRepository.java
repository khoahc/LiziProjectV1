package com.lizi.admin.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.lizi.common.entity.User;

public interface UserRepository extends CrudRepository<User, Integer>{
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserByEmail(@Param("email") String email);	
	
	public Long countById(Integer id);
	
	@Modifying
	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")	//id: 1, enabled: 2
	public void updateEnabledStatus(Integer id, boolean enabled);
}
