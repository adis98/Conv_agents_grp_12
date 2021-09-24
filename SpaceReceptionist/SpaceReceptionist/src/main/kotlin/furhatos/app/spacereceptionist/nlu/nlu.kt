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
                "checkin", "check in", "get a room", "book a room"
        )
    }
}

// Wtf is going on intent
class Confused : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Who are you?", "Where am I?", "What?", "What is this?", "What the hell is this?", "What can I do?")
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
        return listOf("@typ rooms","@typ class rooms","@typ classrooms","@typ class","@typ")
    }
}

class Wish(val wish : String? = null) : ComplexEnumEntity(){
    override fun getEnum(lang: Language): List<String> {
        return listOf("Yes, @wish","@wish","Yeah,@wish","Yes @wish","Yeah @wish")
    }
}

class ActivityQuery : Intent(){ //user wants to know what is available
    override fun getExamples(lang: Language): List<String> {
        return listOf("What activities do you offer?","What are the activities?","Could you tell me what activities you have?")
    }
}

class SigninQuery : Intent(){ //when user wants to know what he has signed-up for
    override fun getExamples(lang: Language): List<String> {
        return listOf("What activities have I signed up for?","What have I signed up for?","What have I registered for?","What activities have I registered for?","Which activities have I registered for?")
    }
}

class StartOver : Intent(){ //if user wants to start over
    override fun getExamples(lang: Language): List<String> {
        return listOf("yes, I'd like to start over","start over","startover")
    }
}
