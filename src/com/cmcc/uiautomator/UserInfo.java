package com.cmcc.uiautomator;

import java.io.Serializable;

/**
 * 
 * @author Admin
 *
 */
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String userName;
	private String password;
	/**
	 * userType 
	 * 0 手机账号 
	 * 1 邮箱账号 
	 * 2 自定义账号 
	 * 3 无账号
	 */
	private String userType;
	/**
	 * taskType
	 * 1 PV操作 
	 * 2 支付操作
	 */
	private String taskType;
	private String customerKey;
	private String createTime;
	private String channelId;
	private String resultCode;
	private String resultMsg;
	private String setOrNot;
	private String duration;
	private String preBalance;
	private String actualBalance;
	private String packageName;

	public String getCustomerKey() {
		return customerKey;
	}

	public UserInfo setCustomerKey(String customerKey) {
		this.customerKey = customerKey;
		return this;
	}

	public String getCreateTime() {
		return createTime;
	}

	public UserInfo setCreateTime(String createTime) {
		this.createTime = createTime;
		return this;
	}

	public String getUserName() {
		return userName;
	}

	public UserInfo setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public String getUserType() {
		return userType;
	}

	public UserInfo setUserType(String userType) {
		this.userType = userType;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public UserInfo setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getSetOrNot() {
		return setOrNot;
	}

	public UserInfo setSetOrNot(String setOrNot) {
		this.setOrNot = setOrNot;
		return this;
	}

	public String getResultCode() {
		return resultCode;
	}

	public UserInfo setResultCode(String resultCode) {
		this.resultCode = resultCode;
		return this;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public UserInfo setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
		return this;
	}

	public String getTaskType() {
		return taskType;
	}

	public UserInfo setTaskType(String taskType) {
		this.taskType = taskType;
		return this;
	}

	public String getChannelId() {
		return channelId;
	}

	public UserInfo setChannelId(String channelId) {
		this.channelId = channelId;
		return this;
	}

	public String getDuration() {
		return duration;
	}

	public UserInfo setDuration(String duration) {
		this.duration = duration;
		return this;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPreBalance() {
		return preBalance;
	}

	public UserInfo setPreBalance(String preBalance) {
		this.preBalance = preBalance;
		return this;
	}

	public String getActualBalance() {
		return actualBalance;
	}

	public UserInfo setActualBalance(String actualBalance) {
		this.actualBalance = actualBalance;
		return this;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}
