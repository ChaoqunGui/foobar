package com.dr.license.entity;

import java.sql.Timestamp;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 * License value object
 * 
 * @author wenle.zhao, thomas.gui
 *
 */
public class License {

  /**
   * @author thomas.gui this enumeration is used for License's property storage
   *         to show its storage type(Server or Client).
   *
   */
  public enum enumLicenseStorage {
    S, C;
  }

  private long id;
  private int storage;
  private String keyString;
  private String clientId;
  private String sharedSecret;
  private String licenseType;
  private Timestamp createdDate;
  private Timestamp expiredDate;
  private Timestamp activatedDate;
  private Timestamp suspendedDate;

  public License() {
  }

  public License(long id, int storage, String keyString, String clientId,
      String sharedSecret, String licenseType, Timestamp createdDate,
      Timestamp expiredDate, Timestamp activatedDate, Timestamp suspendedDate) {
    super();
    this.id = id;
    this.storage = storage;
    this.keyString = keyString;
    this.clientId = clientId;
    this.sharedSecret = sharedSecret;
    this.licenseType = licenseType;
    this.createdDate = createdDate;
    this.expiredDate = expiredDate;
    this.activatedDate = activatedDate;
    this.suspendedDate = suspendedDate;
  }

  public String getKeyString() {
    return keyString;
  }

  public void setKeyString(String keyString) {
    this.keyString = keyString;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  @JsonIgnore
  public String getSharedSecret() {
    return sharedSecret;
  }

  public void setSharedSecret(String sharedSecret) {
    this.sharedSecret = sharedSecret;
  }

  public Timestamp getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public Timestamp getExpiredDate() {
    return expiredDate;
  }

  public void setExpiredDate(Timestamp expiredDate) {
    this.expiredDate = expiredDate;
  }

  public Timestamp getActivatedDate() {
    return activatedDate;
  }

  public void setActivatedDate(Timestamp activatedDate) {
    this.activatedDate = activatedDate;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getStorage() {
    return storage;
  }

  public void setStorage(int storage) {
    this.storage = storage;
  }

  public String getLicenseType() {
    return licenseType;
  }

  public void setLicenseType(String licenseType) {
    this.licenseType = licenseType;
  }

  public Timestamp getSuspendedDate() {
    return suspendedDate;
  }

  public void setSuspendedDate(Timestamp suspendedDate) {
    this.suspendedDate = suspendedDate;
  }

  @Override
  public String toString() {
    return "License [id=" + id + ", storage=" + storage + ", keyString="
        + keyString + ", clientId=" + clientId + ", sharedSecret="
        + sharedSecret + ", licenseType=" + licenseType + ", createdDate="
        + createdDate + ", expiredDate=" + expiredDate + ", activatedDate="
        + activatedDate + ", suspendedDate=" + suspendedDate + "]\n";
  }
}
