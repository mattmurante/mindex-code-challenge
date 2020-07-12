package com.mindex.challenge.dao;

import org.springframework.stereotype.Repository;

import com.mindex.challenge.data.Employee;

import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Employee findByEmployeeId(String id);
}
