package com.wind.vpn.model

enum class ModelState {
    Loading, Success, Error;
    var message: String? = null
}