package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.form.CategoryForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.service.CategoryService;

@Controller
public class AdminCategoryController {

	private final CategoryRepository categoryRepository;
	private final CategoryService categoryService;

	public AdminCategoryController(CategoryRepository categoryRepository, CategoryService categoryService) {
		this.categoryRepository = categoryRepository;
		this.categoryService = categoryService;
	}

	// カテゴリ一覧表示
	@GetMapping("/admin/categories")
	public String index(Model model) {
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);
		return "admin/categories/index"; // カテゴリ一覧ページ
	}

	// カテゴリ新規登録フォーム表示
	@GetMapping("/admin/categories/register")
	public String register(Model model) {
		model.addAttribute("categoryForm", new CategoryForm());
		return "admin/categories/register";
	}

	// カテゴリ作成
	@PostMapping("/admin/categories/create")
	public String create(@ModelAttribute @Validated CategoryForm categoryForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "admin/categories/register";
		}

		try {
			categoryService.create(categoryForm);
			redirectAttributes.addFlashAttribute("successMessage", "カテゴリを登録しました。");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "カテゴリの登録に失敗しました: " + e.getMessage());
			return "redirect:/admin/categories/register";
		}

		return "redirect:/admin/categories";
	}

	// カテゴリ削除
	@PostMapping("/admin/categories/{id}/delete")
	public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		try {
			categoryService.delete(id);
			redirectAttributes.addFlashAttribute("successMessage", "カテゴリを削除しました。");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "カテゴリの削除に失敗しました: " + e.getMessage());
		}
		return "redirect:/admin/categories";
	}
}