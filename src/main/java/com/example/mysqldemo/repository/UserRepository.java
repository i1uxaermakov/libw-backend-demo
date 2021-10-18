package com.example.mysqldemo.repository;

import com.example.mysqldemo.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findUserByFirstName(String firstName);
    List<User> findAll();
}
