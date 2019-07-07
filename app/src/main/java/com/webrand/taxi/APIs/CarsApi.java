package com.webrand.taxi.APIs;

import com.webrand.taxi.models.CarsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CarsApi {
    @GET("nearest/{latitude}/{lng}")
    Call<CarsResponse> getCars(@Path("latitude") double lat, @Path("lng") double lng);
}
