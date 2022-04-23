package com.lizi.admin.category.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import com.lizi.admin.category.CategoryService;
import com.lizi.admin.user.UserNotFoundException;
import com.lizi.common.entity.Category;
import com.lizi.common.entity.Role;
import com.lizi.common.entity.User;

@Controller
public class CategoryController {
	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listFirstPage(Model model,
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		List<Category> listCategories = service.listAll();
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);		
		model.addAttribute("keyword", keyword);		
		
		return "categories/categories";

	}
			
//	@GetMapping("/categories/page/{pageNum}")
//	public String listByPage(			
//			@PathVariable(name = "pageNum") int pageNum, Model model,
//			@Param("sortField") String sortField, 
//			@Param("sortDir") String sortDir,
//			@Param("keyword") String keyword) {
//		if(pageNum < 1) 
//			pageNum = 1;
//		
//		System.out.println("sortField: " + sortField);
//		System.out.println("sortDir: " + sortDir);
//		
//		Page<Category> page = service.listByPage(pageNum, sortField, sortDir, keyword);
//		List<Category> listCategories = page.getContent();
//				
//		long totalItems = page.getTotalElements();
//		long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
//		long endCount = startCount + UserService.USERS_PER_PAGE - 1;
//		
//		if(endCount > totalItems) {
//			endCount = totalItems;
//		}
//		
//		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc"; 
//				
//		model.addAttribute("currentPage", pageNum);
//		model.addAttribute("totalPages", page.getTotalPages());
//		model.addAttribute("startCount", startCount);
//		model.addAttribute("endCount", endCount);
//		model.addAttribute("totalItems", totalItems);
//		model.addAttribute("listCategories", listCategories);
//		model.addAttribute("sortField", sortField);
//		model.addAttribute("sortDir", sortDir);		
//		model.addAttribute("reverseSortDir", reverseSortDir);
//		model.addAttribute("keyword", keyword);		
//		
//		return "categories/categories";
//	}
//	
	
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

//	@GetMapping("/categories/export/excel")
//	public void exportExcel(HttpServletResponse response) throws IOException {
//		List<Category> listCategories = service.listAll();
//		
//		CategoryExcelExporter exporter = new CategoryExcelExporter();
//		exporter.export(listCategories, response);
//	}
//	
	
}
