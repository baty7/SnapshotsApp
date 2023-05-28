package com.example.snapshotsapp

import com.google.firebase.database.Exclude

data class Snapshots(
    @get:Exclude var id: String = "",
    var ownerUid: String = "",
    var titulo: String = "",
    var fotoUrl: String = ""
)
