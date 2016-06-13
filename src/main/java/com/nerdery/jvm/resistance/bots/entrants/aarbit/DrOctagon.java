package com.nerdery.jvm.resistance.bots.entrants.aarbit;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.nerdery.jvm.resistance.bots.DoctorBot;
import com.nerdery.jvm.resistance.models.Prescription;

public class DrOctagon implements DoctorBot {

	private final String ACTION_RX = "Rx";
	private final String ACTION_NO = "No";

	private final String TEMP_RANGE_100 = "100";
	private final String TEMP_RANGE_100_101 = "100-101";
	private final String TEMP_RANGE_101_102 = "101-102";
	private final String TEMP_RANGE_102_103 = "102-103";
	private final String TEMP_RANGE_103 = "103";


	@Override
	public String getUserId() {
		return "aarbit";
	}

	@Override
	public boolean prescribeAntibiotic(float patientTemperature, Collection<Prescription> previousPrescriptions) {

		Map<String, Map<String, Map<String, Integer>>> drMap = new LinkedHashMap<>();

		// For each scrip: determine which dr., and record the action result for each temperature.
		for(Prescription scrip:previousPrescriptions) {
			Map<String, Map<String, Integer>> doctor = drMap.get(scrip.getUserId());
			if(null == doctor) {
				doctor = new HashMap<>();
			}

			if(scrip.getTemperature()<100f) {
				updateDocForScripAndTemp(doctor, scrip, TEMP_RANGE_100);
			} else if(scrip.getTemperature()>=100f && scrip.getTemperature()<101f) {
				updateDocForScripAndTemp(doctor, scrip, TEMP_RANGE_100_101);
			} else if(scrip.getTemperature()>=101f && scrip.getTemperature()<102f) {
				updateDocForScripAndTemp(doctor, scrip, TEMP_RANGE_101_102);
			} else if(scrip.getTemperature()>=102f && scrip.getTemperature()<103f) {
				updateDocForScripAndTemp(doctor, scrip, TEMP_RANGE_102_103);
			} else {
				updateDocForScripAndTemp(doctor, scrip, TEMP_RANGE_103);
			}

			drMap.put(scrip.getUserId(), doctor);
		}



		// Use the drMap of scrip results to learn things...
		if(patientTemperature<100f) {
			// Ain't sick, never prescribe
			return false;
		} else if(patientTemperature>=100f && patientTemperature<101f) {
			// 25% chance of bacterial infection
			// If other drs are aggro, don't prescribe
			float rate = getPrescriptionRateForTemp(TEMP_RANGE_100_101, drMap);
			if(rate > .25 && rate < .5) {
				return true;
			}
		} else if(patientTemperature>=101f && patientTemperature<102f) {
			// 50% chance of bacterial infection
			Float prob = getProbabilityOfDisasterForTemp(TEMP_RANGE_101_102, drMap);
			if(null == prob || prob <= .5) {
				return true;
			}
		} else if(patientTemperature>=102f && patientTemperature<103f) {
			// 75% chance of bacterial infection
			Float prob = getProbabilityOfDisasterForTemp(TEMP_RANGE_101_102, drMap);
			if(null == prob || prob <= .6) {
				return true;
			}
		} else {
			// 100% chance of bacterial infection, always prescribe
			return true;
		}

		return false;
	}

	private Float getPrescriptionRateForTemp(String tempRange, Map<String, Map<String, Map<String, Integer>>> drMap) {
		int rxTotal = 0;
		int noTotal = 0;
		for(Map.Entry<String, Map<String, Map<String, Integer>>> doctor: drMap.entrySet()) {
			Map<String, Integer> tempMap = getTemperature(doctor.getValue(), tempRange);
			if(tempMap.containsKey(ACTION_RX)) {
				rxTotal += tempMap.get(ACTION_RX);
			}
			if(tempMap.containsKey(ACTION_NO)) {
				noTotal += tempMap.get(ACTION_NO);
			}
		}
		if(rxTotal == 0 && noTotal == 0) {
			return 0f;
		}
		return (float) (rxTotal/(rxTotal+noTotal));
	}

	private Float getProbabilityOfDisasterForTemp(String tempRange, Map<String, Map<String, Map<String, Integer>>> drMap) {
		Float prob = null;
		for(Map.Entry<String, Map<String, Map<String, Integer>>> doctor: drMap.entrySet()) {
			int rx = 0;
			int no = 0;
			Map<String, Integer> tempMap = getTemperature(doctor.getValue(), tempRange);
			if(tempMap.containsKey(ACTION_RX)) {
				rx = tempMap.get(ACTION_RX);
			}
			if(tempMap.containsKey(ACTION_NO)) {
				no = tempMap.get(ACTION_NO);
			}
			float rate = 0;
			if(rx!=0 && no!=0) {
				rate = rx/(rx+no);
			}
			if(prob == null)
			{
				prob = rate;
			} else {
				prob = prob * rate;
			}
		}
		return prob;
	}

	private Integer getAction(Map<String, Integer> temperature, String actionType) {
		Integer prescription = temperature.get(actionType);
		if(null == prescription) {
			prescription = 0;
		}
		return prescription;
	}

	private Map<String, Integer> getTemperature(Map<String, Map<String, Integer>> doctor, String tempRange) {
		Map<String, Integer> temperature = doctor.get(tempRange);
		if(null == temperature) {
			temperature = new HashMap<>();
		}
		return temperature;
	}

	private void updateDocForScripAndTemp(Map<String, Map<String, Integer>> doctor, Prescription scrip, String tempRange) {
		Map<String, Integer> temperature = getTemperature(doctor, tempRange);

		if(scrip.isPrescribedAntibiotics()) {
			Integer rx = getAction(temperature, ACTION_RX) + 1;
			temperature.put(ACTION_RX, rx);
			doctor.put(tempRange, temperature);
		} else {
			Integer no = getAction(temperature, ACTION_NO) + 1;
			temperature.put(ACTION_NO, no);
			doctor.put(tempRange, temperature);
		}
	}



}
