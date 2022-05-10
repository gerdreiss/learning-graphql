package com.github.gerdreiss.dsgdemo

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import java.time.LocalDate
import java.time.Month
import java.util.*

@DgsComponent
class ShowsDataFetcher {
    private val mbb = Actor("Millie Bobby Brown", LocalDate.of(2004, Month.FEBRUARY, 19))
    private val wr = Actor("Winona Ryder", LocalDate.of(1971, Month.OCTOBER, 29))
    private val jb = Actor("Jason Bateman", LocalDate.of(1969, Month.JANUARY, 14))
    private val sf = Actor("Sofia Hublitz", LocalDate.of(2000, Month.JUNE, 1))
    private val oc = Actor("Olivia Colman", LocalDate.of(1974, Month.JANUARY, 30))
    private val `is` = Actor("Imelda Staunton", LocalDate.of(1956, Month.JANUARY, 9))
    private val ca = Actor("Christina Applegate", LocalDate.of(1971, Month.NOVEMBER, 25))
    private val jm = Actor("James Marsden", LocalDate.of(1973, Month.SEPTEMBER, 18))
    private val nl = Actor("Natasha Lyonne", LocalDate.of(1979, Month.APRIL, 4))

    private val shows = listOf(
        Show(UUID.randomUUID(), "Stranger Things", 2016, listOf(mbb, wr)),
        Show(UUID.randomUUID(), "Ozark", 2017, listOf(jb, sf)),
        Show(UUID.randomUUID(), "The Crown", 2016, listOf(oc, `is`)),
        Show(UUID.randomUUID(), "Dead to Me", 2019, listOf(ca, jm)),
        Show(UUID.randomUUID(), "Orange is the New Black", 2013, listOf(nl))
    )

    private fun String.toUUID() = UUID.fromString(this)

    // example query
    //{
    //    shows(titleFilter: "The") {
    //        title
    //        releaseYear
    //        cast {
    //            name
    //            born
    //        }
    //    }
    //}
    // @DgsData(parentType = "Query", field = "shows")
    // can be simplified to
    @DgsQuery(field = "shows")
    fun getShows(@InputArgument titleFilter: String?): List<Show> {
        return if (titleFilter != null) {
            shows.filter { it.title.contains(titleFilter) }
        } else {
            shows
        }
    }

    @DgsQuery(field = "show")
    fun getShow(@InputArgument id: String): Show? {
        return shows.find { it.id == id.toUUID() }
    }

    @DgsData(parentType = "Show", field = "cast")
    fun getCast(env: DgsDataFetchingEnvironment): List<Actor> {
        return env.getSource<Show>().cast
    }

    data class Show(val id: UUID, val title: String, val releaseYear: Int, val cast: List<Actor>)
    data class Actor(val name: String, val born: LocalDate)
}