package me.alexkovrigin.splitthebill

import android.Manifest

const val PREF_SESSION_ID = "pref_sessionId"
const val PREF_REFRESH_TOKEN = "pref_refresh_token"

val CAMERA_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
const val CAMERA_REQUEST_CODE = 10

const val EPS = 1e-7