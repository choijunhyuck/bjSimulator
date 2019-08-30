package com.instabox.bjsimulator.item

class ChatItem(user: String, sentence: String) {

    var user: String
        internal set
    var sentence: String
        internal set

    init {
        this.user = user
        this.sentence = sentence
    }

}