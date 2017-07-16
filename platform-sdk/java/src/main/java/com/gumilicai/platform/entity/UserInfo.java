package com.gumilicai.platform.entity;

import java.util.Date;

public class UserInfo extends  SaltInfo {
	// 谷米理财用户名
	private String username;
	// 谷米财富平台用户编号
	private String platformUserno;
	
	// 平台用户名
	private String usernamep;
	
	// 用户在平台的注册时间
	private Date registerAt;
	
	// 平台用户和谷米理财用户绑定时间
	private Date bindAt;
	
	// 绑定类型，0:表示谷米理财带来的新用户，1:表示平台已有用户
	private int bindType;
	
	private Assets assets;
	
	// 标签
	private String[] tags;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsernamep() {
		return usernamep;
	}

	public void setUsernamep(String usernamep) {
		this.usernamep = usernamep;
	}

	public Date getRegisterAt() {
		return registerAt;
	}

	public void setRegisterAt(Date registerAt) {
		this.registerAt = registerAt;
	}

	public Date getBindAt() {
		return bindAt;
	}

	public void setBindAt(Date bindAt) {
		this.bindAt = bindAt;
	}

	public int getBindType() {
		return bindType;
	}

	public void setBindType(int bindType) {
		this.bindType = bindType;
	}

	public Assets getAssets() {
		return assets;
	}

	public void setAssets(Assets assets) {
		this.assets = assets;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getPlatformUserno() {
		return platformUserno;
	}

	public void setPlatformUserno(String platformUserno) {
		this.platformUserno = platformUserno;
	}
}
