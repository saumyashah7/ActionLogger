package com.utsa.eager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utsa.eager.model.User;
import com.utsa.eager.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;


	@Override
	public int addorgetUser(String address) {
		// TODO Auto-generated method stub
		User u=userRepository.findByMachineaddress(address);
		if(u==null) 
		{
			u=new User();
			u.setMachineaddress(address);
			u=userRepository.save(u);
		}
		return (int)u.getId();
	}

}
