package com.phisher98

import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.amap
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.mainPageOf
import com.lagradost.cloudstream3.utils.AppUtils.tryParseJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import org.jsoup.Jsoup
import java.util.Calendar

open class Hdmovie2 : Movierulzhd() {

    override var mainUrl = "https://hdmovie2.navy"
    override var name = "Hdmovie2"
    override val mainPage = mainPageOf(
        "trending" to "Trending",
        "release/${Calendar.getInstance().get(Calendar.YEAR)}" to "Latest",
        "movies" to "Movies",
        "genre/hindi-webseries" to "Hindi Web Series",
        "genre/netflix" to "Netflix",
        "genre/zee5" to "Zee5",
        "genre/hindi-dubbed" to "Hindi Dubbed",
        "genre/comedy" to "Comedy",
        "genre/science-fiction" to "Science Fiction"
    )

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        if (data.startsWith("{")) {
            val loadData = tryParseJson<LinkData>(data)
            val source = app.post(
                url = "$directUrl/wp-admin/admin-ajax.php", data = mapOf(
                    "action" to "doo_player_ajax", "post" to "${loadData?.post}", "nume" to "${loadData?.nume}", "type" to "${loadData?.type}"
                ), referer = data, headers = mapOf("Accept" to "*/*", "X-Requested-With" to "XMLHttpRequest"
                )).parsed<ResponseHash>().embed_url.getIframe()
            if (!source.contains("youtube")) loadExtractor(
                source,
                "$directUrl/",
                subtitleCallback,
                callback
            )
        } else {
            val document = app.get(data).document
            val id = document.select("ul#playeroptionsul > li").attr("data-post")
            val type = if (data.contains("/movies/")) "movie" else "tv"
            document.select("ul#playeroptionsul > li").map {
                it.attr("data-nume")
            }.amap { nume ->
                val source = app.post(
                    url = "$directUrl/wp-admin/admin-ajax.php", data = mapOf(
                        "action" to "doo_player_ajax", "post" to id, "nume" to nume, "type" to type
                    ), referer = data, headers = mapOf("Accept" to "*/*", "X-Requested-With" to "XMLHttpRequest")
                ).parsed<ResponseHash>().embed_url.getIframe()
                if(source.contains("ok.ru"))
                {
                    loadExtractor(
                        "https:$source",
                        "$directUrl/",
                        subtitleCallback,
                        callback
                    )
                }
                else
                when {
                    !source.contains("youtube") -> loadExtractor(
                        source,
                        "$directUrl/",
                        subtitleCallback,
                        callback
                    )
                    else -> ""
                }
            }
        }
        return true
    }

    private fun String.getIframe(): String {
        return Jsoup.parse(this).select("iframe").attr("src")
    }

    data class LinkData(
        val type: String? = null,
        val post: String? = null,
        val nume: String? = null,
    )

    data class ResponseHash(
        @JsonProperty("embed_url") val embed_url: String,
        @JsonProperty("type") val type: String?,
    )

}
