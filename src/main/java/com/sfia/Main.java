package com.sfia;
import com.sfia.model.BusTicket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        Main main = new Main();
        int x = 0;
        int invalidTickets = 0;
        Map<String, Integer> totalInvalidViolations = new HashMap<>();
        Map<String, Integer> stringInvalidViolations;

        do {
            String input = getInput();
            if(input.contains("“")) {
                input = input.replaceAll("“", "\"");
            }
            BusTicket busTicket = new ObjectMapper().readValue(input, BusTicket.class);
            System.out.println(busTicket.toString());
            stringInvalidViolations = main.validateTicket(busTicket);

            if (!stringInvalidViolations.isEmpty()) {
                System.err.println("Invalid ticket");
                invalidTickets++;
                totalInvalidViolations = main.addStringViolationsToTotal(totalInvalidViolations,
                        stringInvalidViolations);
            }
            x++;
        } while (x < 5);

        String mostFrequentReason = main.getMostFrequentReason(totalInvalidViolations);
        main.printOutputString(mostFrequentReason, x, invalidTickets);
    }

    private static String getInput() {
        return new Scanner(System.in).nextLine();
    }

    private Map<String, Integer> addStringViolationsToTotal(Map<String, Integer> totalInvalidViolations, Map<String, Integer> invalidViolations) {
        for (Map.Entry<String, Integer> entry : invalidViolations.entrySet()) {
            String invalidReason = entry.getKey();
            totalInvalidViolations = updateInvalidReasons(totalInvalidViolations, invalidReason);
        }
        return totalInvalidViolations;
    }

    private Map<String, Integer> validateTicket(BusTicket ticket) {
        Map<String, Integer> stringInvalidViolations = new HashMap<>();
        String ticketType = ticket.getTicketType();

        if (!validateTicketType(ticketType, ticket.getStartDate())) {
            incrementOrPut(stringInvalidViolations, "ticket type");
        }
        if (ticketType != null && !ticketType.equals("MONTH") && !ticketType.equals("PRIME") &&
                !validateStartDate(ticket.getStartDate())) {
                incrementOrPut(stringInvalidViolations, "start date");
        }
        if (!validatePrice(ticket.getPrice())) {
            incrementOrPut(stringInvalidViolations, "price");
        }
        return stringInvalidViolations;
    }

    private boolean validateTicketType(String ticketType, String startDate) {
        if (ticketType != null) {
            if (ticketType.equals("MONTH") || ticketType.equals("PRIME")) {
                return startDate == null || startDate.isEmpty();
            } else if (ticketType.equals("DAY") || ticketType.equals("WEEK") || ticketType.equals("YEAR")) {
                return startDate != null;
            }
        }
        return false;
    }

    private boolean validateStartDate(String startDate) {
        if (startDate == null) {
            return false;
        }
        try {
            LocalDate date = LocalDate.parse(startDate);
            return !date.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean validatePrice(String price) {
        if (price == null) {
            return false;
        }
        try {
            double priceValue = Double.parseDouble(price);
            return priceValue > 0 && priceValue % 2 == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void incrementOrPut(Map<String, Integer> map, String key) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + 1);
        } else {
            map.put(key, 1);
        }
    }

    private Map<String, Integer> updateInvalidReasons(Map<String, Integer> totalInvalidViolations, String reason) {
        if (totalInvalidViolations.containsKey(reason)) {
            totalInvalidViolations.put(reason, totalInvalidViolations.get(reason) + 1);
        } else {
            totalInvalidViolations.put(reason, 1);
        }
        return totalInvalidViolations;
    }

    private String getMostFrequentReason(Map<String, Integer> totalInvalidViolations) {
        int maxCount = 0;
        String mostFrequentViolation = "No invalid tickets";

        for (Map.Entry<String, Integer> entry : totalInvalidViolations.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequentViolation = entry.getKey();
            }
        }

        return mostFrequentViolation;
    }

    private void printOutputString(String mostFrequentViolation, int x, int invalidTickets) {
        System.out.println("..." + "\n" + "Total={" + x + "}" + "\n" +
                "Valid={" + (x - invalidTickets) + "}" + "\n" + "Most popular violation={" +
                mostFrequentViolation + "}"+ "\n" + "...");
    }
}
