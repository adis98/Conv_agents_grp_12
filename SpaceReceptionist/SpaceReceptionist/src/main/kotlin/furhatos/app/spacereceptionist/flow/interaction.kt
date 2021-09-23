package furhatos.app.spacereceptionist.flow

import furhatos.nlu.common.*
import furhatos.flow.kotlin.*
import furhatos.app.spacereceptionist.nlu.*
import furhatos.nlu.common.Number
import furhatos.flow.kotlin.onResponse as onResponse

val availablePlaces = 40
var requestNum : Int? = null
var customerName : PersonName? = null
var stayDuration : Duration? = null
var roomType : RoomClass? = null
var corrected_class: String? = null
var suiteRoomCount = 10
var citizenRoomCount = 20
var wishes : MutableList<String>? = mutableListOf()
var activities  = arrayOf<String>("skiing","tennis","badminton","zombie")
var registrations : MutableList<String>? = mutableListOf()


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
        requestNum = it.intent.value
        if(availablePlaces >= requestNum!!){
            goto(RandomQuestion1)
        } else {
            goto(BookingRequestLimitExceeded)
        }
    }

}

val RandomQuestion1: State = state(Interaction){
    onEntry{
        furhat.ask("Great. By the way, would you like to know about the available amenities in your rooms?")
    }
    onResponse<Yes>{
        furhat.say("You're provided a bed, a table, a chair, and a replicator, which allows you to create any dish you want in the comfort of your room")
        goto(FurtherDetails)
    }
    onResponse<No>{
        goto(FurtherDetails)
    }
    onNoResponse { goto(FurtherDetails) }

}

val BookingRequestLimitExceeded: State = state(Interaction){
    onEntry{
        furhat.ask("Unfortunately, we can only accommodate ${availablePlaces} people. Would you like to change the number of guests, or cancel check in?")
    }
    onResponse<Cancel>{
        furhat.say("Alright then. Please tell me if you'd like to start over. Otherwise I wish you a good day")
    }
    onResponse<Change>{
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

val FurtherDetails: State = state(Interaction) {
    onEntry{
        furhat.ask("Now, could you give me your name, how long you intend to stay on the Enterprise, and whether you'd like to stay on our suite-class or citizen-class rooms?")
    }
    onResponse {
        customerName = it.findFirst(PersonName())
        stayDuration = it.findFirst(Duration())
        roomType = it.findFirst(RoomClass())
        goto(CheckDetails)
    }

}

val FurtherDetails1: State = state(Interaction) {
    onEntry{
        furhat.ask("Sorry, I couldn't get your name. Could you say it again")
    }
    onResponse<PersonName>{
        customerName = it.findFirst(PersonName())
        goto(CheckDetails)
    }

}

val FurtherDetails2: State = state(Interaction) {
    onEntry{
        furhat.ask("Sorry, I couldn't get the stay duration. Could you say it again")
    }
    onResponse<Duration>{
        stayDuration = it.findFirst(Duration())
        goto(CheckDetails)
    }
}

val FurtherDetails3: State = state(Interaction) {
    onEntry{
        furhat.ask("Could you tell me whether you prefer suite-class or citizen class?")
    }
    onResponse {
        roomType = it.findFirst(RoomClass())
        print(it.text)
        goto(CheckDetails)
    }
}


val CheckDetails: State = state(Interaction) {
    onEntry{
        if (customerName == null){
            goto(FurtherDetails1)
        }
        if(stayDuration == null){
            goto(FurtherDetails2)
        }
        if(roomType == null){
            goto(FurtherDetails3)
        }
        if(roomType!!.typ.toString() == "sweet"){
            corrected_class = "suite"
        }
        else{
            corrected_class = roomType!!.typ.toString()
        }
        furhat.say("So your order details are:"+ customerName!!.text+", "+"room for "+ requestNum+", "+ stayDuration!!.days.toString()+" days, "+ corrected_class +" class, right?")
        furhat.say("Let me check whether this is feasible... ")
        if(roomType!!.toString() == "citizen"){
            if(citizenRoomCount < requestNum!!){
                goto(StarshipOverloaded)
            }
            goto(SpecificWishes)
        }
        else{
            if(suiteRoomCount*2 < requestNum!!){
                goto(StarshipOverloaded)
            }
            goto(SpecificWishes)
        }
    }
}

val StarshipOverloaded: State = state(Interaction){
    onEntry {
        if(corrected_class == "suite"){
            furhat.say("Unfortunately we do not have enough rooms of this kind for your request. We only have ${suiteRoomCount} rooms, each with a double bed")
        }
        else{
            furhat.say("Unfortunately we do not have enough rooms of this kind for your request. We only have ${citizenRoomCount} rooms, each with a single bed")
        }
        furhat.ask("Would you like to change the number of people you want to check-in?")
    }
    onResponse<Yes>{
        goto(NumberOfPeopleChange)
    }
    onResponse<No>{
        goto(Check_in_cancel)
    }
    onNoResponse {
        goto(Check_in_cancel)
    }
}

val NumberOfPeopleChange: State = state(Interaction){
    onEntry{
        furhat.ask("Wonderful. How many guests would you like to check-in?")
    }
    onResponse<Number>{
        requestNum = it.intent.value
        if(roomType!!.toString() == "citizen"){
            if(citizenRoomCount < requestNum!!){
                goto(StarshipOverloaded)
            }
            goto(SpecificWishes)
        }
        else{
            if(suiteRoomCount*2 < requestNum!!){
                goto(StarshipOverloaded)
            }
            goto(SpecificWishes)
        }
    }
}

val SpecificWishes: State = state(Interaction){
    onEntry {
        furhat.ask("Amazing. The data has been entered to your name. Now before I mention the activities on-board, do you have any wishes during your stay?")
    }
    onResponse<Wish>{
        var temp = it.intent.wish
        if (temp != null) {
            wishes!!.add(temp)
        }
        goto(WishesLoop)
    }
    onResponse<No>{
        goto(NoWishes)
    }
}

val Check_in_cancel: State = state(Interaction){
    onEntry{
        furhat.ask("Alright. Please let me know if you'd like to start over. Otherwise, I wish you a good day")
    }
}

val WishesLoop: State = state(Interaction){
    onEntry{
//        furhat.ask("What other wishes?")
        random(
                {furhat.ask("What other wishes?")},
                {furhat.ask("Any other wishes?")},
                {furhat.ask("What else would you like?")},
                {furhat.ask("Anything else?")}
        )
    }
    onResponse<No>{
        goto(EndOfWishes)
    }
    onResponse {
        var test = it.findFirst(Wish())
        var temp = test.wish
        if (temp != null) {
            wishes?.add(temp)
        }
        reentry()
    }

}




val NoWishes: State = state(Interaction){
    onEntry {
        furhat.say("All right then, lets move on")
        goto(StarshipActivities)
    }

}

val EndOfWishes: State = state(Interaction){
    onEntry {
        furhat.say("All your demands have been noted and will be read by the crew. Let's move on then")
        goto(StarshipActivities)
    }
}

val StarshipActivities: State = state(Interaction){
    onEntry{
        furhat.ask("On Enterprise, we offer Skiing, Tennis, Badminton, and Zombie Survival. Which ones would you like to sign up for today?")
    }
    onReentry {
        furhat.ask("What other activies do you want to sign up for?")
    }

    onResponse<No>{
        goto(EndState)
    }
    onResponse<ActivityQuery>{
        furhat.say("We offer skiing, tennis, badminton, and Zombie Survival")
        reentry()
    }
    onResponse<SigninQuery>{
        if(registrations == null){
            furhat.say("You haven't signed up for anything")
        }
        else{
            var answer = ""
            for (temp in registrations!!){
                answer = answer + ", " + temp
            }
            furhat.say("You've registered for"+answer)
        }
        reentry()
    }

    onResponse {
        var txt = it.text
        var lst = txt.split(" ")
        for (word in lst){
            if (word in activities){
                if(word == "zombie"){
                    registrations!!.add("zombie survival")
                }
                else{
                    registrations!!.add(word)
                }

            }
        }
        reentry()
    }
}

val EndState: State = state(Interaction){
    onEntry {
        furhat.say("Understood. You have now successfully checked in. You will soon be teleported to your room, and your luggage will be delivered to your room. We hope you have a pleasant stay here")
    }
}