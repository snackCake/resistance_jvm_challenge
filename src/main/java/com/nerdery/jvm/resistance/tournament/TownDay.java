package com.nerdery.jvm.resistance.tournament;

import com.nerdery.jvm.resistance.models.Patient;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class TownDay {
    private List<Entrant> doctors;
    private Set<Entrant> unscoredDoctors;
    private List<Patient> patients;

    public TownDay(List<Entrant> doctors, Set<Entrant> unscoredDoctors, List<Patient> patients) {
        this.doctors = doctors;
        this.unscoredDoctors = unscoredDoctors;
        this.patients = patients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TownDay townDay = (TownDay) o;
        return Objects.equals(doctors, townDay.doctors) &&
                Objects.equals(unscoredDoctors, townDay.unscoredDoctors) &&
                Objects.equals(patients, townDay.patients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctors, unscoredDoctors, patients);
    }
}
