package com.cricket.auction.model;

import lombok.Generated;

import java.util.Map;

public interface PlayerResponseProjection {

    public Integer getId();
    public String getName();
    public String getPhoto();
    public Integer getBasePrice();
    public String getStats();
    public String getStatus() ;
    public Integer getSkillId();
    public String getSkillName();
    public Integer getSoldPrice();
    public Integer getTeamId() ;
    public String getTeamName() ;
    public Integer getIsNewPlayer();
    public Integer getTournamentId();
    public Integer getIsConsiderInAuction();
    public String getGroupCode();
}
