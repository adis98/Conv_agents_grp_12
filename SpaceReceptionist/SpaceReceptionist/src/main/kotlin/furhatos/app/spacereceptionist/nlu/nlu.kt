package furhatos.app.spacereceptionist.nlu

import furhatos.nlu.ComplexEnumEntity
import furhatos.nlu.EnumEntity
import furhatos.nlu.Intent
import furhatos.nlu.TextGenerator
import furhatos.util.Language
import furhatos.nlu.common.Number
import furhatos.nlu.common.PersonName
import org.apache.commons.lang.ObjectUtils


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
        return listOf("I'd like to cancel","cancel","cancel booking","cancel my booking")
    }
}

class Change : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("I'd like to change","change","change the number of bookings")
    }
}

class Duration(val days : Number? = Number(2)) : ComplexEnumEntity() {
    override fun getEnum(lang: Language) : List<String> {
        return listOf("@days days", "for @days days")
    }
}


class RoomTypes : EnumEntity() {
    override fun getEnum(lang: Language): List<String> {
        return listOf("suite","citizen","sweet")
    }
}


class RoomClass(val typ : RoomTypes? = null):ComplexEnumEntity(){
    override fun getEnum(lang: Language): List<String> {
        return listOf("@typ rooms","@typ class rooms","@typ classrooms","@typ class")
    }
}

class Wish(val wish : String? = null) : ComplexEnumEntity(){
    override fun getEnum(lang: Language): List<String> {
        return listOf("Yes, @wish","@wish")
    }
}

class ActivityQuery : Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf("What activities do you offer?","What are the activities?","Could you tell me what activities you have?")
    }
}

class SigninQuery : Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf("What activities have I signed up for?","What have I signed up for?","What have I registered for?","What activities have I registered for?","Which activities have I registered for?")
    }
}
