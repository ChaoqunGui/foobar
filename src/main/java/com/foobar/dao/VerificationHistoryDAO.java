package com.dr.license.dao;

import java.util.Collection;

import com.dr.license.entity.VerificationHistory;

/**
 * Use mysql for Database
 * 
 * @author thomas.gui
 *
 */

public interface VerificationHistoryDAO {

  public VerificationHistory getVerificationHistoryById(int id);

  public VerificationHistory createVerificationHistory(VerificationHistory vr);

  public Collection<VerificationHistory> listVerificationHistorys();
}
