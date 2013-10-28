package com.kmug.controller;

import com.google.common.base.Throwables;
import com.kmug.aggregate.ComputedNumericalAggregate;
import com.kmug.aggregate.TimeSpan;
import com.kmug.client.Sample;
import com.kmug.service.AggregatesService;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Adrian Wolny
 */
@Controller
@RequestMapping("rest")
public class AggregatesController {
    public static final String DATE_FORMAT = "yyyy-MM-dd_HH:mm:ss";

    private final AggregatesService aggregatesService;
    private final ObjectMapper mapper;

    @Autowired
    public AggregatesController(AggregatesService aggregatesService) {
        this.aggregatesService = aggregatesService;
        this.mapper = new ObjectMapper();
    }

    public AggregatesService getAggregatesService() {
        return aggregatesService;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    @RequestMapping("{resource}/{start}/{end}")
    @ResponseBody
    public List<ComputedNumericalAggregate> getNumericalSeries(@PathVariable String resource, @PathVariable String start, @PathVariable String end) {
        return getAggregatesService().getNumericalSeries(resource, parseDate(start), parseDate(end));
    }

    @RequestMapping(value = "collector", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public void collectNumerical(@RequestBody String sampleStr) {
        try {
            Sample sample = getMapper().readValue(sampleStr, Sample.class);
            getAggregatesService().collectNumerical(sample.getResource(), sample.getValue(), new Date(sample.getTimestamp()));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            // get date with up to a minute accuracy
            return TimeSpan.MINUTELY.getPeriodStart(sdf.parse(dateString));
        } catch (ParseException e) {
            throw Throwables.propagate(e);
        }
    }
}
