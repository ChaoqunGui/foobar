package com.dr.license.dao;

import java.util.Collection;

import com.dr.license.entity.License;

/**
 * Use mysql for Database
 * 
 * @author wenle.zhao, Thomas.Gui
 *
 */

public interface LicenseDAO {

  public License getLicense(String licenseKey);

  public License createLicense(License license);

  public Collection<License> listLicenses();

  public int updateLicenseById(License license);

  public int updateLicenseByLicensekey(License license);
}
