package com.gumilicai.platform.entity;

import java.util.Date;

public class InvestInfo {
	private String id;
	private String bid;
	private String burl;
	private String username;
	private Float amount;
	private Float actualAmount;
	private Float income;
	private Date investAt;
	private Date repayAt;
	private String[] tags;
	private String status;
	/**
	 * 默认：0未转让1表示转让
	 */
	private String transState;
	/**
	 * 转让时间 默认为空，已转让：格式2015-09-01 20:30:12精确到秒
	 */
	private Date transTime;
	/**
	 * 已回款本金
	 */
	private Float allBackPrincipal;
	/**
	 * 已回款利息
	 */
	private Float allBackInterest;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getBurl() {
		return burl;
	}
	public void setBurl(String burl) {
		this.burl = burl;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Float getAmount() {
		return amount;
	}
	public void setAmount(Float amount) {
		this.amount = amount;
	}
	public Float getActualAmount() {
		return actualAmount;
	}
	public void setActualAmount(Float actualAmount) {
		this.actualAmount = actualAmount;
	}
	public Float getIncome() {
		return income;
	}
	public void setIncome(Float income) {
		this.income = income;
	}
	public Date getInvestAt() {
		return investAt;
	}
	public void setInvestAt(Date investAt) {
		this.investAt = investAt;
	}
	public Date getRepayAt() {
		return repayAt;
	}
	public void setRepayAt(Date repayAt) {
		this.repayAt = repayAt;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getTransState() {
		return transState;
	}

	public void setTransState(String transState) {
		this.transState = transState;
	}

	public Date getTransTime() {
		return transTime;
	}

	public void setTransTime(Date transTime) {
		this.transTime = transTime;
	}

	public Float getAllBackPrincipal() {
		return allBackPrincipal;
	}

	public void setAllBackPrincipal(Float allBackPrincipal) {
		this.allBackPrincipal = allBackPrincipal;
	}

	public Float getAllBackInterest() {
		return allBackInterest;
	}

	public void setAllBackInterest(Float allBackInterest) {
		this.allBackInterest = allBackInterest;
	}
}
