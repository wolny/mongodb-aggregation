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
        DefaultHttpClient httpClient = new DefaultHttpClient();
        ResponseHandler<Void> handler = new StringResponseHandler();

        SamplesGenerator generator = new SamplesGenerator(100.0, 50.0, "2013-10-28", "2013-10-29");
        double sum = 0.0;
        long count = 0;
        for (int i = 0; i < COUNT; i++) {
            long start = System.nanoTime();
            httpClient.execute(generator.genPostRequest(), handler);
            long dur = System.nanoTime() - start;
            sum += TimeUnit.MILLISECONDS.convert(dur, TimeUnit.NANOSECONDS);
            count++;
        }

        System.out.println("Avg. request processing time: " + (sum / count) + " ms");
    }

    private static final int COUNT = 10000;

    private static class StringResponseHandler implements ResponseHandler<Void> {
        @Override
        public Void handleResponse(HttpResponse response) throws IOException {
            //ByteStreams.copy(response.getEntity().getContent(), System.out);
            return null;
        }
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String SAMPLE_RESOURCE = "sampleResource";

    private final URI uri;
    private final double mean;
    private final double stdDev;
    private final long start;
    private final long end;
    private final ObjectMapper mapper;

    public URI getUri() {
        return uri;
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

    public SamplesGenerator(double mean, double stdDev, String start, String end) throws ParseException {
        uri = URI.create("http://localhost:8080/rest/collector");
        this.mean = mean;
        this.stdDev = stdDev;
        this.start = new SimpleDateFormat(DATE_FORMAT).parse(start).getTime();
        this.end = new SimpleDateFormat(DATE_FORMAT).parse(end).getTime();
        mapper = new ObjectMapper();
    }

    private HttpPost genPostRequest() throws IOException {
        HttpPost result = new HttpPost(getUri());
        String json = getMapper().writeValueAsString(
                new Sample(SAMPLE_RESOURCE,
                        getMean() + getStdDev() * ThreadLocalRandom.current().nextGaussian(),
                        genRandomDate()));
        StringEntity input = new StringEntity(json);
        input.setContentType("application/json");
        result.setEntity(input);
        return result;
    }

    private long genRandomDate() {
        return ThreadLocalRandom.current().nextLong(getStart(), getEnd());
    }
}
