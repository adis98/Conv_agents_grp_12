package furhatos.app.spacereceptionist.nlu

import furhatos.nlu.Intent
import furhatos.util.Language


// CheckIn Intent
class CheckIn : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like to check in", "I would like to check-in",
                "check-in",
                "checkin"
        )
    }
}

// Wtf is going on intent
class Confused : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Who are you?", "Where am I?", "What?", "What is this?", "What the hell is this?")
    }
}

