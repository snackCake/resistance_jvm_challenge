package com.nerdery.jvm.resistance.bots.entrants.revans

import com.github.kittinunf.fuel.httpPost
import com.nerdery.jvm.resistance.bots.DoctorBot
import com.nerdery.jvm.resistance.models.Prescription
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.SecureRandom

/**
 * An implementation of [DoctorBot] that has a "personality" based on Dr. Gregory House. _It's the next season of House M.D.!_
 *
 * Note: check out [this HipChat room](https://chat.nerdery.com/chat/room/1590) for some interactive fun!
 *
 * @author Ryan Evans (revans@nerdery.com)
 */
class DrHouseBot : DoctorBot {

    companion object {
        private const val HIPCHAT_URL = "http://chat.nerdery.com/v2/room/1590/notification"
        private const val HIPCHAT_TOKEN = "9atggOJWmQARwj1IKpPmdlhkVfju5vo8Y487ytHb"

        private const val START_GIF = "http://images1.fanpop.com/images/photos/2300000/Wpaper-house-md-2308673-1024-768.jpg"
        private const val VICODIN_GIF = "http://nlc.p3k.hu/data/cikk/15/145304/10.jpg"
        private const val STAY_IN_BED_GIF = "https://66.media.tumblr.com/cdc03932b686403d8b4fcbb1d153eef1/tumblr_mzgpg5yaQY1r5jv7ho1_500.gif"
        private const val EYEROLL_GIF = "https://m.popkey.co/f1f4c7/rwN3v.gif"
        private const val PATIENT_GIF = "http://i68.photobucket.com/albums/i14/marchtrpt4bhs/GIFs/tumblr_lk8ucqV08D1qauuneo1_500.gif"
        private const val GONNA_YELL_GIF = "http://img.ifcdn.com/images/77e56c97f61fa4f31e80b4db10203f83958dd5e16c0e99dbfca54a1f07f8ba52_1.gif"
        private const val ARGUMENT_GIF = "http://i570.photobucket.com/albums/ss142/mmntkllr/House-Animation-Oh-My-God-house-md-.gif"
        private const val INTERESTING_GIF = "http://media.tumblr.com/531b30b26a04de9efb7acca04a51fbcf/tumblr_inline_mteafaOkEp1r80p9c.gif"
        private const val IDIOT_COLLEAGUE_GIF = "http://38.media.tumblr.com/1d699b5408a435fcbf5f00d06bb78938/tumblr_msswrgJB1f1sggyx2o1_500.gif"

        private val random = SecureRandom()

        private fun randomBool() = random.nextBoolean()
        private fun randomInt(lower: Int, upper: Int) = lower + random.nextInt((upper - lower) + 1)
    }

    private val house = DrHouse()
    private val sentGifs: MutableSet<String> = mutableSetOf()

    init {
        sendHipChatNotification(
                "The next season of House M.D. is starting! Let's see what happens!",
                START_GIF,
                alwaysSend = true
        )
    }

    override fun getUserId(): String? = "revans"

    override fun prescribeAntibiotic(patientTemp: Float,
                                     yesterdaysScrips: MutableCollection<Prescription>?): Boolean {
        return with(house) {
            wakeUpToMessageFromCuddy(patientTemp, yesterdaysScrips?.toList() ?: emptyList())
            winceInPain()
            if (shouldActuallyGetOutOfBed())
                freakingGoToWork()
            else Decision.DO_NOTHING
        } == Decision.GIVE_DRUGS
    }

    private fun sendHipChatNotification(message: String, imageUrl: String = "", alwaysSend: Boolean = false) {
        if (!alwaysSend && (sentGifs.contains(imageUrl) || randomBool()))
            if (randomInt(1, 1000) <= 950) // allow a small chance that we send anyway
                return // don't send the GIF in order to not SPAM

        // record that we're sending this GIF
        sentGifs.add(imageUrl)

        try {
            val imagePart = if (imageUrl != "") """<img src=\"$imageUrl\">""" else ""
            HIPCHAT_URL
                    .httpPost()
                    .header(Pair("Authorization", "Bearer " + HIPCHAT_TOKEN))
                    .header(Pair("Content-Type", "application/json"))
                    .body("""{"message_format":"html","message":"$imagePart<p>$message</p>"}""")
                    .response()
        } catch(e: Exception) {
            // do nothin'
        }
    }

    private inner class DrHouse() {

        var patientTemp: Float = 0.0f
        var yesterdaysScrips: List<Prescription> = emptyList()

        var painLevel = 0
        var annoyanceLevel = 0
        var vicodinOnHand = 5
        var gotYelledAtByCuddy = false

        fun wakeUpToMessageFromCuddy(patientTemp: Float, yesterdaysScrips: List<Prescription>) {
            this.patientTemp = BigDecimal.valueOf(patientTemp.toDouble()).setScale(1, RoundingMode.HALF_UP).toFloat()
            this.yesterdaysScrips = yesterdaysScrips
        }

        fun currentMood(): Mood = when (painLevel + annoyanceLevel) {
            in 125..200 -> Mood.VERY_BAD
            in 65..125 -> Mood.BAD
            else -> Mood.TOLLERABLE
        }

        fun winceInPain() {
            painLevel += randomInt(10, 30)
            if (painLevel >= 75)
                takeVicodin()
        }

        fun takeVicodin() {
            if (vicodinOnHand > 0) {
                vicodinOnHand--
                painLevel = Math.max(0, painLevel - randomInt(20, 80))

                sendHipChatNotification(
                        "House does love him some Vicodin! Now he only has $vicodinOnHand pills left...",
                        VICODIN_GIF
                )
            }
        }

        fun shouldActuallyGetOutOfBed() = currentMood().let { mood ->
            if (patientTemp > 100.0f
                    && (vicodinOnHand == 0
                    || gotYelledAtByCuddy
                    || mood == Mood.TOLLERABLE
                    || (mood == Mood.BAD && maybe())
                    || patientTemp > 103.0f)) {
                true
            } else {
                sendHipChatNotification(
                        "I'm not even getting out of bed today... this patient with a temp of $patientTemp will probably be fine.",
                        STAY_IN_BED_GIF
                )
                false
            }
        }

        fun freakingGoToWork(): Decision {
            gotYelledAtByCuddy = false
            refreshVicodin()
            mockColleagues()
            return seePatient()
        }

        fun refreshVicodin() {
            if (vicodinOnHand == 0 && (randomBool() || randomBool())) {
                vicodinOnHand = 5
            }
        }

        fun mockColleagues() {
            val colleaguesWithATheory = yesterdaysScrips
                    .filter { it.isPrescribedAntibiotics }
            if (colleaguesWithATheory.count() >= 2) {
                val (temp, colleague) = yesterdaysScrips[randomInt(0, colleaguesWithATheory.size - 1)]
                        .let { Pair(it.temperature, it.userId) }

                annoyanceLevel += randomInt(5, (temp / 10).toInt())

                sendHipChatNotification(
                        """I can't believe "Dr. $colleague" just threw drugs at a patient with a temp of only $temp!""",
                        IDIOT_COLLEAGUE_GIF
                )
            }
        }

        fun rollEyes(times: Int = 1) {
            repeat(times) {
                annoyanceLevel += randomInt(5, 10)
            }

            sendHipChatNotification(
                    "Are you freaking kidding me right now! You actually came in with a temp of $patientTemp???",
                    EYEROLL_GIF
            )
        }

        fun rudelyMockPatient() {
            if (annoyanceLevel < 20) // Feeling more or less calm. Decide to give the patient a break...
                return

            val intensityOfYelling = randomInt(5, 10) * (currentMood().ordinal + 1)

            annoyanceLevel = Math.max(0, annoyanceLevel - randomInt(5, intensityOfYelling))

            sendHipChatNotification(
                    "... Not going to end well for this patient ...",
                    GONNA_YELL_GIF
            )

            if (intensityOfYelling > 10)
                patientComplainsToHospital()
        }

        fun patientComplainsToHospital() {
            annoyanceLevel += randomInt(10, 20)
            gotYelledAtByCuddy = true

            sendHipChatNotification(
                    "Cuddy is alway picking fights about those patients! I swear I only yelled a little.",
                    ARGUMENT_GIF
            )
        }

        fun seePatient(): Decision {
            sendHipChatNotification(
                    "Listening to this patient whine about their temp of $patientTemp makes me want to fall asleep...",
                    PATIENT_GIF
            )
            return when {
                patientTemp < 101.0f -> {
                    rollEyes(2)
                    rudelyMockPatient()
                    giveDrugs(maybe())
                }
                patientTemp < 102.0f -> {
                    rollEyes()
                    rudelyMockPatient()
                    giveDrugs(probably())
                }
                patientTemp < 103.0f -> {
                    rudelyMockPatient()
                    giveDrugs(mostLikely())
                }
                else -> {
                    solveInterestingCase()
                    giveDrugs(true)
                }
            }
        }

        fun giveDrugs(decision: Boolean) = if (decision) Decision.GIVE_DRUGS else Decision.DO_NOTHING

        fun maybe() = randomBool()
        fun probably() = randomBool() || randomBool()
        fun mostLikely() = randomBool() || randomBool() || randomBool()

        fun solveInterestingCase() {
            painLevel = Math.max(0, painLevel - randomInt(10, 40))
            annoyanceLevel = 0

            sendHipChatNotification(
                    "This patient with a temp of $patientTemp will need ALL the drugs.",
                    INTERESTING_GIF
            )
        }
    }

    enum class Decision {
        GIVE_DRUGS,
        DO_NOTHING
    }

    enum class Mood {
        TOLLERABLE,
        BAD,
        VERY_BAD
    }
}