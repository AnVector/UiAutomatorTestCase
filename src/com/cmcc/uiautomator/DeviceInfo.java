package com.cmcc.uiautomator;

import java.io.Serializable;

public class DeviceInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String imei;
	private String macAddr;
	private String androidId;
	private String SSID;
	private String BSSID;
	private String phoneNum;
	private String ICCID;
	private String IMSI;
	private String simStatus;
	private String operatorId;
	private String operatorName;
	private String countryCode;
	private String model;
	private String manufacture;
	private String hardware;
	private String brand;
	public String getImei() {
		return imei;
	}
	public DeviceInfo setImei(String imei) {
		this.imei = imei;
		return this;
	}
	public String getMacAddr() {
		return macAddr;
	}
	public DeviceInfo setMacAddr(String macAddr) {
		this.macAddr = macAddr;
		return this;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getAndroidId() {
		return androidId;
	}
	public DeviceInfo setAndroidId(String androidId) {
		this.androidId = androidId;
		return this;
	}
	public String getSSID() {
		return SSID;
	}
	public DeviceInfo setSSID(String sSID) {
		SSID = sSID;
		return this;
	}
	public String getBSSID() {
		return BSSID;
	}
	public DeviceInfo setBSSID(String bSSID) {
		BSSID = bSSID;
		return this;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public DeviceInfo setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
		return this;
	}
	public String getICCID() {
		return ICCID;
	}
	public DeviceInfo setICCID(String iCCID) {
		ICCID = iCCID;
		return this;
	}
	public String getIMSI() {
		return IMSI;
	}
	public DeviceInfo setIMSI(String iMSI) {
		IMSI = iMSI;
		return this;
	}
	public String getSimStatus() {
		return simStatus;
	}
	public DeviceInfo setSimStatus(String simStatus) {
		this.simStatus = simStatus;
		return this;
	}
	public String getOperatorId() {
		return operatorId;
	}
	public DeviceInfo setOperatorId(String operatorId) {
		this.operatorId = operatorId;
		return this;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public DeviceInfo setOperatorName(String operatorName) {
		this.operatorName = operatorName;
		return this;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public DeviceInfo setCountryCode(String countryCode) {
		this.countryCode = countryCode;
		return this;
	}
	public String getModel() {
		return model;
	}
	public DeviceInfo setModel(String model) {
		this.model = model;
		return this;
	}
	public String getManufacture() {
		return manufacture;
	}
	public DeviceInfo setManufacture(String manufacture) {
		this.manufacture = manufacture;
		return this;
	}
	public String getHardware() {
		return hardware;
	}
	public DeviceInfo setHardware(String hardware) {
		this.hardware = hardware;
		return this;
	}
	public String getBrand() {
		return brand;
	}
	public DeviceInfo setBrand(String brand) {
		this.brand = brand;
		return this;
	}
	
}
