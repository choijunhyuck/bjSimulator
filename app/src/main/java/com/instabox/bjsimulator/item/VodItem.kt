package com.instabox.bjsimulator.item

class VodItem(screen: String, title: String, time: Int, play: Int, recommand: Int, date: String) {

    var screen: String
        internal set
    var title: String
        internal set
    var time: Int
        internal set
    var play: Int
        internal set
    var recommand: Int
        internal set
    var date: String
        internal set

    init {
        this.screen = screen
        this.title = title
        this.time = time
        this.play = play
        this.recommand = recommand
        this.date = date
    }

}