package com.dr.license.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dr.license.dao.LicenseDAO;
import com.dr.license.dao.VerificationHistoryDAO;
import com.dr.license.entity.License;
import com.dr.license.entity.VerifyResponse;
import com.dr.license.entity.VerificationHistory;
import com.dr.license.entity.VerificationHistory.enumVerificationFailure;
import com.dr.license.util.LCRandom;
import com.dr.license.util.DateUtil;

/**
 * license service implementation
 * 
 * @author wenle.zhao, thomas.gui
 *
 */
@Service("licenseService")
public class LicenseServiceImpl implements LicenseService {

  static Logger logger = LoggerFactory.getLogger(LicenseServiceImpl.class);

  @Autowired
  @Qualifier("licenseDAO")
  private LicenseDAO licenseDAO;

  @Autowired
  @Qualifier("verificationhistorydDAO")
  private VerificationHistoryDAO verificationhistorydDAO;

  @Override
  public License createLicense() throws ParseException {

    for (int i = 0; i < 10; i++) {
      String licenseKey = LCRandom.genRandomString(20, 20);
      if (licenseDAO.getLicense(licenseKey) != null) { // duplicated license key
        continue;
      }
      Timestamp createdTime = new Timestamp(System.currentTimeMillis());
      // TODO:current license type is permanent, set the expired date to
      // 2200-01-01
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      Timestamp expiredTime = new Timestamp(format.parse("2200-01-01")
          .getTime());
      License license = new License();
      // Server DB
      license.setStorage(License.enumLicenseStorage.S.ordinal());
      license.setKeyString(licenseKey);
      license.setCreatedDate(createdTime);
      license.setExpiredDate(expiredTime);
      return licenseDAO.createLicense(license);
    }

    // too many licenses in the system, or bad luck
    return null; // TODO: throw exception
  }

  @Override
  public License getLicense(String licenseKey) {
    return licenseDAO.getLicense(licenseKey);
  }

  @Override
  public void activateLicense(String licenseKey, String clientId,
      String sharedSecret) {
    License license = getLicense(licenseKey);
    if (license.getActivatedDate() != null) { // already activated
      logger.warn("reactivating license " + licenseKey);
      // do nothing
      return;
    }
    license.setActivatedDate(new Timestamp(System.currentTimeMillis()));
    license.setSharedSecret(sharedSecret);
    license.setClientId(clientId);

    licenseDAO.updateLicenseByLicensekey(license);

  }

  @Override
  public VerifyResponse verifyLicense(long currentTime, String licenseKey,
      long verifyTime, String signature) throws Exception {

    License saved = getLicense(licenseKey);
    
    if (saved == null) {
      String failInfo = getVerificationFailureString(VerificationHistory.enumVerificationFailure.LICENSE_NOT_EXITS);
      logger.debug("license is not activated");
      throw new IllegalStateException(failInfo);
    }
    
    // if client fails to verify, need to save the record
    VerificationHistory vr = new VerificationHistory(saved.getClientId(),
        DateUtil.longToString(currentTime, "yyyy-MM-dd HH:mm:ss"));

    // license haven't been activated yet
    if (saved.getActivatedDate() == null
        || saved.getSharedSecret() == null) {
      String failInfo = getVerificationFailureString(VerificationHistory.enumVerificationFailure.LICENSE_IS_NOT_ACTIVATED);
      vr.setFailInfo(failInfo);
      verificationhistorydDAO.createVerificationHistory(vr);
      logger.debug("license is not activated");
      throw new IllegalStateException(failInfo);
    }

    // license is over time
    if ((saved.getSuspendedDate() != null && saved.getActivatedDate() != null && saved
        .getSuspendedDate().before(saved.getActivatedDate()))
        || (saved.getExpiredDate() != null && saved.getActivatedDate() != null && saved
            .getExpiredDate().before(saved.getActivatedDate()))) {
      String failInfo = getVerificationFailureString(VerificationHistory.enumVerificationFailure.LICENSE_IS_EXPIRED);
      vr.setFailInfo(failInfo);
      verificationhistorydDAO.createVerificationHistory(vr);
      logger.debug("license is expired");
      throw new IllegalStateException(failInfo);
    }

    // The date is not in a week
    try {
      if (DateUtil.daysBeforeNow(verifyTime) > 7) {
        String failInfo = getVerificationFailureString(VerificationHistory.enumVerificationFailure.REQUEST_IS_OVERDUE);
        vr.setFailInfo(failInfo);
        verificationhistorydDAO.createVerificationHistory(vr);
        logger.debug("request is overdue");
        throw new IllegalStateException(failInfo);
      }
    } catch (ParseException e) {
      String failInfo = getVerificationFailureString(VerificationHistory.enumVerificationFailure.REQUEST_FORMAT_IS_WRONG);
      vr.setFailInfo(failInfo);
      verificationhistorydDAO.createVerificationHistory(vr);
      logger.debug("request format is wrong");
      throw new IllegalStateException(failInfo);
    }

    // Hash license key, client identifier and client's host name and the
    // request header's date
    // to generate the signature
    // verify signature
    String hexSig = computeSignature(verifyTime, saved.getKeyString(),
        saved.getClientId(), saved.getSharedSecret());
    logger.info("computed signature is" + hexSig);

    if (!StringUtils.equals(hexSig, signature)) {
      String failInfo = getVerificationFailureString(VerificationHistory.enumVerificationFailure.INVALID_SIGNATURE);
      vr.setFailInfo(failInfo);
      verificationhistorydDAO.createVerificationHistory(vr);
      logger.debug("invalid signature");
      throw new IllegalStateException(failInfo);
    }

    // generate verify response signature
    String sigResp = computeSignature(currentTime, saved.getKeyString(),
        saved.getClientId(), saved.getSharedSecret());
    VerifyResponse response = new VerifyResponse(saved.getClientId(),
        saved.getKeyString(), sigResp, System.currentTimeMillis(), saved
            .getExpiredDate().getTime());
    return response;
  }

  @Override
  public void suspendLicense(String licenseKey) {
    License license = getLicense(licenseKey);

    license.setSuspendedDate(new Timestamp(System.currentTimeMillis()));

    licenseDAO.updateLicenseByLicensekey(license);
  }

  @Override
  public Collection<License> listLicenses() {
    return licenseDAO.listLicenses();
  }

  /**
   * @param millisecond
   *          could be current timesecond
   * @param licenseKey
   *          the generated license key
   * @param clientId
   *          the client identifier
   * @param sharedSecret
   *          the secret we shared with client
   * @return signature generated by Hex codec
   */
  private String computeSignature(long millisecond, String licenseKey,
      String clientId, String sharedSecret) {
    StringBuilder sb = new StringBuilder();
    sb.append(licenseKey).append('\n').append(clientId).append('\n')
        .append(millisecond).append('\n');

    String signature = null;
    try {
      Mac mac = Mac.getInstance("HmacSHA1");
      SecretKeySpec secret = new SecretKeySpec(sharedSecret.getBytes(),
          mac.getAlgorithm());
      mac.init(secret);
      byte[] digest = mac.doFinal(sb.toString().getBytes());
      signature = new String(Hex.encodeHex(digest));
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return signature;
  }

  /**
   * Get the verification failure string by the enumVerificationFailure type
   * 
   * @param evf
   * @return
   */
  private String getVerificationFailureString(enumVerificationFailure evf) {
    switch (evf) {
    case LICENSE_NOT_EXITS:
      return "License not exists";
    case LICENSE_IS_NOT_ACTIVATED:
      return "License is not activated";
    case LICENSE_IS_EXPIRED:
      return "License is expired";
    case REQUEST_IS_OVERDUE:
      return "Request is overdue";
    case REQUEST_FORMAT_IS_WRONG:
      return "Request format is not right";
    case INVALID_SIGNATURE:
      return "Invalid signature";
    default:
      return "Unknow error";
    }
  }
}
