package com.example.demo;

import org.springframework.data.repository.ListCrudRepository;

interface CustomerRepository extends ListCrudRepository<Customer, Integer> {

}
