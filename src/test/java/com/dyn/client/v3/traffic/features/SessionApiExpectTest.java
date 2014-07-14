/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dyn.client.v3.traffic.features;
import static com.dyn.client.common.DynClientVersion.VERSION;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.dyn.client.v3.traffic.DynTrafficApi;
import com.dyn.client.v3.traffic.domain.SessionCredentials;
import com.dyn.client.v3.traffic.internal.BaseDynTrafficApiExpectTest;
import com.dyn.client.v3.traffic.parse.CreateSessionResponseTest;
/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SessionApiExpectTest")
public class SessionApiExpectTest extends BaseDynTrafficApiExpectTest {

   public void testCreateWhenResponseIs2xx() {
      DynTrafficApi apiCreatesSession = requestSendsResponse(createSession, createSessionResponse);
      assertEquals(apiCreatesSession.getSessionApi().login(SessionCredentials.builder()
                                                                         .customerName("jclouds")
                                                                         .userName("joe")
                                                                         .password("letmein").build()).toString(),
                   new CreateSessionResponseTest().expected().toString());
   }

   HttpRequest isValid = HttpRequest.builder().method(GET)
                                    .endpoint("https://api2.dynect.net/REST/Session")
                                    .addHeader("API-Version", "3.3.8")
                                    .addHeader(USER_AGENT, VERSION)
                                    .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                                    .addHeader("Auth-Token", authToken).build();

   HttpResponse validResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/session_valid.json", APPLICATION_JSON)).build();

   HttpResponse invalidResponse = HttpResponse.builder().statusCode(400)
         .payload(payloadFromResourceWithContentType("/session_invalid.json", APPLICATION_JSON)).build();

   public void testSessionValid() {
      DynTrafficApi apiWhenValid = requestSendsResponse(isValid, validResponse);
      assertTrue(apiWhenValid.getSessionApi().isValid(authToken));
   }

   public void testSessionInvalid() {
      DynTrafficApi apiWhenInvalid = requestSendsResponse(isValid, invalidResponse);
      assertFalse(apiWhenInvalid.getSessionApi().isValid(authToken));
   }

   HttpRequest logout = HttpRequest.builder().method(DELETE)
                                   .endpoint("https://api2.dynect.net/REST/Session")
                                   .addHeader("API-Version", "3.3.8")
                                   .addHeader(USER_AGENT, VERSION)
                                   .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                                   .addHeader("Auth-Token", authToken).build();

   HttpResponse logoutResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/logout.json", APPLICATION_JSON)).build();

   public void testLogout() {
      DynTrafficApi apiWhenLogoutSuccess = requestSendsResponse(logout, logoutResponse);
      apiWhenLogoutSuccess.getSessionApi().logout(authToken);
   }
}
