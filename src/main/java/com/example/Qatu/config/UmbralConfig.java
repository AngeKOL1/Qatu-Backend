package com.example.Qatu.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "qatu.heatmap")
public class UmbralConfig {
    private int umbralRojo     = 10;
    private int umbralAmarillo = 5;
    private int radioMetros    = 100;
}
