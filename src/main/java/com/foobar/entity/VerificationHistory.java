package com.dr.license.entity;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

/**
 * @author thomas.gui
 *
 */
public class VerificationHistory {

  public enum enumVerificationFailure {
    LICENSE_NOT_EXITS,LICENSE_IS_NOT_ACTIVATED, LICENSE_IS_EXPIRED, REQUEST_IS_OVERDUE, REQUEST_FORMAT_IS_WRONG, INVALID_SIGNATURE
  }

  private int id;
  private String clientId;
  private String failInfo;
  private String requestTime;

  public VerificationHistory(String clientId, String requestTime) {
    this.clientId = clientId;
    if (StringUtils.isEmpty(requestTime)) {
      Timestamp now = new Timestamp(System.currentTimeMillis());
      this.requestTime = now.toString();
    } else {
      this.requestTime = requestTime;
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getRequestTime() {
    return requestTime;
  }

  public void setRequestTime(String requestTime) {
    this.requestTime = requestTime;
  }

  public String getFailInfo() {
    return failInfo;
  }

  public void setFailInfo(String failInfo) {
    this.failInfo = failInfo;
  }
}
