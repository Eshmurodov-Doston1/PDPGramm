package com.programmalar.pdpgramm.models

class Message {
    var message:String?=null
    var date:String?=null
    var fromUid:String?=null
    var toUid:String?=null
    var key:String?=null

    constructor(
        message: String?,
        date: String?,
        fromUid: String?,
        toUid: String?,
        key:String
    ) {
        this.message = message
        this.date = date
        this.fromUid = fromUid
        this.toUid = toUid
        this.key = key
    }

    constructor()

}