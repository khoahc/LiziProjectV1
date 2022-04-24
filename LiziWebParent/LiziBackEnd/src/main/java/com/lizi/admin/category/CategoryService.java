package com.lizi.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.lizi.common.entity.Category;

@Service
@Transactional
public class CategoryService {
	public static final int USERS_PER_PAGE = 4;
	@Autowired
	private CategoryRepository categoryRepo;				
	
	public List<Category> listAll(){			
		List<Category> listRootCategories = categoryRepo.findRootCategories();
		return listHierarchicalCategories(listRootCategories);
	}

	public List<Category> listHierarchicalCategories(List<Category> rootCategories){
		List<Category> hierarchicalCategories = new ArrayList<>();
		
		for(Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));
			
			Set<Category> children = rootCategory.getChildren();
			
			for(Category subCategory : children) {
				String nameCategory = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, nameCategory));		
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1);
			}
		}
		return hierarchicalCategories;
	}	
	
	public void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
			Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();
		
		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name += "--";
			}	
			
			name += subCategory.getName();
			
			hierarchicalCategories.add(Category.copyFull(subCategory, name));
			
			listSubCategoriesUsedInForm(hierarchicalCategories, subCategory, newSubLevel);			
		}	
	}
	
	public List<Category> listCategoriesUsedInForm() {
		List<Category> categoriesInForm = new ArrayList<>();
		
		Iterable<Category> categoriesInDB = categoryRepo.findAll();
		
		for(Category category : categoriesInDB) {
			if(category.getParent() == null) {				
				categoriesInForm.add(Category.copyIdAndName(category));
						
				Set<Category> children = category.getChildren();
				
				for(Category subCategory : children) {
					String nameCategory = "--" + subCategory.getName();
					categoriesInForm.add(Category.copyIdAndName(subCategory.getId(), nameCategory));		
					listSubCategoriesUsedInForm(categoriesInForm, subCategory, 1);
				}
				
			}				
		}		
				
		return categoriesInForm;
	}
	
	private void listSubCategoriesUsedInForm(List<Category> categoriesInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();
		
		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name += "--";
			}	
			
			name += subCategory.getName();
			
			categoriesInForm.add(Category.copyIdAndName(subCategory.getId(), name));
			
			listSubCategoriesUsedInForm(categoriesInForm, subCategory, newSubLevel);			
		}				
	}
	
	public Category save(Category category) {
		return categoryRepo.save(category);
	}
	
	public Category getCategoryByName(String name) {			
		return categoryRepo.getCategoryByName(name);
	}
	
//	public Page<Category> listByPage(int pageNumber, String sortField, String sortDir, String keyword) {
//		Sort sort = Sort.by(sortField);
//		
//		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
//		
//		Pageable pageable = PageRequest.of(pageNumber - 1, USERS_PER_PAGE, sort);
//		
//		if(keyword != null) {
//			return categoryRepo.findAll(keyword, pageable);
//		}
//		
//		return categoryRepo.findAll(pageable);
//	}	
		
	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return categoryRepo.findById(id).get();			
		} catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("Không tìm loại sản phẩm có ID: " + id);
		}
		
	}
	
	public void delete(Integer id) throws CategoryNotFoundException {
		Long countById = categoryRepo.countById(id);
		
		if(countById == null || countById == 0) {
			throw new CategoryNotFoundException("Không tìm loại sản phẩm có ID: " + id);
		}
		
		categoryRepo.deleteById(id);
	}
	
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		categoryRepo.updateEnabledStatus(id, enabled);
	}
	
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		
		Category categoryByName = categoryRepo.findByName(name);
		
		if(isCreatingNew) {
			if(categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = categoryRepo.findByAlias(alias);
				if(categoryByAlias != null) {
					return "DuplicateAlias";
				}
			}
		} else {
			if(categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}
			Category categoryByAlias = categoryRepo.findByAlias(alias);
			if(categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}
		}
		
		return "OK";
	}
}
