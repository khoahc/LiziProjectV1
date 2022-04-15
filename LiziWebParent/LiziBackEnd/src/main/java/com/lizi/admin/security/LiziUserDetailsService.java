package com.lizi.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.lizi.admin.user.UserRepository;
import com.lizi.common.entity.User;

public class LiziUserDetailsService implements UserDetailsService {

	@Autowired UserRepository userRepo;	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {	
		User user = userRepo.getUserByEmail(email);
		if(user != null) {
			return new LiziUserDetails(user);
		}
		throw new UsernameNotFoundException("Không tìm thấy người dùng có email " + email);
	}

}
