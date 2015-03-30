package com.dr.license.dao.impl;

import java.util.Collection;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import com.dr.license.dao.VerificationHistoryDAO;
import com.dr.license.entity.VerificationHistory;

/**
 * Use mysql for Database
 * 
 * @author thomas.gui
 *
 */
@Repository("verificationhistorydDAO")
public class VerificationHistoryDAOImpl extends SqlSessionDaoSupport implements
    VerificationHistoryDAO {
  @Override
  public VerificationHistory getVerificationHistoryById(int id) {
    return this.getSqlSession().selectOne("getVerificationHistoryById", id);
  }

  @Override
  public VerificationHistory createVerificationHistory(VerificationHistory vr) {
    this.getSqlSession().insert("createVerificationHistory", vr);
    return vr;
  }

  @Override
  public Collection<VerificationHistory> listVerificationHistorys() {
    return this.getSqlSession().selectList("listVerificationHistorys");
  }

}
