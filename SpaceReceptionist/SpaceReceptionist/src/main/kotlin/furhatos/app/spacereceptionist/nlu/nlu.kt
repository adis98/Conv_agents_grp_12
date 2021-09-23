package furhatos.app.spacereceptionist.nlu

import furhatos.nlu.ComplexEnumEntity
import furhatos.nlu.Intent
import furhatos.nlu.EnumEntity
import furhatos.nlu.common.Number
import furhatos.nlu.common.PersonName
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

class Cancel : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I'd like to cancel", "Cancel please", "Cancel")
    }
}

class ChangeNumGuests : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "I'd like to modify the number of guests",
            "I want to change the number of guests",
            "Change",
            "Change number of guests"
        )
    }
}

class NumberOfPeople : Intent() {
    var numPeople: Int? = null

    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "@numPeople people",
            "@numPeople"
        )
    }
}

class RoomTypeEntity: EnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf("suite", "citizen")
    }
}

class Details : Intent() {
    var name: PersonName? = null
    var days: Duration? = null
    val roomChoice: RoomTypeEntity? = null

    override fun getExamples(lang: Language): List<String> {
        return listOf(
            "@name @days @roomChoice"
        )
    }
}

class Duration(val days : Number? = Number(2)) : ComplexEnumEntity() {
    override fun getEnum(lang: Language) : List<String> {
        return listOf("@days days", "for @days days")
    }
}

