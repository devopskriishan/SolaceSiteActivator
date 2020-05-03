/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.anz.psp.solace;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.XMLMessageListener;


public class SiteActivatorMessageConsumer implements XMLMessageListener  {

	private SEMPRestCallApplication sempRestCallApplication;

	private String applicationMode;
	
	private String redundancyStatusCheckResponse;



	public void setApplicationMode(String applicationMode) {
		this.applicationMode = applicationMode;
	}

	public void setSEMPRestCallApplication(SEMPRestCallApplication sempRestCallApplication) {
		this.sempRestCallApplication = sempRestCallApplication;
	}

	private CountDownLatch latch = new CountDownLatch(1);
	private static final Logger logger = LoggerFactory.getLogger(SiteActivatorMessageConsumer.class);

	public void onReceive(BytesXMLMessage msg) {
		if (msg instanceof TextMessage) {
			logger.info("============= TextMessage received: " + ((TextMessage) msg).getText());
		} else {
			logger.info("============= Message received.");
		}
		latch.countDown(); // unblock main thread
	}

	public void onException(JCSMPException e) {

		logger.info("####### Message consumer caught exception at :::: " , getCurrentTimeStamp() , ", exception details :: ", e);

		logger.info("####### Checking the value of current application mode :: ", applicationMode);

		if(applicationMode!= null && applicationMode.equalsIgnoreCase("normal")){
			logger.info("####### Application is running in NORMAL mode, site activator to check for redundancy status before enabling cross site VPN");	
			redundancyStatusCheckResponse = sempRestCallApplication.getRedundancyStatus();
			if(redundancyStatusCheckResponse != null && redundancyStatusCheckResponse.equalsIgnoreCase("Down")) {
				logger.info("######## Redundancy status is Down, changing the replication role of VPN on cross site to UP ########");

				sempRestCallApplication.enableVPN();

				logger.info("######## Cross site VPN enabled ########");
			}
		}else {
			logger.info("####### Application is running in Maintenance mode, Site activator will not take any action ########");
		}
	}


	public CountDownLatch getLatch() {
		return latch;
	}

/*	@Bean
	public void changeDMREnabled() {

		try {
			RestTemplate restTemplate = new RestTemplate();

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			restTemplate.setRequestFactory(requestFactory);

			String jsonInput2 = "{\"dmrEnabled\":false}";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBasicAuth("admin", "admin");

			HttpEntity<String> entity = new HttpEntity<String>(jsonInput2, headers);
			String response = restTemplate.patchForObject("http://localhost:8080/SEMP/v2/config/msgVpns/testVPN", entity, java.lang.String.class);

			logger.info("#### Update DMREnabled status response ==== " + response);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@Bean
	public void getRedundancyStatus() {

		try {
			RestTemplate restTemplate = new RestTemplate();

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			//requestFactory.setConnectTimeout(1000);
			//requestFactory.setReadTimeout(1000);

			restTemplate.setRequestFactory(requestFactory);
			String xmlInput = "<rpc semp-version=\"soltr/9_4VMR\"><show><redundancy><detail></detail></redundancy></show></rpc>";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_XML);
			headers.setBasicAuth("admin", "admin");

			HttpEntity<String> entity = new HttpEntity<String>(xmlInput, headers);

			ResponseEntity<String> reponse = restTemplate.postForEntity("http://localhost:8080/SEMP", entity,
					java.lang.String.class);
			System.out.println(reponse.getBody());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	public String getCurrentTimeStamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
	}
}


