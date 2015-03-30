package com.dr.license.entity;

/**
 * @author thomas.gui This class is used to return response to the client, when
 *         client verify the signature.
 *
 */
public class VerifyResponse {
  private String clientId;
  private String keyString;
  private String signature;
  private long verifyDate;
  private long expiredDate;

  public VerifyResponse(String clientId, String keyString, String signature,
      long verifyDate, long expiredDate) {
    super();
    this.clientId = clientId;
    this.keyString = keyString;
    this.signature = signature;
    this.verifyDate = verifyDate;
    this.expiredDate = expiredDate;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getKeyString() {
    return keyString;
  }

  public void setKeyString(String keyString) {
    this.keyString = keyString;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public long getVerifyDate() {
    return verifyDate;
  }

  public void setVerifyDate(long verifyDate) {
    this.verifyDate = verifyDate;
  }

  public long getExpiredDate() {
    return expiredDate;
  }

  public void setExpiredDate(long expiredDate) {
    this.expiredDate = expiredDate;
  }

}
