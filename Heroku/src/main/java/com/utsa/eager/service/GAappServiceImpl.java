package com.utsa.eager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utsa.eager.model.GAapp;
import com.utsa.eager.repository.GAappRepository;

@Service
public class GAappServiceImpl implements GAappService {
	
	@Autowired
	GAappRepository gaappRepository;

	@Override
	public void addGAApp(GAapp gaapp) {
		// TODO Auto-generated method stub
		gaappRepository.save(gaapp);
		
	}

	@Override
	public List<GAapp> findAllGAProjects() {
		// TODO Auto-generated method stub
		return gaappRepository.findAll();
	}

}
