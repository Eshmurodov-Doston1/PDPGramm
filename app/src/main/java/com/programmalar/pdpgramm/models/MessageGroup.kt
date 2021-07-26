package com.programmalar.pdpgramm.models

class MessageGroup {
    var message:String?=null
    var toUid:String?=null
    var date:String?=null
    var key:String?= null
    var keyMessage:String?= null

    constructor(message: String?, toUid: String?, date: String?, key: String?,keyMessage:String) {
        this.message = message
        this.toUid = toUid
        this.date = date
        this.key = key
        this.keyMessage=keyMessage
    }

    constructor()

}