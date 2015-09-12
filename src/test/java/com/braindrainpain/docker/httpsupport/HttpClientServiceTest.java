package com.braindrainpain.docker.httpsupport;

import com.thoughtworks.go.plugin.api.logging.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.MockitoLogger;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class HttpClientServiceTest {

    @Mock
    private HttpClient httpClient;

    @Spy
    private GetMethod getMethod;

    @InjectMocks
    private HttpClientService httpClientService;

    public static final String URL = "http://www.google.de";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        httpClientService = new HttpClientService(httpClient, getMethod);
    }

    @Test
    public void testDoGetWithEmptyUrlShouldFail() {
        try {
            httpClientService.doGet("");
            fail("IOException expected if url is empty");
        } catch (IOException e) {
            Assert.assertEquals("cannot connect to url ", e.getMessage());
        }
    }

    @Test
    public void testDoGetIsAnswering() throws Exception {
        when(httpClient.executeMethod(any(HttpMethod.class))).thenReturn(200);
        String answer = "spy answered";
        when(getMethod.getResponseBodyAsString()).thenReturn(answer);
        Assert.assertEquals(answer, httpClientService.doGet(URL));
    }

    @Test
    public void testDoGetThrowsExceptionOnStatus404() {
        String answer = "spy answered";
        try {
            when(httpClient.executeMethod(any(HttpMethod.class))).thenReturn(404);
            when(getMethod.getResponseBodyAsString()).thenReturn(answer);
            httpClientService.doGet(URL);
            fail("IOException expected if status is 404");
        } catch (IOException e) {
            Assert.assertEquals("cannot connect to url "+URL, e.getMessage());
        }
    }

    @Test
    public void testCheckConnection() throws IOException {
        when(httpClient.executeMethod(any(HttpMethod.class))).thenReturn(200);
        httpClientService.checkConnection(URL);
    }

    @Test
    public void testCheckConnectionThrowsExceptionOnStatus404() throws IOException {
        when(httpClient.executeMethod(any(HttpMethod.class))).thenReturn(404);
        try {
            httpClientService.checkConnection(URL);
            fail("RuntimeException expected if status is 404");
        } catch (RuntimeException e) {
            Assert.assertEquals("Not ok from: '"+URL+"'", e.getMessage());
        }
    }

    @Test
    public void testCheckConnectionThrowsIOExceptionOnExecuteMethodShouldThrowRuntimeException() throws IOException {
        when(httpClient.executeMethod(any(HttpMethod.class))).thenThrow(new IOException());
        try {
            httpClientService.checkConnection(URL);
            fail();
        } catch (RuntimeException e) {
            Assert.assertEquals("Error connecting to: '"+URL+"'", e.getMessage());
        }
    }

}



