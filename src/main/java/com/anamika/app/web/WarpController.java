package com.anamika.app.web;

import com.anamika.app.error.MessageDTO;
import com.anamika.app.request.WarpRequest;
import com.anamika.app.service.WarpService;
import com.anamika.app.utils.Histogram;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 *
 */
@RestController
@Validated
@RequestMapping("/warp.service")
public class WarpController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final WarpService warpService;
    private final Histogram histogram;

    @Autowired
    public WarpController(WarpService warpService, Histogram histogram) {
        this.warpService = warpService;
        this.histogram = histogram;
    }

    @ApiOperation(value = "Schedule Warp", response = MessageDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully scheduled warp", response = MessageDTO.class),
            @ApiResponse(code = 400, message = "Bad Request!!", response = MessageDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error!!", response = MessageDTO.class)
    })
    @RequestMapping(method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody()
    public ResponseEntity<MessageDTO> scheduleWarp(@Valid @RequestBody WarpRequest request) {
        LOGGER.debug("scheduleWarp, request = {}", request);
        histogram.addSample(request.getWarpSpeed(), false);
        warpService.scheduleFleet(request.getFleetId(), request.getDuration(), request.getWarpSpeed());
        return new ResponseEntity<MessageDTO>(MessageDTO.builder().message("Success").build(), HttpStatus.OK);
    }
}
