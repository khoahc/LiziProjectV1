package com.lizi.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.lizi.admin.user.UserNotFoundException;
import com.lizi.admin.user.UserService;
import com.lizi.admin.user.export.UserExcelExporter;
import com.lizi.common.entity.Role;
import com.lizi.common.entity.User;


@Controller
public class UserController {
	
	@Autowired
	private UserService service;
	
	@GetMapping("/users")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "firstName", "asc", null);
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(			
			@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, 
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		if(pageNum < 1) 
			pageNum = 1;
		
		System.out.println("sortField: " + sortField);
		System.out.println("sortDir: " + sortDir);
		
		Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<User> listUsers = page.getContent();
				
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
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);		
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);		
		
		return "users/users";
	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRoles = service.listRoles();
		
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Tạo người dùng");
		return "users/user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		System.out.println("User: " + user);
		System.out.println(multipartFile.getOriginalFilename());
		
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			
			User savedUser = service.save(user);
			
			String uploadDir = "user-photos/" + savedUser.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if(user.getPhotos().isEmpty()) {
				user.setPhotos(null);
			}
			service.save(user);
		}
		
		redirectAttributes.addFlashAttribute("message", "Người dùng được lưu thành công.");
		
		return getRedirectURLToAffectedUser(user);
	}

	private String getRedirectURLToAffectedUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0];			
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes, Model model) {
		try {
			User user = service.get(id);
			List<Role> listRoles = service.listRoles();
			model.addAttribute("user", user);
			model.addAttribute("listRoles", listRoles);
			model.addAttribute("pageTitle", "Sửa người dùng (ID: " + id + ")");
			return "users/user_form"; 
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
		}							
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes, Model model) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "Người dùng ID " + id + " đã được xóa thành công.");			
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/users";
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnableStatus(@PathVariable("id") Integer id, 
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateUserEnabledStatus(id, enabled);
		String status = enabled ? " đã được kích hoạt" : " đã bị vô hiệu hóa";
		String message = "Tài khoản người dùng ID " + id + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/users";
	}

	@GetMapping("/users/export/excel")
	public void exportExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		
		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(listUsers, response);
	}
	
}