package com.utsa.eager.service;

import java.util.List;

import com.utsa.eager.model.GAapp;

public interface GAappService {
	
	public void addGAApp(GAapp gaapp);
	public List<GAapp> findAllGAProjects();

}
