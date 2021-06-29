package com.utsa.eager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utsa.eager.model.UsageMetric;
import com.utsa.eager.model.UsageMetricId;
import com.utsa.eager.repository.UsageMetricRepository;

@Service
public class UsageMetricServiceImpl implements UsageMetricService {
	
	@Autowired
	UsageMetricRepository usageMetricRepository;
	
	@Override
	public void updateUsage(UsageMetric usageMetric) {
		// TODO Auto-generated method stub
		usageMetricRepository.save(usageMetric);
	}

	@Override
	public void incrementUsage(UsageMetric usageMetric) {
		// TODO Auto-generated method stub
		
		Optional<UsageMetric> um=null;
		um = usageMetricRepository.findById(new UsageMetricId(usageMetric.getUserid(),usageMetric.getApplication(),usageMetric.getMetric()));
		if(um.isPresent()) 
		{
			usageMetric.setUsage(um.get().getUsage()+1);	
		}
		else 
		{
			usageMetric.setUsage(1);			
		}
		
		usageMetricRepository.save(usageMetric);
		if(!usageMetric.getMetric().equals("usage")) 
		{
			um = usageMetricRepository.findById(new UsageMetricId(usageMetric.getUserid(),usageMetric.getApplication(),"usage"));
			if(um.isPresent()) 
			{
				usageMetric.setUsage(um.get().getUsage()+1);	
			}
			else 
			{
				usageMetric.setUsage(1);			
			}
			usageMetric.setMetric("usage");
			usageMetricRepository.save(usageMetric);
			
		}
		
		  
	}

	@Override
	public List<UsageMetric> getAppUsage() {
		// TODO Auto-generated method stub
		
		//return null;
		return usageMetricRepository.findAll();
	}



}
