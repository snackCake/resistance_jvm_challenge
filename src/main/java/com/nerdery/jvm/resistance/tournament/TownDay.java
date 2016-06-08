package com.nerdery.jvm.resistance.tournament;

import com.nerdery.jvm.resistance.models.Patient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class TownDay {

    private int dayNumber;
    private List<Entrant> doctors;
    private List<Patient> patients;

    private TownDay(int theDayNumber, List<Entrant> theDoctors, List<Patient> thePatients) {
        dayNumber = theDayNumber;
        doctors = theDoctors;
        patients = thePatients;
    }

    public static TownDayBuilder builder() {
        return new TownDayBuilder();
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public List<Entrant> getDoctors() {
        return doctors;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    @Override
    public boolean equals(Object atheO) {
        if (this == atheO) return true;
        if (atheO == null || getClass() != atheO.getClass()) return false;
        TownDay aaTownDay = (TownDay) atheO;
        return dayNumber == aaTownDay.dayNumber &&
                Objects.equals(doctors, aaTownDay.doctors) &&
                Objects.equals(patients, aaTownDay.patients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayNumber, doctors, patients);
    }

    public static class TownDayBuilder {

        private List<Entrant> entrants;
        private int dayNumber;

        private TownDayBuilder() {
            entrants = Collections.emptyList();
        }

        public TownDay build() {
            return new TownDay(dayNumber, entrants, buildPatients(entrants.size()));
        }

        private List<Patient> buildPatients(int patientCount) {
            Patient.PatientBuilder patientBuilder = Patient.builder();
            patientBuilder.randomizeNeeded(true);
            return IntStream.of(patientCount)
                    .mapToObj(i -> patientBuilder.build())
                    .collect(Collectors.toList());
        }

        public TownDayBuilder entrants(List<Entrant> entrants) {
            this.entrants = entrants;
            return this;
        }

        public TownDayBuilder dayNumber(int dayNumber) {
            this.dayNumber = dayNumber;
            return this;
        }
    }
}
