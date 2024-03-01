package com.example.demo;

import org.springframework.data.annotation.Id;

record Customer(@Id Integer id, String name) {
}
