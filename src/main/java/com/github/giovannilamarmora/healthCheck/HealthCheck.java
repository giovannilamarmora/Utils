package com.github.giovannilamarmora.healthCheck;

import org.springframework.stereotype.Service;

@Service
public class HealthCheck {

    public static String startHealthCheck(){

        return "STATUS_OK";
    }
}
