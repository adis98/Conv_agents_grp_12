package furhatos.app.spacereceptionist.flow

import furhatos.nlu.common.*
import furhatos.flow.kotlin.*
import furhatos.app.spacereceptionist.nlu.*
import furhatos.nlu.common.Number
import furhatos.flow.kotlin.onResponse as onResponse

val availablePlaces = 40

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
    onResponse<Number>{
        furhat.say("Cool! Let me run some checks on this.")
        var requestNum : Int? = it.intent.value
        if(availablePlaces >= requestNum!!){
            goto(RandomQuestion1)
        } else {
            goto(BookingRequestLimitExceeded)
        }
    }

}

val RandomQuestion1: State = state(Interaction){
    onEntry{
        furhat.say("Congrats, you passed exercise 1!")
    }
}

val BookingRequestLimitExceeded: State = state(Interaction){
    onEntry{
        furhat.ask("Unfortunately, we can only accomodate ${availablePlaces} people. Would you like to change the number of guests, or cancel check-in?")
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