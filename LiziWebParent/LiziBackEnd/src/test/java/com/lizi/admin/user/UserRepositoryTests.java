package com.lizi.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.data.domain.Pageable;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;

import com.lizi.common.entity.Role;
import com.lizi.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userKhoa = new User("khoa@gmail.com", "khoa", "Khoa", "Dang");
		userKhoa.addRole(roleAdmin);
		
		User saveUser = repo.save(userKhoa);
		
		assertThat(saveUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateNewUserWithTwoRoles() {
		User userHau = new User("hau@gmail.com", "hau", "Hau", "Van");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);
		
		userHau.addRole(roleEditor);
		userHau.addRole(roleAssistant);
		
		User saveUser = repo.save(userHau);
		
		assertThat(saveUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllUsers() {
//		 Implementing this interface allows an object to be the target of the enhanced
//		 {@code for} statement (sometimes called the "for-each loop" statement)
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void testGetUserById() {
		User userKhoa = repo.findById(7).get();
		System.out.println(userKhoa);
		assertThat(userKhoa).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User userKhoa = repo.findById(7).get();		
		userKhoa.setEnabled(true);
		
		repo.save(userKhoa);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User userHau = repo.findById(6).get();
		Role roleEditor = new Role(3);
		Role roleSalesPerson = new Role(2);
		
		userHau.getRoles().remove(roleEditor);
		userHau.addRole(roleSalesPerson);
		
		repo.save(userHau);
	}
	
	@Test
	public void testDeleteUser() {
		int idUserHau = 6;
		repo.deleteById(idUserHau);
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "khoa@gmail.com";
		User user = repo.getUserByEmail(email);
		
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Integer id = 7;
		Long countById = repo.countById(id);
		
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testDisableUser() {
		Integer id = 7;
		repo.updateEnabledStatus(id, false);		
	}
	
	@Test
	public void testListFirstPage() {
		int pageNumber = 1;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
	@Test
	public void testSearchUsers() {
		String keyword = "bruce";
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(keyword, pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isGreaterThan(0);
	}
}
