package furhatos.app.spacereceptionist.flow

import furhatos.nlu.common.*
import furhatos.flow.kotlin.*
import furhatos.app.spacereceptionist.nlu.*
import furhatos.gestures.Gestures
import furhatos.nlu.common.Number
import furhatos.records.Location
import kotlinx.coroutines.runBlocking
import furhatos.flow.kotlin.onResponse as onResponse
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

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


val beginTalking : State = state(Interaction){
    onEntry{
        furhat.glance(Location.RIGHT,duration=1000)
        delay(2000)
        reentry()
    }
    onEvent("stoppertalking"){
        print("event called")
        terminate()
    }
    onEvent("dontlook"){
        furhat.glance(Location.LEFT,duration=2000)
    }

}

val gazeTester: State = state(Interaction){

    onEntry{
        furhat.say("You are now in gaze test 1. I will wait for a few seconds before demonstrating")
        delay(3000)
        parallel() {
            call(beginTalking)
        }
        furhat.say("Noticed that I look away when I start talking? Interesting, right?")
        send("stoppertalking")
        furhat.listen()
    }
    onResponse<Yes>{
        furhat.say("You probably also saw that I stare at you when I expect a response from you. Lets now move on to test 2")
        goto(gazeTester2)

    }
    onResponse{
        furhat.say("Aww, too bad. Lets move on to test 2 then")
        goto(gazeTester2)
    }
}

val gazeTester4: State = state(Interaction){

    onEntry{
        furhat.say("I hope you noticed that I pay close attention to you when you're speaking! Lets go to test 4")
        furhat.say("You are now in gaze test 4. I will wait for a few seconds before demonstrating")
        delay(3000)
        parallel() {
            call(beginTalking)
        }
        furhat.say("This test demonstrates interrupts. Please interrupt me anytime once I start speaking after the delay")
        delay(3000)
        furhat.say("blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah")
        furhat.listen()
    }
    onResponse{
        send("stoppertalking")
        goto(conclusionGazeTest)
    }
    onNoResponse {
        furhat.say("Come on! Please participate. Lets try this again")
        reentry()
    }

}

val conclusionGazeTest: State = state(Interaction){
    onEntry{
        furhat.say("Thank you for participating in this experiment. Lets go back to the reception")
        goto(Start)
    }

}



val gazeTester3: State = state(Interaction){

    onEntry{
        furhat.say("You are now in gaze test 3. I will wait for a few seconds before demonstrating")
        delay(3000)
        parallel() {
            call(beginTalking)
        }
        furhat.say("I will say a sentence now and wait for you to start speaking. Observe my gaze when you start speaking")
        furhat.say("Start speaking now!")
        send("stoppertalking")
        furhat.listen()
    }
    onResponse{
        goto(gazeTester4)
    }
}

val gazeTester2: State = state(Interaction){

    onEntry{
        furhat.say("You are now in gaze test 2. I will wait for a few seconds before demonstrating")
        delay(3000)
        parallel() {
            call(beginTalking)
        }
        furhat.say("Today is a beautiful day, but I wonder if this will continue... let me check")
        furhat.say("Nope. The weather report says that it's gonna snow for the next 5 days.")
        send("stoppertalking")
        delay(3000)
        furhat.say("Now you saw that I continue glancing away when I intend to continue speaking. Lets move to the next test")
        goto(gazeTester3)
    }
}

val Start: State = state(Interaction) {


    onEntry {
        //furhat.cameraFeed.enable()

        //print(furhat.cameraFeed.isOpen())
        //furhat.param.interruptableOnAsk = true
        //furhat.param.interruptableOnSay = true
        //furhat.ask("Hello, how can I help you?")

        furhat.say("Welcome! Before we begin, would you like to try out the emotion recognizer or gaze detector?")
        furhat.listen()
        //send("stoppertalking")
        //furhat.listen() //expecting response so look at user
        //furhat.ask("Hello, how can I help you?")
//        TODO: make custom intents
    }
    onResponse("gaze detector","gaze","gays detector","gas detector"){
        goto(gazeTester)
    }
    onResponse<Yes>{
        call(detectEmotions)
        parallel() {
            call(beginTalking)
        }
        furhat.say("Well, that was an interesting experiment! Back to business now. How can I help you?")
        send("stoppertalking")
        furhat.listen()
    }
    onResponse<No>{
        reentry()
    }
    onReentry { furhat.ask("How can I help you?") }

    onResponse<CheckIn> {
        goto(CheckInIntro)
    }

    onResponse<Confused> {
        goto(RobotIntro)
    }

    onResponse<Greeting> {
        goto(thisState)
    }
}

val detectEmotions = state(Interaction){
    onEntry{
        parallel() {
            call(beginTalking)
        }
        furhat.ask("Shall I detect your emotion now?")
    }
    onResponse<Yes>{
        val url = URL("http://localhost:9999/detect")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET
            //print("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
            furhat.say("$responseMessage")
            /*
            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    val match = regex.find("anger")
                    if (match != null) {
                        furhat.say(match.value)
                    }

                }
            }*/
        }
        reentry()
    }
    onResponse<No>{
        terminate()
    }
}

val RobotIntro = state(Interaction) {
    onEntry {
        parallel() {
            call(beginTalking)
        }
        furhat.say {
            +("""
            Welcome to Starship
            Enterprise. We are currently
            leaving for a 12-day voyage from
            planet Earth to planet Vulkan.
            """.trimIndent())
            +blocking{
                furhat.gesture(Gestures.BigSmile(duration = 2.0), async=false)
            }
                +Gestures.Smile(duration = 6.0)
                +"My name is Data and I am your check-in assistant for today."
        }
        furhat.say("Would you like to check in?")
        send("stoppertalking")
        furhat.listen()
    }
    onReentry {
        parallel() {
            call(beginTalking)
        }
        furhat.ask("Would you like to check in?") }

    onResponse<Yes> { goto(CheckInIntro) }

    onResponse<CheckIn> { goto(CheckInIntro) }
    onResponse<No>{
        parallel() {
            call(beginTalking)
        }
        furhat.say("Goodbye then")
        send("donttalk")
    }
}

val CheckInIntro: State = state(Start) {
    onEntry {
        furhat.ask("""
                    As the travel is longer than two days on our journey to Vulkan,
                    regulation requires we ask a few questions. Is that okay with you?
                    """.trimIndent())
    }
    onReentry { furhat.ask("Due to regulations, I need to ask a few questions. Is that fine?") }

    onResponse<Yes> {
        goto(HowManyGuests)
    }

    onResponse<No> {
        goto(UserDeniesInfo)
    }
}

val HowManyGuests: State = state(Interaction) {
    onEntry {
        furhat.gesture(Gestures.BigSmile(duration=2.0))
        furhat.ask(
            """
                Let's get started then. How many people would you like to check-in?
                """.trimIndent()
        )
    }
    onReentry { furhat.ask("How many would you like to check in?") }
    onResponse<Number>{
        furhat.gesture(Gestures.Smile(duration=1.5))
        parallel(){
            goto(beginTalking)
        }
        furhat.say("Cool! Let me run some checks on this....checking...checking....checking...")
        send("dontlook")
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
        furhat.gesture(Gestures.BrowRaise)
    }
    onReentry { furhat.ask("Would you like to know the available amenities in your rooms?") }
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
        furhat.gesture(Gestures.Oh(duration=4.0), async=true)
        furhat.ask("Unfortunately, we can only accommodate ${availablePlaces} people. Would you like to change the number of guests, or cancel check in?")
    }
    onReentry {
        furhat.ask("Would you like to change the number of guests, or cancel check in?")
    }
    onResponse<Cancel>{
        // furhat.say("Alright then. Please tell me if you'd like to start over. Otherwise I wish you a good day")
        goto(CheckInCancel)
    }
    onResponse<Yes> {
        goto(HowManyGuests)
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
    onReentry {
        furhat.listen()
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
        //furhat.param.interruptableOnAsk = true
// Make Furhat interruptable during all furhat.say(...)
        //furhat.param.interruptableOnSay = true
        furhat.ask("Now, could you give me your name, how long you intend to stay on the Enterprise, and whether you'd like to stay on our suite-class or citizen-class rooms?")
    }
    onReentry {
        furhat.listen()
    }
    onResponse {
        customerName = it.findFirst(PersonName())
        stayDuration = it.findFirst(Duration())
        roomType = it.findFirst(RoomClass())
        goto(CheckDetails)
    }

}

val FurtherDetails1: State = state(Interaction) {//gets the user's name if not received before
    onEntry{
        furhat.ask("Sorry, I couldn't get your name. Could you say it again?")
    }
    onReentry {
        furhat.listen()
    }
    onResponse<PersonName>{
        customerName = it.findFirst(PersonName())
        goto(CheckDetails)
    }

}

val FurtherDetails2: State = state(Interaction) {//gets the stay duration if not received before
    onEntry{
        furhat.ask("Sorry, I couldn't get the stay duration. Could you say it again?")
    }
    onReentry {
        furhat.listen()
    }
    onResponse<Duration>{
        stayDuration = it.findFirst(Duration())
        goto(CheckDetails)
    }
}

val FurtherDetails3: State = state(Interaction) {//gets the room class type if not received before
    onEntry{
        furhat.ask("Could you tell me whether you prefer suite-class or citizen class?")
    }
    onReentry {
        furhat.listen()
    }
    onResponse {
        roomType = it.findFirst(RoomClass())
        print(it.text)
        goto(CheckDetails)
    }
}


val CheckDetails: State = state(Interaction) {//checks the booking details
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
        else if(roomType!!.typ.toString() == "sweet"){
            corrected_class = "suite"
        }
        else{
            corrected_class = roomType!!.typ.toString()
        }
        furhat.say("So your order details are:"+ customerName!!.text+", "+"room for "+ requestNum+", "+ stayDuration!!.days.toString()+" days, "+ corrected_class +" class, right?")
        furhat.say("Let me check whether this is feasible... ")
        if( (corrected_class == "citizen" && citizenRoomCount >= requestNum!!) ||  //checks that the number of people can fit in the number of citizen class rooms
                (corrected_class == "suite" && suiteRoomCount*2 >= requestNum!!))  //each suite-class room can accommodate 2 people
        {
            goto(SpecificWishes)
        }else{
            goto(StarshipOverloaded)
        }
    }
}

val StarshipOverloaded: State = state(Interaction){
    onEntry {
        if(corrected_class == "suite"){
            furhat.say("Unfortunately we do not have enough rooms of this kind for your request. We only have ${suiteRoomCount} suite rooms, each with a double bed. So we can only accomodate ${suiteRoomCount*2} people at most")
        }
        else{
            furhat.say("Unfortunately we do not have enough rooms of this kind for your request. We only have ${citizenRoomCount}  citizen rooms, each with a single bed. So we can only accomodate ${citizenRoomCount} people at most")
        }
        furhat.ask("Would you like to change the number of people you want to check-in?")
    }
    onReentry {
        furhat.listen()
    }
    onResponse<Yes>{
        goto(NumberOfPeopleChange)
    }
    onResponse<No>{
        goto(CheckInCancel)
    }
    onNoResponse {
        goto(CheckInCancel)
    }
}

val NumberOfPeopleChange: State = state(Interaction){
    onEntry{
        furhat.ask("Wonderful. How many guests would you like to check-in?")
    }
    onReentry {
        furhat.listen()
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
        furhat.ask("The data has been entered to your name. Now before I mention the activities on-board, do you have any wishes during your stay?")
    }
    onReentry {
        furhat.listen()
    }
    onResponse<No>{
        goto(NoWishes)
    }
    onResponse<Wish>{
        var temp = it.intent.wish
        if (temp != null) {
            wishes!!.add(temp)
        }
        goto(WishesLoop)
    }

}

val CheckInCancel: State = state(Interaction){
    onEntry{
        furhat.ask("Alright. Please let me know if you'd like to start over. Otherwise, I wish you a good day")
    }
    onReentry {
        furhat.listen()
    }
    onResponse<StartOver>{
        goto(Start)
    }
}

val WishesLoop: State = state(Interaction){ //user mentions wishes
    onEntry{
//        furhat.ask("What other wishes?")
        random(
                {furhat.ask("What other wishes?")},
                {furhat.ask("Any other wishes?")},
                {furhat.ask("What else would you like?")},
                {furhat.ask("Anything else?")}
        )
    }
//    onReentry {
//        furhat.listen()
//    }
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
    onReentry {
        furhat.listen()
    }
}

val EndOfWishes: State = state(Interaction){
    onEntry {
        furhat.say("All your demands have been noted and will be read by the crew. Let's move on then")
        goto(StarshipActivities)
    }
    onReentry {
        furhat.listen()
    }
}

val StarshipActivities: State = state(Interaction){
    onEntry{
        furhat.ask("On Enterprise, we offer Skiing, Tennis, Badminton, and Zombie Survival. Which ones would you like to sign up for today?")
    }
    onReentry {
        random(
            {furhat.ask("Any other activities you want to sign up for?")},
            {furhat.ask("Any other activities?")},
            {furhat.ask("Anything else?")}
        )
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
            if(answer != "")
                furhat.say("You've registered for"+answer)
            else{
                furhat.say("You've not signed up for anything")
            }
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