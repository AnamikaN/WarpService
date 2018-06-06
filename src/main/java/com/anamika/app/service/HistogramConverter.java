package com.anamika.app.service;

import com.anamika.app.utils.Histogram;
import com.anamika.app.utils.HistogramJsonOutput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;


@Service
public class HistogramConverter implements Converter<Histogram, String> {
    private final ObjectMapper objectMapper;

    @Autowired
    public HistogramConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String convert(Histogram histogram) {
        HistogramJsonOutput histogramJsonOutput = histogram.toJson();
        try {
            return objectMapper.writeValueAsString(histogramJsonOutput);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}

