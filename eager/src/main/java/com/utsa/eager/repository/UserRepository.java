package com.utsa.eager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.utsa.eager.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByMachineaddress(String address);
	

}
