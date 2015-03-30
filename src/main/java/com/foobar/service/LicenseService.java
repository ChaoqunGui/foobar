package com.dr.license.service;

import java.text.ParseException;
import java.util.Collection;

import com.dr.license.entity.License;
import com.dr.license.entity.VerifyResponse;

/**
 * license service interface provide
 * 
 * @author wenle.zhao, thomas.gui
 *
 */
public interface LicenseService {

  /**
   * @return create a new license with
   * @throws ParseException
   */
  License createLicense() throws ParseException;

  /**
   * @param licenseKey
   * @return
   */
  License getLicense(String licenseKey);

  /**
   * use client identity and shared secret to activate an existed license
   * 
   * @param licenseKey
   *          get the license with the license key
   * @param clientId
   * @param sharedSecret
   * 
   */
  void activateLicense(String licenseKey, String clientId, String sharedSecret);

  /**
   * @param currentTime
   *          use currentDate(millisecond) to calculate a signature and return
   *          the signature to the client.
   * @param licenseKey
   *          license key get from the client
   * @param verifyTime
   *          the time client try to verify the license with the server
   * @param signature
   *          this signature is calculated by the license client
   * @return
   * @throws Exception
   *           use exception information for the HTTP status code
   */
  VerifyResponse verifyLicense(long currentTime, String licenseKey,
      long verifyTime, String signature) throws Exception;

  /**
   * If the license is used by some illegal users, we can use suspend the
   * license by license key.
   * 
   * @param licenseKey
   */
  void suspendLicense(String licenseKey);

  /**
   * @return
   */
  Collection<License> listLicenses();
}
