package com.nerdery.jvm.resistance.test

import com.nerdery.jvm.resistance.TournamentRunner
import com.nerdery.jvm.resistance.bots.DoctorBot
import com.nerdery.jvm.resistance.models.Outcome
import com.nerdery.jvm.resistance.models.Patient
import com.nerdery.jvm.resistance.models.PatientOutcome
import com.nerdery.jvm.resistance.models.Prescription
import com.nerdery.jvm.resistance.models.tournament.Entrant
import com.nerdery.jvm.resistance.models.tournament.Tournament
import com.nerdery.jvm.resistance.models.tournament.TownDay
import com.nerdery.jvm.resistance.services.MicrobialSimulationService
import com.nerdery.jvm.resistance.services.ResistanceSimulationService
import spock.lang.Specification

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
class ResistanceSimulationServiceSpec extends Specification {
    def "Days after the apocalypse aren't scored"() {
        given:
        def mockMicrobial = Mock(MicrobialSimulationService)
        def service = new ResistanceSimulationService(mockMicrobial);

        when:
        def patients = [new Patient(101.5f, false),
                        new Patient(101.5f, false),
                        new Patient(101.5f, false),
                        new Patient(101.5f, false)]
        def prescriptions = [new Prescription("test", true, 101.5f, 3),
                             new Prescription("test", true, 101.5f, 3),
                             new Prescription("test", true, 101.5f, 3),
                             new Prescription("test", true, 101.5f, 3)]
        def outcomes = [Outcome.UNLUCKY_VIRAL_ANTIBIOTICS,
                        Outcome.UNLUCKY_VIRAL_ANTIBIOTICS,
                        Outcome.UNLUCKY_VIRAL_ANTIBIOTICS,
                        Outcome.UNLUCKY_VIRAL_ANTIBIOTICS]
        def entrants = [new Entrant(Mock(DoctorBot)),
                        new Entrant(Mock(DoctorBot)),
                        new Entrant(Mock(DoctorBot)),
                        new Entrant(Mock(DoctorBot))]
        def yesterdayOutcome = [new PatientOutcome(patients[0], prescriptions[0], outcomes[0]),
                                new PatientOutcome(patients[1], prescriptions[1], outcomes[1]),
                                new PatientOutcome(patients[2], prescriptions[2], outcomes[2]),
                                new PatientOutcome(patients[3], prescriptions[3], outcomes[3])]
        def townDay = new TownDay(12, entrants, patients)

        then: "Scored outcomes are empty"
        service.runAndScoreDay(yesterdayOutcome, townDay).isEmpty()
    }

    def "The service can run a tournament without crashing or anything and the scores make some sense"() {
        given:
        def tournament = Tournament.builder().entrantPackage(TournamentRunner.TEST_ENTRANT_PACKAGE).build();
        def microbial = new MicrobialSimulationService()
        def service = new ResistanceSimulationService(microbial)

        when:
        service.runTournament(tournament)

        then:
        def entrants = tournament.listScoredEntrants()
        def ordered = true
        for (int i = 1; i < entrants.size() && ordered; i++) {
            ordered = entrants.get(i).getScore() < entrants.get(i - 1).getScore()
        }
        ordered
    }
}
