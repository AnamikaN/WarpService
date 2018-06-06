package com.anamika.app.web;

import com.anamika.app.service.HistogramConverter;
import com.anamika.app.utils.Histogram;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 *
 */
@RestController
public class HealthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Histogram histogram;
    private final HistogramConverter histogramConverter;

    @Autowired
    public HealthController(Histogram histogram, HistogramConverter histogramConverter) {
        this.histogram = histogram;
        this.histogramConverter = histogramConverter;
    }

    @ApiOperation(value = "Health Check", response = String.class)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Health Check Successful!!")
            }
    )
    @RequestMapping(path = "/health", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkHealth() {
        return new ResponseEntity<String>(histogramConverter.convert(histogram), HttpStatus.OK);
    }
}
