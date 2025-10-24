package com.github.gomestkd.eventmanagement.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InstanceInformationService {

    private static final Logger logger = LoggerFactory.getLogger(InstanceInformationService.class);

    private static final String HOST_NAME = "HOSTNAME";
    private static final String DEFAULT_ENV_INSTANCE_GUID = "LOCAL";

    @Value("${" + HOST_NAME + ":" + DEFAULT_ENV_INSTANCE_GUID + "}")
    private String hostName;

    public String retrieveInstanceInfo() {
        logger.info("Retrieving instance information from hostName: {}", hostName);

        if (hostName == null || hostName.isEmpty()) {
            logger.warn("Host name is null or empty. Returning default instance identifier: '{}'", DEFAULT_ENV_INSTANCE_GUID);
            return DEFAULT_ENV_INSTANCE_GUID;
        }

        if (hostName.length() < 3) {
            logger.warn("Host name '{}' is shorter than 3 characters. Returning full value.", hostName);
            return hostName;
        }

        String instanceSuffix = hostName.substring(hostName.length() - 3);
        logger.info("Instance information retrieved successfully: {}", instanceSuffix);

        return instanceSuffix;
    }
}
