package furhatos.app.spacereceptionist.flow

import furhatos.nlu.common.*
import furhatos.flow.kotlin.*
import furhatos.app.spacereceptionist.nlu.*
import furhatos.nlu.common.Number
import kotlin.math.floor
import furhatos.flow.kotlin.onResponse as onResponse

enum class RoomClass(val text: String) {
    Suite("suite"), Citizen("citizen")
}

val availablePlaces = 40
var requestNum : Int? = null
var customerName : PersonName? = null
var stayDuration : Duration? = null
var roomType : RoomClass? = null
var corrected_class: String? = null
var roomCounts = mapOf(
    RoomClass.Suite to 10,
    RoomClass.Citizen to 20
)
var wishes: List<String> = listOf()
//var suiteRoomCount = 10
//var citizenRoomCount = 20

val Start: State = state(Interaction) {

    onEntry {
        furhat.ask("Hello, how can I help you?")
//        TODO: make custom intents
    }

    onResponse<Yes> {
        furhat.say("I like humans.")
    }

    onResponse<No> {
        furhat.say("That's sad.")
    }
    onResponse<CheckIn> {
        goto(CheckInIntro)
    }

    onResponse<Confused> {
        goto(RobotIntro)
    }
}

val RobotIntro = state(Interaction) {
    onEntry {
        furhat.say("""
            Welcome to Starship
            Enterprise. We are currently
            leaving for a 12-day voyage from
            planet Earth to planet Vulkan.
            My name is Data and I am your check-in assistant for today.
        """.trimIndent())

        furhat.ask("Would you like to check in?")
    }

    onResponse<Yes> { goto(CheckInIntro) }

    onResponse<CheckIn> { goto(CheckInIntro) }
}

val CheckInIntro: State = state(Start) {
    onEntry {
        furhat.ask("""
                    Great! As the travel is longer than two days on our journey to Vulkan,
                    regulation requires we ask a few questions. Is that okay with you?
                    """.trimIndent())
    }

    onResponse<Yes> {
        goto(HowManyGuests)
    }

    onResponse<No> {
        goto(UserDeniesInfo)
    }
}

val HowManyGuests: State = state(Interaction) {
    onEntry {
        furhat.ask(
                """
                Let's get started then. How many people would you like to check-in?
                """.trimIndent()
        )
    }

    onResponse<NumberOfPeople> {
        requestNum = it.intent.numPeople
        goto(RandomQuestion1)
    }
}

val RandomQuestion1: State = state(Interaction) {
    onEntry {
        furhat.say(
            """
            Great. By the way, would you like to know about
            the available amenities in our rooms?
            """.trimIndent()
        )
    }

    onResponse<Yes> {
        furhat.say(
            """
            You are provided a bed, a table, a chair, and a Replicator,
            which allows you to instantly create any dish you've ever
            wanted to eat, in the comfort of your own room.
            """.trimIndent()
        )

        goto(FurtherDetails)
    }

    onResponse<No> {
        goto(FurtherDetails)
    }
}

val FurtherDetails: State = state(Interaction) {
    onEntry {
        furhat.say(
            """
            Perfect. Now, could you give me your name, how long you intend
            to stay on Starship Enterprise, and whether you would like to stay
            in our Suite-class rooms or the Citizen-class rooms? (suite class
            rooms have 2 beds, citizen-class have 1 bed).
            """.trimIndent()
        )
    }

    onResponse<Details> {
        if (it.intent.name == null || it.intent.days == null || it.intent.roomChoice == null) {
            furhat.say(
                """
            Sorry, I didn't understand one or more of your answers, can you repeat them?
            """.trimIndent()
            )
        } else {
            customerName = it.intent.name
            stayDuration = it.intent.days

            roomType = if (it.intent.roomChoice!!.equals("suite")) RoomClass.Suite else RoomClass.Citizen
            val roomsNeeded = if (roomType!! == RoomClass.Citizen) requestNum else (floor(requestNum!! / 2f))

            if (roomCounts[roomType!!]!! < requestNum!!) {
                goto(StarshipOverloaded)
            } else {
                goto(SpecificWishes)
            }
        }
    }
}

val FurtherDetails1: State = state(Interaction) {
    onEntry {

    }
}

val SpecificWishes: State = state(Interaction) {
    onEntry {
        furhat.say(
            """
            Amazing. The data has been entered to your name, $customerName.
            Now, before asking you about the different activities we offer
            on board, I would like to ask you if you have any specific wishes
            for your stay here.
            """.trimIndent()
        )
    }

    onResponse<Yes>() {
        furhat.say("Ok, please tell me your wish.")
        goto(WishesLoop)
    }

    onResponse<No> {
        goto(ThereAreNoWishes)
    }
}

val WishesLoop: State = state(Interaction) {
    onEntry {
        furhat.say("Understood. Anything else?") // add variations
    }
}

val ThereAreNoWishes: State = state(Interaction) {
    onEntry {
        furhat.say("Alright, then let's move on.")
        goto(StarshipActivities)
    }
}

val StarshipOverloaded: State = state(Interaction) {
    onEntry {
        furhat.say(
            """
            Unfortunately there are no rooms left of this kind. We only
            have ${roomCounts[roomType]} of this kind free. Would you like to change
            the number of people you are checking in?
            """.trimIndent()
        )
    }

    onResponse<Yes> {
        goto(NumberOfPeopleChange)
    }

    onResponse<No> {
        goto(CheckInCancel)
    }

    onTime(10000) {
        goto(CheckInCancel)
    }
}

val CheckInCancel: State = state(Interaction) {
    onEntry {
        furhat.say(
            """
            Alright then, please tell me if you'd like to
            start over. Otherwise, I wish you a good day
            """.trimMargin())
    }

    onTime(5000) {
        goto(Idle)
    }
}

val NumberOfPeopleChange: State = state(Interaction) {
    onEntry {
        furhat.say("Wonderful. Please tell me how many guests you would like me to check in.")
    }

    onResponse<Number>() {
        requestNum = it.intent.value

        if (roomCounts[roomType!!]!! < requestNum!!) {
            goto(StarshipOverloaded)
        } else {
            goto(SpecificWishes)
        }
    }
}

val StarshipActivities: State = state(Interaction) {

}

val UserAsksForTooManyPlaces: State = state(Interaction) {
    onEntry {
        furhat.say(
            """
            Unfortunately we only have $availablePlaces places available.
            """.trimIndent()
        )

        furhat.ask(
            """
            Would you like to change the number of guests, or cancel the
            check-in?
            """.trimIndent()
        )
    }

    onResponse<Cancel> {
        furhat.say("Ok, have a nice day.")
        goto(Idle)
    }

    onResponse<ChangeNumGuests> {
        furhat.say(
            """
            No problem.
            """.trimIndent()
        )

        goto(HowManyGuests)
    }
}

val UserDeniesInfo: State = state(Interaction) {
    onEntry {
        furhat.ask(
                """
                    Without your information I cannot book you in. Are you sure?
                """.trimIndent())
    }

    onResponse<Yes> {
        furhat.say("Alright then, bye!")
        goto(Idle)
    }

    onResponse<No> {
        goto(HowManyGuests)
    }
}