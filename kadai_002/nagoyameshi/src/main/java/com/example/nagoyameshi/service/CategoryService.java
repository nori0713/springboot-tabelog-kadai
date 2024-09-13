package com.example.nagoyameshi.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.form.CategoryForm;
import com.example.nagoyameshi.repository.CategoryRepository;

@Service
@Transactional
public class CategoryService {
	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	// カテゴリを作成するメソッド
	public void create(CategoryForm categoryForm) {
		Category category = new Category();
		category.setName(categoryForm.getName());
		categoryRepository.save(category); // 新しいカテゴリを保存
	}

	// カテゴリを更新するメソッド
	public void update(Integer id, CategoryForm categoryForm) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("カテゴリが見つかりませんでした。ID: " + id));

		// カテゴリ名を更新
		category.setName(categoryForm.getName());

		categoryRepository.save(category); // 更新したカテゴリを保存
	}

	// カテゴリを削除するメソッド
	public void delete(Integer id) {
		try {
			categoryRepository.deleteById(id); // 指定されたIDのカテゴリを削除
		} catch (EmptyResultDataAccessException e) {
			// IDに該当するカテゴリが存在しない場合のエラーハンドリング
			throw new IllegalArgumentException("カテゴリが見つかりませんでした。ID: " + id, e);
		} catch (Exception e) {
			// カテゴリが他のエンティティと関連している場合のエラーハンドリング
			throw new IllegalStateException("カテゴリを削除できません。他のエンティティと関連付けられています。", e);
		}
	}
}