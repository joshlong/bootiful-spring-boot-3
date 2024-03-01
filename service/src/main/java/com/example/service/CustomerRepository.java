package com.example.service;

import org.springframework.data.repository.ListCrudRepository;

interface CustomerRepository extends ListCrudRepository<Customer, Integer> {

}
