package com.grp3.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grp3.models.Pledge;
import com.grp3.repositories.IPledgeRepository;

@Service
public class PledgeService implements IPledgeService {
	
	@Autowired
	private IPledgeRepository _pledgeRepo;

	@Override
	public boolean cancelPledge(Pledge pledge) {
		if(pledge.getProject().getCompleted()) {
			return false;
		}
		pledge.setActive(false);
		return true;
	}

	@Override
	public void cancelPledges(List<Pledge> pledges) {
		for (Pledge pledge : pledges) {
			cancelPledge(pledge);
			_pledgeRepo.save(pledge);
		};
		
	}

	@Override
	public void save(Pledge pledge) {
		_pledgeRepo.save(pledge);
		
	}

}
