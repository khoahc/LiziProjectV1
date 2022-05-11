package com.lizi.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lizi.common.entity.Setting;
import com.lizi.common.entity.SettingCategory;

@Service
public class SettingService {
	@Autowired private SettingRepository repo;
	
	public List<Setting> getGeneralSettings() {
		return repo.findByTowCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}
	
}
