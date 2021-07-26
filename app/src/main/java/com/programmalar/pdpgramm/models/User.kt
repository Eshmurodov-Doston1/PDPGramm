package com.programmalar.pdpgramm.models

import java.io.Serializable

class User:Serializable {
    var email:String?=null
    var displayName:String?=null
    var photoUrl:String?=null
    var uid:String?=null
    var colorMessage:String?=null

    constructor(email: String?, displayName: String?, photoUrl: String?, uid: String?,colorMessage:String) {
        this.email = email
        this.displayName = displayName
        this.photoUrl = photoUrl
        this.uid = uid
        this.colorMessage = colorMessage
    }
    constructor()
}