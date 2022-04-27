package com.lizi.admin.category.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lizi.admin.FileUploadUtil;
import com.lizi.admin.category.CategoryNotFoundException;
import com.lizi.admin.category.CategoryPageInfo;
import com.lizi.admin.category.CategoryService;
import com.lizi.common.entity.Category;

@Controller
public class CategoryController {
	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listFirstPage(String sortDir, Model model) {
		return listByPage(1, sortDir, null, model);
	}			
	
	@GetMapping("/categories/page/{pageNum}") 
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, 
			String sortDir,	String keyword,	Model model) {
		if (sortDir ==  null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> listCategories = service.listByPage(pageInfo, pageNum, sortDir, keyword);
		
		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
		if (endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);		
		
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("moduleURL", "/categories");
		
		return "categories/categories";		
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {	
		List<Category> listCategories = service.listCategoriesUsedInForm();
		Category category = new Category();
		category.setEnabled(true);
		model.addAttribute("category", category);
		model.addAttribute("listCategories", listCategories);		
		model.addAttribute("pageTitle", "Tạo loại sản phẩm");
		return "categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, 
			@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {	
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);
			
			Category savedCategory = service.save(category);
			
			String uploadDir = "../category-images/" + savedCategory.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			service.save(category);
		}		
		
		redirectAttributes.addFlashAttribute("message", "Loại sản phẩm được lưu thành công.");
		
		return "redirect:/categories";			
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes, Model model) {
		try {
			Category category = service.get(id);
			List<Category> listCategories = service.listCategoriesUsedInForm();
			
			model.addAttribute("category", category);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Sửa loại sản phẩm (ID: " + id + ")");
			return "categories/category_form"; 
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/categories";
		}							
	}
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes, Model model) throws CategoryNotFoundException {
		try {
			service.delete(id);
			String categoryDir = "../category-images/" + id;
			FileUploadUtil.removeDir(categoryDir);
			redirectAttributes.addFlashAttribute("message", "Loại sản phẩm Id " + id + " đã được xóa thành công.");			
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryEnableStatus(@PathVariable("id") Integer id, 
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateCategoryEnabledStatus(id, enabled);
		String status = enabled ? " đã được kích hoạt" : " đã bị vô hiệu hóa";
		String message = "Loại sản phẩm ID " + id + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/categories";
	}
	
}
