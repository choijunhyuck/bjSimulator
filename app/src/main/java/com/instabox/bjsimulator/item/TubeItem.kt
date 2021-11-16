package com.instabox.bjsimulator.item

class TubeItem(screen: String, title: String, time: Int, play: Int, date: String) {

    var screen: String
        internal set
    var title: String
        internal set
    var time: Int
        internal set
    var play: Int
        internal set
    var date: String
        internal set

    init {
        this.screen = screen
        this.title = title
        this.time = time
        this.play = play
        this.date = date
    }

}