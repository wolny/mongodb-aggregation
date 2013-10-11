package com.kmug.controller;

import com.google.common.base.Throwables;
import com.kmug.aggregate.ComputedNumericalAggregate;
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
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public AggregatesController(AggregatesService aggregatesService) {
        this.aggregatesService = aggregatesService;
    }

    public AggregatesService getAggregatesService() {
        return aggregatesService;
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
            Sample sample = mapper.readValue(sampleStr, Sample.class);
            getAggregatesService().collectNumerical(sample.getResource(), sample.getValue(), new Date(sample.getTimestamp()));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            return getMinute(sdf.parse(dateString));
        } catch (ParseException e) {
            throw Throwables.propagate(e);
        }
    }

    // get date with up to a minute accuracy
    private Date getMinute(Date date) {
        DateTime dt = new DateTime(date);
        return new DateTime(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), dt.getHourOfDay(), dt.getMinuteOfHour(), 0, 0).toDate();
    }
}
