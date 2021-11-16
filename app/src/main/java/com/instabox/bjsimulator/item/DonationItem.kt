package com.instabox.bjsimulator.item

class DonationItem(ranking: String, user: String, coin: String) {

    var ranking: String
        internal set
    var user: String
        internal set
    var coin: String
        internal set

    init {
        this.ranking = ranking
        this.user = user
        this.coin = coin
    }

}