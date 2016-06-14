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

/*
* Antibiotic prescription Dr. council criteria
*   *This trial consists of 7 Drs. When a patient comes in, each Dr. will give their decision as to
*   prescribing antibiotics. Majority wins.
*
* Royals
* Moon phases
* Trump trending
* Star Wars character height
* Previous prescriptions
* Patient temp
* Weather conditions
*
* */
public class ThatDrugTrial implements DoctorBot {

    private int antibiotics = 0;
    private int rest = 0;

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
        drHowAreTheRoyalsDoing();
        // If there's a full moon, prescribe anitbiotics to prevent werewolfing (proven to work in 10th century)
        drMoonPhases();
        // Assuming antibiotics are manufactured overseas, if Trump is trending we assume he's shutting down drug shipments.  Ain't no one getting antibiotics
        drGoogleTrends();
        // Randomly retrieve a Star Wars character.  If the person has Little Man's syndrome we should probably give him antibiotics
        drStarWarsPeople();
        // If weather is NOT clear in KC, we're feeling "under the weather".  Give them drugs.
        drCurrentWeather();
        // If people are using too much, ahhh lets do it anyways
        drPastHistoryOfOtherDrs(antiBioticPrescriptions);
        // Just makes sense
        drPatientTemp(patientTemperature);

        if (antibiotics >= rest){
            resetVotes();
            return true;
        } else {
            resetVotes();
            return false;
        }
    }

    private void resetVotes(){
        rest = 0;
        antibiotics = 0;
    }

    private void drPatientTemp(float patientTemperature){
        if (patientTemperature >= 102f) {
            antibiotics ++;
        } else {
            rest ++;
        }
    }

    private void drPastHistoryOfOtherDrs(List antiBioticPrescriptions){
        if (antiBioticPrescriptions.size() >= 2) {
            antibiotics ++;
        } else {
            rest ++;
        }
    }

    private void drHowAreTheRoyalsDoing(){
//        DefaultHttpClient httpclient = new DefaultHttpClient();
//        ObjectMapper objectMapper = new ObjectMapper();
//        String streak;
//
//        try {
//            HttpGet getRequest = new HttpGet("https://erikberg.com/mlb/standings.json");
//            HttpResponse httpResponse = httpclient.execute(getRequest);
//            MlbStandings standings = objectMapper.readValue(httpResponse.getEntity().getContent(), MlbStandings.class);
//            List<TeamStandings> royals = Arrays.stream(standings.getTeamStandings()).filter(t -> "kansas-city-royals".equals(t.getTeamId())).collect(Collectors.toList());
//            streak = royals.get(0).getStreakType();
//        } catch (Exception e){//we'll probably hit api request limit, so just return null
//            streak = null;
//        }
//        if (streak != null && streak.equals("loss")){
//            antibiotics ++;
//        } else {
//            rest ++;
//        }
    }

    private void drMoonPhases(){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String uri = "http://api.usno.navy.mil/moon/phase?date=" + LocalDate.now().format(formatter) + "&nump=4";
        String phaseData;

        try {
            HttpGet getRequest = new HttpGet(uri);
            HttpResponse httpResponse = httpclient.execute(getRequest);
            MoonPhases moonPhases = objectMapper.readValue(httpResponse.getEntity().getContent(), MoonPhases.class);
            phaseData = moonPhases.getPhaseDatas()[0].getPhase();
        } catch (Exception e){
            phaseData = null;
        }
        if (phaseData != null && phaseData.equals("Full Moon")){
            antibiotics ++;
        } else {
            rest ++;
        }
    }

    private void drGoogleTrends(){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        String trends;

        try {
            HttpGet getRequest = new HttpGet("https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&q=http%3A%2F%2Fwww.google.com%2Ftrends%2Fhottrends%2Fatom%2Fhourly");
            HttpResponse httpResponse = httpclient.execute(getRequest);
            GoogleTrends googleTrends = objectMapper.readValue(httpResponse.getEntity().getContent(), GoogleTrends.class);
            trends = googleTrends.getGoogleResponse().getGoogleFeed().getEntries()[0].getContent();
        } catch (Exception e){
            trends = null;
        }
        if (trends != null && !trends.toLowerCase().contains("trump")) {
            antibiotics ++;
        } else {
            rest ++;
        }
    }

    private void drStarWarsPeople(){
        int randomNum = 1 + (int)(Math.random() * 87);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        String starWarsPeopleHeight;

        try {
            HttpGet getRequest = new HttpGet("http://swapi.co/api/people/" + randomNum + "/");
            HttpResponse httpResponse = httpclient.execute(getRequest);
            StarWarsPeople starWarsPeople = objectMapper.readValue(httpResponse.getEntity().getContent(), StarWarsPeople.class);
            starWarsPeopleHeight = starWarsPeople.getHeight();
            if (starWarsPeopleHeight != null && Integer.parseInt(starWarsPeopleHeight) < 170){
                antibiotics ++;
            } else {
                rest ++;
            }
        } catch (Exception e) {
            rest ++;
        }
    }

    private void drCurrentWeather (){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        String weather;

        try {
            HttpGet getRequest = new HttpGet("http://api.openweathermap.org/data/2.5/forecast?lat=39.099727&lon=-94.578567&APPID=d728fdbe10c0047a8d45fd286f6eb1a9");
            HttpResponse httpResponse = httpclient.execute(getRequest);
            WeatherConditions weatherConditions = objectMapper.readValue(httpResponse.getEntity().getContent(), WeatherConditions.class);
            weather = weatherConditions.getWeatherList()[0].getWeatherInfo()[0].getMain();
        } catch (Exception e) {
             weather = null;
        }
        if (weather != null && !weather.equals("Clear")){
            antibiotics ++;
        } else {
            rest ++;
        }
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

@JsonIgnoreProperties(ignoreUnknown = true)
class StarWarsPeople{

    @JsonProperty("height")
    private String height;

    public String getHeight() {
        return height;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WeatherConditions{

    @JsonProperty("list")
    private WeatherAttributesList[] weatherList;

    public WeatherAttributesList[] getWeatherList() {
        return weatherList;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WeatherAttributesList{

    @JsonProperty("weather")
    private WeatherInfo[] weatherInfo;

    public WeatherInfo[] getWeatherInfo() {
        return weatherInfo;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WeatherInfo{

    @JsonProperty("main")
    private String main;

    public String getMain() {
        return main;
    }
}

