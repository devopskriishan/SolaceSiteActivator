package com.anz.psp.solace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SEMPRestCallApplication {

	private static final Logger log = LoggerFactory.getLogger(SEMPRestCallApplication.class);
	
	@Value("${semp.redundancy.status.call.payload}")
	private String redundancyStatusPayload;
	
	@Value("${redundancy.status.start}")
	private String REDUNDANCY_STATUS;
	
	@Value("${redundancy.status.closing}")
	private String REDUNDANCY_STATUS_CLOSING;
	
	@Value("${solace.cli.username}")
	private String cliUserName;
	
	@Value("${solace.cli.password}")
	private String cliPassword;
	
	@Value("${application.semp.url}")
	private String sempURL;
	
	@Value("${semp.replication.role.update.json.payload}")
	private String replicationJsonPayload;
	
	@Value("${semp.replication.role.url}")
	private String replicationRoleURL;
	
	@Value("${semp.msgvpn.enable.json.payload}")
	private String vpnEnableJsonPayload;
	
	
	public void changeDMREnabled() {

		try {
			RestTemplate restTemplate = new RestTemplate();

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(1000);
			requestFactory.setReadTimeout(1000);

			restTemplate.setRequestFactory(requestFactory);

			String jsonInput2 = "{\"dmrEnabled\":false}";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBasicAuth("admin", "admin");

			HttpEntity<String> entity = new HttpEntity<String>(jsonInput2, headers);
			String response = restTemplate.patchForObject("http://localhost:8080/SEMP/v2/config/msgVpns/testVPN",
					entity, java.lang.String.class);
			System.out.println("###### Update DMREnabled status done !!");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public String getRedundancyStatus() {
		String redundancyStatus = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			log.info("Payload for redundancy status check ::: " + redundancyStatusPayload);
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			//requestFactory.setConnectTimeout(1000);
			//requestFactory.setReadTimeout(1000);
			restTemplate.setRequestFactory(requestFactory);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_XML);
			headers.setBasicAuth(cliUserName,cliPassword);

			HttpEntity<String> entity = new HttpEntity<String>(redundancyStatusPayload, headers);

			ResponseEntity<String> response = restTemplate.postForEntity(sempURL, entity,
					java.lang.String.class);
			String responseBody = response.getBody().trim();
			redundancyStatus = responseBody.substring(
					responseBody.indexOf(REDUNDANCY_STATUS) + REDUNDANCY_STATUS.length(),
					responseBody.indexOf(REDUNDANCY_STATUS_CLOSING));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return redundancyStatus;

	}
	
	public void changeReplicationRole() {
			try {
				RestTemplate restTemplate = new RestTemplate();

				HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
				requestFactory.setConnectTimeout(1000);
				requestFactory.setReadTimeout(1000);

				restTemplate.setRequestFactory(requestFactory);
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.setBasicAuth(cliUserName, cliPassword);

				HttpEntity<String> entity = new HttpEntity<String>(replicationJsonPayload, headers);
				String response = restTemplate.patchForObject(replicationRoleURL, entity, java.lang.String.class);
				log.info("####### Redundancy Status Response ::: " + response);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	}
	
	public void enableVPN() {
		try {
			RestTemplate restTemplate = new RestTemplate();

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(1000);
			requestFactory.setReadTimeout(1000);

			restTemplate.setRequestFactory(requestFactory);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBasicAuth(cliUserName, cliPassword);

			HttpEntity<String> entity = new HttpEntity<String>(vpnEnableJsonPayload, headers);
			String response = restTemplate.patchForObject(replicationRoleURL, entity, java.lang.String.class);
			log.info("####### Msg VPN Enabled Status Response ::: " + response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
}

}
