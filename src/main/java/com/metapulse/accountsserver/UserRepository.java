package com.metapulse.accountsserver;

import org.springframework.data.repository.CrudRepository;

// import com.example.accessingdatamysql.User;
// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository

import com.metapulse.accountsserver.User;

public interface UserRepository extends CrudRepository<User, Integer>{
    User findByName(String name);

}
