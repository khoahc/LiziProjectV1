package com.lizi.admin.category.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.lizi.admin.category.CategoryNotFoundException;
import com.lizi.admin.category.CategoryService;
import com.lizi.admin.user.UserService;
import com.lizi.common.entity.Category;

@Controller
public class CategoryController {
	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "name", "asc", null);
	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(			
			@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		if(pageNum < 1) 
			pageNum = 1;
		
		System.out.println("sortField: " + sortField);
		System.out.println("sortDir: " + sortDir);
		
		Page<Category> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<Category> listCategories = page.getContent();
				
		long totalItems = page.getTotalElements();
		long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;
		
		if(endCount > totalItems) {
			endCount = totalItems;
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc"; 
				
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);		
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);		
		
		return "categories/categories";
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
