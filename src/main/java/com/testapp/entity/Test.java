package com.testapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tests")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    // getters & setters
}

