package com.programmalar.pdpgramm.models

import java.io.Serializable

class ViewPager:Serializable {
    var image:Int?=null
    var text:String?=null
    var position:Int?= null

    constructor(image: Int?, text: String?,position:Int) {
        this.image = image
        this.text = text
        this.position= position
    }
}