package com.dr.license.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.dr.license.entity.License;

/**
 * LicenseOp(License operation) is used for creating, activating, listing
 * licenses
 * 
 * @author thomas.gui
 *
 */
public class LicenseOp {
  private static String LIST_LICENSE = "listlic";
  private static String CREATE_LICENSE = "createlic";
  private static String ACTIVATE_LICENSE = "activatelic";
  private Connection connection = null;
  private static LicenseOp licOp = null;

  /**
   * simple singleton
   * 
   * @return
   */
  public static LicenseOp getLicenseOp() {
    // if null, new LicenseOp
    if (licOp == null) {
      licOp = new LicenseOp();
    }
    return licOp;
  }

  /**
   * initialize the connection
   */
  private void init() {
    Properties prop = new Properties();// JDBC properties
    InputStream is;
    try {
      is = this.getClass().getResourceAsStream("/config/jdbc.properties");
      prop.load(is);
      Class.forName(prop.getProperty("jdbc.driverClassName"));
      connection = java.sql.DriverManager.getConnection(
          prop.getProperty("jdbc.url"), prop.getProperty("jdbc.username"),
          prop.getProperty("jdbc.password"));
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param licenseKey
   * @return
   */
  private License getLicense(String licenseKey) {
    PreparedStatement pst = null;
    ResultSet rs = null;
    License license = null;
    try {
      if (connection == null) {
        init();
      }
      Class.forName("com.mysql.jdbc.Driver");
      // sql string
      String sql = "select * from t_license where keystring ='" + licenseKey
          + "'";
      pst = connection.prepareStatement(sql);
      rs = pst.executeQuery();

      if (rs != null) {
        if (rs.next()) {
          license = new License();
          license.setId(rs.getLong("id"));
          license.setStorage(rs.getInt("storage"));
          license.setClientId(rs.getString("client_ident"));
          license.setSharedSecret(rs.getString("shared_secret"));
          license.setKeyString(rs.getString("keystring"));
          license.setLicenseType(rs.getString("license_type"));
          license.setCreatedDate(rs.getTimestamp("created_d"));
          license.setActivatedDate(rs.getTimestamp("activated_d"));
          license.setExpiredDate(rs.getTimestamp("expired_d"));
          license.setSuspendedDate(rs.getTimestamp("suspended_d"));
        }
      }

      return license;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (pst != null) {
          pst.close();
          pst = null;
        }
        if (connection != null) {
          connection.close();
          connection = null;
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return license;
  }

  private List<License> getAllLicenses() {
    PreparedStatement pst = null;
    ResultSet rs = null;
    License license = null;
    List<License> lics = new ArrayList<License>();
    try {
      if (connection == null) {
        init();
      }
      Class.forName("com.mysql.jdbc.Driver");
      // sql string
      String sql = "select * from t_license order by created_d";
      pst = connection.prepareStatement(sql);
      rs = pst.executeQuery();

      if (rs != null) {
        while (rs.next()) {
          license = new License();
          license.setId(rs.getLong("id"));
          license.setStorage(rs.getInt("storage"));
          license.setClientId(rs.getString("client_ident"));
          license.setSharedSecret(rs.getString("shared_secret"));
          license.setKeyString(rs.getString("keystring"));
          license.setLicenseType(rs.getString("license_type"));
          license.setCreatedDate(rs.getTimestamp("created_d"));
          license.setActivatedDate(rs.getTimestamp("activated_d"));
          license.setExpiredDate(rs.getTimestamp("expired_d"));
          license.setSuspendedDate(rs.getTimestamp("suspended_d"));
          lics.add(license);
        }
      }

      return lics;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (pst != null) {
          pst.close();
          pst = null;
        }
        if (connection != null) {
          connection.close();
          connection = null;
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return lics;
  }

  private License createLicense() throws ParseException {
    License license = null;
    for (int i = 0; i < 10; i++) {
      String licenseKey = LCRandom.genRandomString(20, 20);
      if (getLicense(licenseKey) != null) { // duplicated license key
        continue;
      }
      Timestamp createdTime = new Timestamp(System.currentTimeMillis());
      // TODO:current license type is permanent, set the expired date to
      // 2200-01-01
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      Timestamp expiredTime = new Timestamp(format.parse("2200-01-01")
          .getTime());

      PreparedStatement pst = null;
      try {
        if (connection == null) {
          init();
        }
        Class.forName("com.mysql.jdbc.Driver");
        // sql string
        String sql = "insert into t_license(`storage`, keystring,"
            + "created_d, expired_d) values(?,?,?,?)";
        pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pst.setInt(1, License.enumLicenseStorage.S.ordinal());
        pst.setString(2, licenseKey);
        pst.setTimestamp(3, createdTime);
        pst.setTimestamp(4, expiredTime);
        pst.executeUpdate();
        // get key
        ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
          license = new License();
          license.setId(rs.getInt(1));
          license.setStorage(License.enumLicenseStorage.S.ordinal());
          license.setKeyString(licenseKey);
          license.setCreatedDate(createdTime);
          license.setExpiredDate(expiredTime);
          return license;
        }
        rs.close();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        try {
          if (pst != null) {
            pst.close();
            pst = null;
          }
          if (connection != null) {
            connection.close();
            connection = null;
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      return license;
    }

    // too many licenses in the system, or bad luck
    return null; // TODO: throw exception
  }

  /**
   * Parse the input to license property
   * 
   * @param input
   * @return
   */
  private static License parseInput2License(String input) {
    License lic = null;
    String[] licParam = input.split("##");
    // if the input format is correct, e.g.
    // activatelic##keystring##clientid##sharedsecret
    if (licParam.length == 4) {
      lic = new License();
      lic.setKeyString(licParam[1]);
      lic.setClientId(licParam[2]);
      lic.setSharedSecret(licParam[3]);
    }
    return lic;
  }

  private int activateLicense(String licenseKey, String clientId,
      String sharedSecret) {
    PreparedStatement pst = null;
    int result = -1;
    License license = getLicense(licenseKey);
    if (license.getActivatedDate() != null) { // already activated
      // do nothing
      return 0;
    }

    try {
      if (connection == null) {
        init();
      }
      Class.forName("com.mysql.jdbc.Driver");
      // sql string
      String sql = "update t_license set client_ident = ?, shared_secret = ?,"
          + "activated_d = ? where keystring = ?";
      pst = connection.prepareStatement(sql);
      pst.setString(1, clientId);
      pst.setString(2, sharedSecret);
      pst.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
      pst.setString(4, license.getKeyString());
      result = pst.executeUpdate();

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (pst != null) {
          pst.close();
          pst = null;
        }
        if (connection != null) {
          connection.close();
          connection = null;
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    return result;
  }

  public static void main(String[] args) {
    try {
      String str = args[0];
      if (StringUtils.equals(str, LIST_LICENSE)) {
        List<License> lics = LicenseOp.getLicenseOp().getAllLicenses();
        System.out.println(lics);
      } else if (StringUtils.equals(str, CREATE_LICENSE)) {
        License lic = LicenseOp.getLicenseOp().createLicense();
        System.out.println("License has been created as below,\n" + lic);
      } else if (StringUtils.contains(str, ACTIVATE_LICENSE)) {
        // parse the input
        License lic = parseInput2License(str);
        if (lic == null) {
          System.out.println("Input is not correct!");
        } else {
          int result = LicenseOp.getLicenseOp().activateLicense(
              lic.getKeyString(), lic.getClientId(), lic.getSharedSecret());
          if (result > 0) {
            System.out.println("Activate license successfully!");
          } else if (result == 0) {
            System.out.println("The license has already been activated");
          } else {
            System.out.println("Failed to activate license!");
          }
        }
      } else {
        System.out.println("Unknown command");
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

}
