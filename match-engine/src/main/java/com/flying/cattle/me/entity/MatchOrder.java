package com.flying.cattle.me.entity;

import java.util.ArrayList;
import java.util.List;

public class MatchOrder extends Order{
	
	private static final long serialVersionUID = -4205367001273335512L;

	private List<LevelMatch> list = new ArrayList<LevelMatch>();

	public List<LevelMatch> getList() {
		return list;
	}

	public void setList(List<LevelMatch> list) {
		this.list = list;
	}
}
