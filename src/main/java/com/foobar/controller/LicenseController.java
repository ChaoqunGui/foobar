package com.dr.license.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dr.license.entity.VerifyResponse;
import com.dr.license.service.LicenseService;

@Controller("licenseController")
@RequestMapping("/")
public class LicenseController {
  private Logger logger = LoggerFactory.getLogger(LicenseController.class);
  @Autowired
  @Qualifier("licenseService")
  protected LicenseService licenseSvc;

  /**
   * client verify the license with the server
   * 
   * @param request
   * @param response
   * @param licenseKey
   * @return
   * @throws IOException
   */
  @RequestMapping(value = "/license/{key}", method = RequestMethod.GET)
  public @ResponseBody VerifyResponse verifyLicense(
      final HttpServletRequest request, final HttpServletResponse response,
      @PathVariable("key") String licenseKey) throws IOException {
    logger.debug("Start to verify a new license...");

    // verify license
    String signature = request.getParameter("signature");
    
    if (StringUtils.isEmpty(request.getHeader("X-VerifyTime"))) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "No verification time");
      return null;
    }
    long date = Long.valueOf(request.getHeader("X-VerifyTime").toString());
    long currentDate = System.currentTimeMillis();
    response.setHeader("X-VerifyTime", String.valueOf(currentDate));
    if (StringUtils.isEmpty(signature)) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "invalid signature");
      return null;
    } else {
      try {
        VerifyResponse vr = licenseSvc.verifyLicense(currentDate, licenseKey,
            date, signature);
        return vr;
      } catch (Exception e) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
      }
    }
    return null;
  }

}
