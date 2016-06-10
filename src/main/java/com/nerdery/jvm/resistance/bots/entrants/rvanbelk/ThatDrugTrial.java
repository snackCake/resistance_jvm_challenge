package com.nerdery.jvm.resistance.bots.entrants.rvanbelk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Prescription;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ThatDrugTrial implements DoctorBot {

    @Override
    public String getUserId() {
        return "rvanbelk";
    }

    @Override
    public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {
        List<Prescription> antiBioticPrescriptions = previousPrescriptions.stream()
                .filter(Prescription::isPrescribedAntibiotics)
                .collect(Collectors.toList());

        // If the Royals are losing, we need drugs
        TeamStandings royals = howAreTheRoyalsDoing();
        if (royals != null && royals.getStreakType().equals("loss")){
            return true;
        }
        // If there's a full moon, prescribe anitbiotics to prevent werewolfing (proven to work in 10th century)
        PhaseData phaseData = moonPhases();
        if (phaseData != null && phaseData.getPhase().equals("Full Moon")){
            return true;
        }
        // Assuming antibiotics are manufactured overseas, if Trump is trending we assume he's shutting down drug shipments.  Aint no one getting antibiotics
        String trends = googleTrends();
        if (trends.toLowerCase().contains("trump")) {
            return false;
        }
        // If people are using too much, ahhh lets do it anyways
        if (antiBioticPrescriptions.size() >= 2) {
            return true;
        }
        // Just makes sense
        if (patientTemperature >= 102f) {
            return true;
        } else { //default to no antibiotics to prevent super strain
            return false;
        }
    }

    private TeamStandings howAreTheRoyalsDoing(){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        List<TeamStandings> royals = null;

        try {
            HttpGet getRequest = new HttpGet("https://erikberg.com/mlb/standings.json");
            HttpResponse httpResponse = httpclient.execute(getRequest);
            MlbStandings standings = objectMapper.readValue(httpResponse.getEntity().getContent(), MlbStandings.class);
            royals = Arrays.stream(standings.getTeamStandings()).filter(t -> "kansas-city-royals".equals(t.getTeamId())).collect(Collectors.toList());
        } catch (Exception e){//we'll probably hit api request limit, so just return null
            return null;
        }
        return royals.get(0);
    }

    private PhaseData moonPhases(){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        PhaseData phases = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String uri = "http://api.usno.navy.mil/moon/phase?date=" + LocalDate.now().format(formatter) + "&nump=4";

        try {
            HttpGet getRequest = new HttpGet(uri);
            HttpResponse httpResponse = httpclient.execute(getRequest);
            MoonPhases moonPhases = objectMapper.readValue(httpResponse.getEntity().getContent(), MoonPhases.class);
            phases = moonPhases.getPhaseDatas()[0];
        } catch (Exception e){
            return null;
        }
        return phases;
    }

    private String googleTrends(){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        String trends = null;

        try {
            HttpGet getRequest = new HttpGet("https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&q=http%3A%2F%2Fwww.google.com%2Ftrends%2Fhottrends%2Fatom%2Fhourly");
            HttpResponse httpResponse = httpclient.execute(getRequest);
            GoogleTrends googleTrends = objectMapper.readValue(httpResponse.getEntity().getContent(), GoogleTrends.class);
            trends = googleTrends.getGoogleResponse().getGoogleFeed().getEntries()[0].getContent();
        } catch (Exception e){
            return null;
        }
        return trends;
    }
}

//MAPPERS
@JsonIgnoreProperties(ignoreUnknown = true)
class MlbStandings{

    @JsonProperty("standing")
    private TeamStandings[] teamStandingses;

    public TeamStandings[] getTeamStandings(){
        return teamStandingses;
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
class TeamStandings{

    @JsonProperty("streak_type")
    private String streakType;

    @JsonProperty("team_id")
    private String teamId;

    public String getTeamId(){
        return teamId;
    }

    public String getStreakType(){
        return streakType;
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
class MoonPhases{

    @JsonProperty("phasedata")
    private PhaseData[] phaseDatas;

    public PhaseData[] getPhaseDatas(){
        return phaseDatas;
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
class PhaseData{

    @JsonProperty("phase")
    private String phase;

    public String getPhase() {
        return phase;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class GoogleTrends{

    @JsonProperty("responseData")
    private GoogleResponse googleResponse;

    public GoogleResponse getGoogleResponse() {
        return googleResponse;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class GoogleResponse{

    @JsonProperty("feed")
    private GoogleFeed googleFeed;

    public GoogleFeed getGoogleFeed() {
        return googleFeed;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class GoogleFeed{

    @JsonProperty("entries")
    private GoogleEntries[] entries;

    public GoogleEntries[] getEntries() {
        return entries;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class GoogleEntries{

    @JsonProperty("content")
    private String content;

    public String getContent() {
        return content;
    }
}
