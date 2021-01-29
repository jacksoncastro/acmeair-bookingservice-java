/*******************************************************************************
 * Copyright (c) 2018 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.acmeair.client;

import javax.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.time.temporal.ChronoUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@ApplicationScoped
@RegisterRestClient(configKey="customerClient")
@Path("/")
public interface CustomerClient {  

  @GET
  @Path("/internal/updateCustomerTotalMiles/{custid}/{miles}")
  @Produces("application/json")
  @Timeout(10000) // throws exception after 500 ms which invokes fallback handler
  @CircuitBreaker(requestVolumeThreshold=4,failureRatio=0.5,successThreshold=10,delay=1,delayUnit=ChronoUnit.SECONDS)
  @Retry(maxRetries=3,delayUnit=ChronoUnit.SECONDS,delay=5,durationUnit=ChronoUnit.SECONDS,
    maxDuration=30, retryOn = Exception.class, abortOn = IOException.class)
  @Fallback(LongFallbackHandler.class)
  public MilesResponse updateCustomerTotalMiles(
      @PathParam("custid") String customerid, 
      @PathParam("miles") Long miles);
}
