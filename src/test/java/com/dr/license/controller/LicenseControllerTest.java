package com.dr.license.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dr.license.entity.VerifyResponse;
import com.dr.license.service.LicenseServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-config.xml" })
@WebAppConfiguration
public class LicenseControllerTest {
  private MockMvc mockMvc;
  @Mock
  private LicenseServiceImpl mockService;
  @Autowired
  private LicenseController licenseController;

  @Before
  public void init() {
    mockService = Mockito.mock(LicenseServiceImpl.class);
    licenseController.licenseSvc = mockService;
    this.mockMvc = MockMvcBuilders.standaloneSetup(licenseController).build();
  }
  
  @Test
  public void testEmptySignature() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
        "/license/FL3D97JVKYHUUVN8DK3J?signature=").header("X-VerifyTime",
        "123");
    mockMvc.perform(requestBuilder).andExpect(status().isForbidden());
  }

  @Test
  public void testVerify() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
        "/license/FL3D97JVKYHUUVN8DK3J?signature=abcdefg").header(
        "X-VerifyTime", 111);
    VerifyResponse vr = new VerifyResponse("surong.com", "welcome1",
        "FL3D97JVKYHUUVN8DK3J", 123, 234);

    Mockito.when(
        mockService.verifyLicense(Matchers.anyLong(), Matchers.anyString(),
            Matchers.anyLong(), Matchers.anyString())).thenReturn(vr);
    mockMvc.perform(requestBuilder).andExpect(status().isOk())
        .andExpect(jsonPath("$.clientId", equalTo("surong.com")));
  }

}
