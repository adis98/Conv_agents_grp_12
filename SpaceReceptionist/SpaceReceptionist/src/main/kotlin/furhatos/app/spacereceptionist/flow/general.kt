package furhatos.app.spacereceptionist.flow

import furhatos.flow.kotlin.*
import furhatos.util.*



val Idle: State = state {

    init {
        furhat.setVoice(Language.ENGLISH_US, Gender.MALE)
        if (users.count > 0) {
            furhat.attend(users.random)
            goto(Start)
        }
    }

    onEntry {
        furhat.attendNobody()
    }

    onUserEnter {
        furhat.attend(it)
        goto(Start)
    }
}

val Interaction: State = state {

    init {
        furhat.param.noSpeechTimeout = 10000
        furhat.param.interruptableOnAsk = true                  //to make all states interruptible. This can be done individually for each state as well
// Make Furhat interruptable during all furhat.say(...)
        furhat.param.interruptableOnSay = true
        furhat.param.interruptableWithoutIntents = true          //to make states that implement onResponse { } interruptible as well
    }


    onUserLeave(instant = true) {
        if (users.count > 0) {
            if (it == users.current) {
                furhat.attend(users.other)
                goto(Start)
            } else {
                furhat.glance(it)
            }
        } else {
            goto(Idle)
        }
    }

    onUserEnter(instant = true) {
        furhat.glance(it)
    }
    onResponse {
        random (
            { furhat.say("Didn't get you.") },                  //This is the fallback state response when user gives an unexpected response
            { furhat.say("Could you repeat what you said?") }
        )
        reentry()
    }
    onNoResponse {
        random (
            { furhat.say("Didn't hear you.") },                   //fallback state response when user gives no response
            { furhat.say("I can't hear you.") }
        )
        reentry()

    }

}


