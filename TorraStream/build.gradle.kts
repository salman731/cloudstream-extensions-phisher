// use an integer for version numbers
version = 18


cloudstream {
    // All of these properties are optional, you can safely remove them

    description = "#1 best extention based on MultiAPI 4K Support (Torrent)"
    language    = "en"
    authors = listOf("Phisher98")

    /**
    * Status int as the following:
    * 0: Down
    * 1: Ok
    * 2: Slow
    * 3: Beta only
    * */
    status = 1 // will be 3 if unspecified

    // List of video source types. Users are able to filter for extensions in a given category.
    // You can find a list of available types here:
    // https://recloudstream.github.io/cloudstream/html/app/com.lagradost.cloudstream3/-tv-type/index.html
    tvTypes = listOf("Movie","Torrent","AsianDrama","TvSeries","Anime")

    iconUrl = "https://i.ibb.co/w4BnkC9/GwxAcDV.png"

    isCrossPlatform = false
}

dependencies {
    // FIXME remove this when crossplatform is fully supported
    val cloudstream by configurations
    cloudstream("com.lagradost:cloudstream3:pre-release")
}
