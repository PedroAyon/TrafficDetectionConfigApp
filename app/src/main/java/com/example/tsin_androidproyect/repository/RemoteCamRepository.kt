package com.example.tsin_androidproyect.repository

import com.example.tsin_androidproyect.models.CamRequest
import com.example.tsin_androidproyect.models.RemoteTrafficCam
import com.example.tsin_androidproyect.network.RetrofitClient


class RemoteCamRepository {
    private val api = RetrofitClient.apiService

    suspend fun fetchAllCams(): List<RemoteTrafficCam> =
        api.getCams()

    suspend fun createCam(request: CamRequest): RemoteTrafficCam =
        api.addCam(request)

    suspend fun editCam(id: Int, request: CamRequest): RemoteTrafficCam =
        api.updateCam(id, request)
}
