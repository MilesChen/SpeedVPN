package com.wind.vpn.data.bean

class InviteResp {
    var codes: List<InviteCode> = emptyList()
    var stat: List<Int> = emptyList()

    fun getValidCode():String?{
        if (!codes.isNullOrEmpty()){
            for (code in codes) {
                if (code.status == 0 && !code.code.isNullOrEmpty()) {
                    return code.code
                }
            }
        }
        return null
    }

    fun hasValidCode():Boolean{
        return getValidCode()!= null
    }

    fun getRecommendPeople():String {
        if (stat.isNullOrEmpty()) {
            return "0"
        }
        return "${stat[0]}"
    }
}