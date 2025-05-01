package com.example.tsin_androidproyect.network

import com.example.tsin_androidproyect.models.CamRequest
import com.example.tsin_androidproyect.models.TrafficCam
import retrofit2.http.*

interface ApiService {
    @GET("cams")
    suspend fun getCams(): List<TrafficCam>

    @POST("cams")
    suspend fun addCam(@Body request: CamRequest): TrafficCam

    @PUT("cams/{id}")
    suspend fun updateCam(
        @Path("id") id: Int,
        @Body request: CamRequest
    ): TrafficCam
}
