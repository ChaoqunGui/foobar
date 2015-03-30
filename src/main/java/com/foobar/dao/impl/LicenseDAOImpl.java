package com.dr.license.dao.impl;

import java.util.Collection;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import com.dr.license.dao.LicenseDAO;
import com.dr.license.entity.License;

/**
 * Use mysql for Database
 * 
 * @author wenle.zhao, Thomas.Gui
 *
 */
@Repository("licenseDAO")
public class LicenseDAOImpl extends SqlSessionDaoSupport implements LicenseDAO {
  @Override
  public License getLicense(String licenseKey) {
    return this.getSqlSession().selectOne("getLicenseByLicensekey", licenseKey);
  }

  @Override
  public License createLicense(License license) {
    this.getSqlSession().insert("insertLicense", license);
    return license;
  }

  @Override
  public int updateLicenseById(License license) {
    return this.getSqlSession().update("updateLicenseById", license);
  }

  @Override
  public int updateLicenseByLicensekey(License license) {
    return this.getSqlSession().update("updateLicenseByLicensekey", license);
  }

  @Override
  public Collection<License> listLicenses() {
    return this.getSqlSession().selectList("getAllLicenses");
  }

}
