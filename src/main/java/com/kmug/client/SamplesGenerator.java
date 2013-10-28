package com.kmug.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author Adrian Wolny
 */
public class SamplesGenerator {
    public static void main(String[] args) throws IOException, ParseException {
        // resource to be monitored
        String resource = "xxx666";
        // number of samples to be send to collector
        int numberOfSamples = 500000;
        // start date
        String start = "2013-10-20";
        // end date
        String end = "2013-10-29";
        // mean value of the Gaussian distribution
        double mean = 100.0;
        // standard deviation of the Gaussian distribution
        double stdDev = 50.0;


        DefaultHttpClient httpClient = new DefaultHttpClient();
        ResponseHandler<Void> handler = new StringResponseHandler();
        SamplesGenerator generator = new SamplesGenerator(resource, start, end, mean, stdDev);
        double sum = 0.0;
        long count = 0;
        for (int i = 0; i < numberOfSamples; i++) {
            long startTime = System.nanoTime();
            httpClient.execute(generator.genPostRequest(), handler);
            long dur = System.nanoTime() - startTime;
            sum += TimeUnit.MILLISECONDS.convert(dur, TimeUnit.NANOSECONDS);
            count++;
        }

        System.out.println("Avg. request processing time: " + (sum / count) + " ms");
    }

    public static final String CONTENT_TYPE = "application/json";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private static class StringResponseHandler implements ResponseHandler<Void> {
        @Override
        public Void handleResponse(HttpResponse response) throws IOException {
            //ByteStreams.copy(response.getEntity().getContent(), System.out);
            return null;
        }
    }


    private final URI uri;
    private final String resource;
    private final double mean;
    private final double stdDev;
    private final long start;
    private final long end;
    private final ObjectMapper mapper;

    public URI getUri() {
        return uri;
    }

    public String getResource() {
        return resource;
    }

    public double getMean() {
        return mean;
    }

    public double getStdDev() {
        return stdDev;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Generates samples with random date within time range specified by <tt>start</tt> and <tt>end</tt>.
     * Samples are generated with Gaussian distribution with mean 0.0 and standard deviation 1.0.
     *
     * @param resource name of the resource to be analyzed
     * @param start    lower limit of the time range
     * @param end      upper limit of the time range
     * @throws ParseException if <tt>start</tt> or <tt>end</tt> does not comply with <em>yyyy-MM-dd</em> date format
     */
    public SamplesGenerator(String resource, String start, String end) throws ParseException {
        this(resource, start, end, 0.0, 1.0);
    }

    /**
     * Generates samples with random date within time range specified by <tt>start</tt> and <tt>end</tt>.
     * Samples are generated with Gaussian distribution with mean <tt>mean</tt> and standard deviation <tt>stdDev</tt>.
     *
     * @param resource name of the resource to be analyzed
     * @param start    lower limit of the time range
     * @param end      upper limit of the time range
     * @param mean     mean value of Gaussian function
     * @param stdDev   standard deviation of Gaussian function
     * @throws ParseException if <tt>start</tt> or <tt>end</tt> does not comply with <em>yyyy-MM-dd</em> date format
     */
    public SamplesGenerator(String resource, String start, String end, double mean, double stdDev) throws ParseException {
        uri = URI.create("http://localhost:8080/rest/collector");
        this.resource = resource;
        this.mean = mean;
        this.stdDev = stdDev;
        this.start = new SimpleDateFormat(DATE_FORMAT).parse(start).getTime();
        this.end = new SimpleDateFormat(DATE_FORMAT).parse(end).getTime();
        mapper = new ObjectMapper();
    }

    private HttpPost genPostRequest() throws IOException {
        HttpPost result = new HttpPost(getUri());
        String json = getMapper().writeValueAsString(
                new Sample(getResource(),
                        getMean() + getStdDev() * ThreadLocalRandom.current().nextGaussian(),
                        genRandomDate()));
        StringEntity input = new StringEntity(json);
        input.setContentType(CONTENT_TYPE);
        result.setEntity(input);
        return result;
    }

    private long genRandomDate() {
        return ThreadLocalRandom.current().nextLong(getStart(), getEnd());
    }
}
