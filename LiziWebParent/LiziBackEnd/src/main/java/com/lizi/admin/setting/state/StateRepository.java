package com.lizi.admin.setting.state;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.lizi.common.entity.Country;
import com.lizi.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer> {
	
	public List<State> findByCountryOrderByNameAsc(Country country);
}
