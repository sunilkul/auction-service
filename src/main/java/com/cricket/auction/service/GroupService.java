package com.cricket.auction.service;

import com.cricket.auction.model.PlayerGroupMaster;
import com.cricket.auction.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {
 //   Add group-related methods here and call player repository if needed to get player group master list
    @Autowired
    private PlayerRepository playerRepository;

    public List<PlayerGroupMaster> getGroupInfo() {
        return playerRepository.fetchPlayerGroups();
    }

}
