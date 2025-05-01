package com.example.tsin_androidproyect.models

data class TrafficCam(
    val traffic_cam_id: Int,
    val alias: String,
    val location_lat: Double,
    val location_lng: Double,
    val start_ref_line: RefLine,
    val finish_ref_line: RefLine,
    val ref_distance: Float,
    val track_orientation: String
)
