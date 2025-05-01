package com.example.tsin_androidproyect.repository

import com.example.tsin_androidproyect.models.CamRequest
import com.example.tsin_androidproyect.models.TrafficCam
import com.example.tsin_androidproyect.network.RetrofitClient


class CamRepository {
    private val api = RetrofitClient.apiService

    suspend fun fetchAllCams(): List<TrafficCam> =
        api.getCams()

    suspend fun createCam(request: CamRequest): TrafficCam =
        api.addCam(request)

    suspend fun editCam(id: Int, request: CamRequest): TrafficCam =
        api.updateCam(id, request)
}
