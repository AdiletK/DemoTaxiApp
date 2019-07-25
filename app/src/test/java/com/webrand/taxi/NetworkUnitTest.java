package com.webrand.taxi;

import com.webrand.taxi.helpers.NetworkService;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class NetworkUnitTest {

    @Test
    public void testNetwork() throws IOException {
        double lat = 42.882004;
        double lon = 74.582748;
       boolean result = NetworkService.getInstance()
                .getJSONApi()
                .getCars(lat,lon)
                .execute()
                .isSuccessful();
       assertTrue(result);
    }
}