package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotEmpty;

public class CategoryForm {

	@NotEmpty(message = "カテゴリ名を入力してください。")
	private String name;

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}