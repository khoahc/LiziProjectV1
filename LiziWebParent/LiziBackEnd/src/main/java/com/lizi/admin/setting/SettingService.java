package com.lizi.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lizi.common.entity.Setting;
import com.lizi.common.entity.SettingCategory;

@Service
public class SettingService {
	@Autowired private SettingRepository repo;
	
	public List<Setting> listAllSettings() {
		return (List<Setting>) repo.findAll();
	}
	
	public GeneralSettingBag getGeneralSettings() {
		List<Setting> settings = new ArrayList<>();
		
		List<Setting> generalSetting = repo.findByCategory(SettingCategory.GENERAL);
		List<Setting> currencySetting = repo.findByCategory(SettingCategory.CURRENCY);
		
		settings.addAll(generalSetting);
		settings.addAll(currencySetting);
		
		return new GeneralSettingBag(settings);
	}
	
	public void saveAll(Iterable<Setting> settings) {
		repo.saveAll(settings);
	}
}
