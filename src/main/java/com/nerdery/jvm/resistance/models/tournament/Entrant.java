package com.nerdery.jvm.resistance.models.tournament;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
public class Entrant {
    private static final Logger logger = LoggerFactory.getLogger(Entrant.class);
    private DoctorBot doctorBot;
    private List<EntrantGenerationResult> results;

    public Entrant(DoctorBot doctorBot) {
        this.doctorBot = doctorBot;
        this.results = new ArrayList<>();
    }

    public DoctorBot getDoctorBot() {
        return doctorBot;
    }

    public int getScore() {
        return results.stream().mapToInt(EntrantGenerationResult::getScore).sum();
    }

    public int getExtinctionsCaused() {
        return (int) results.stream().filter(EntrantGenerationResult::isZombieApocalypseTriggered).count();
    }

    public void addGenerationResult(EntrantGenerationResult result) {
        this.results.add(result);
    }

    public void resetDoctorBot() {
        try {
            this.doctorBot = this.doctorBot.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            // This really shouldn't happen.
            logger.error("Failed to reset doctor bot: ", e);
        }
    }

    public static EntrantFinder finder() {
        return new EntrantFinder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entrant entrant = (Entrant) o;
        return Objects.equals(doctorBot, entrant.doctorBot) &&
                Objects.equals(results, entrant.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorBot, results);
    }

    @Override
    public String toString() {
        return "Entrant{" +
                "doctorBot=" + doctorBot +
                ", results=" + results +
                '}';
    }

    public static class EntrantFinder {
        private static final Logger logger = LoggerFactory.getLogger(EntrantFinder.class);

        private String searchPackage;

        private EntrantFinder() {
        }

        private List<Entrant> findEntrants(String entrantPackage) {
            Reflections entrantFinder = new Reflections(entrantPackage);
            Set<Class<? extends DoctorBot>> subTypes = entrantFinder.getSubTypesOf(DoctorBot.class);
            return subTypes.stream()
                    .filter(clazz -> !(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())))
                    .map(clazz -> {
                        DoctorBot doctorBot = null;
                        try {
                            doctorBot = clazz.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            logger.error("Error loading bot " + clazz, e);
                        }
                        return doctorBot;
                    })
                    .map(Entrant::new)
                    .collect(Collectors.toList());
        }

        public EntrantFinder searchPackage(String searchPackage) {
            this.searchPackage = searchPackage;
            return this;
        }

        public List<Entrant> find() {
            return findEntrants(searchPackage);
        }
    }
}

