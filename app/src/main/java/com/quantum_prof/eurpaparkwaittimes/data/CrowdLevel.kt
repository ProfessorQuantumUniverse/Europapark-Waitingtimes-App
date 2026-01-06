package com.quantum_prof.eurpaparkwaittimes.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CrowdLevel(
    @SerialName("crowd_level")
    val crowdLevel: Double,
    val timestamp: String
)

