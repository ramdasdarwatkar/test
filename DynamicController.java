package com.rd;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DynamicController {

    @PostMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String convertToXML(@RequestBody String input) throws Exception {
        Map<String, Object> outputMap = new HashMap<>();

        // Split the input into lines
        String[] lines = input.split("\n");
        for (String line : lines) {
            // Check for <input> tags and extract name and value
            if (line.contains("<input")) {
                String name = extractValue(line, "name");
                String value = extractValue(line, "value");

                // Add to the output map dynamically
                addToMap(outputMap, name, value);
            }
        }

        // Convert the map to XML
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.writeValueAsString(outputMap);
    }
    
    @PostMapping(value = "/json")
    public String convertToJson(@RequestBody String input) throws Exception {
        Map<String, Object> outputMap = new HashMap<>();

        // Split the input into lines
        String[] lines = input.split("\n");
        for (String line : lines) {
            // Check for <input> tags and extract name and value
            if (line.contains("<input")) {
                String name = extractValue(line, "name");
                String value = extractValue(line, "value");

                // Add to the output map dynamically
                addToMap(outputMap, name, value);
            }
        }

        // Convert the map to XML
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(outputMap);
    }

    private String extractValue(String line, String key) {
        String pattern = key + "=\"([^\"]*)\"";
        var matcher = java.util.regex.Pattern.compile(pattern).matcher(line);
        return matcher.find() ? matcher.group(1) : null;
    }

    private void addToMap(Map<String, Object> map, String name, String value) {
        String[] keys = name.split("\\.");
        Map<String, Object> current = map;

        for (int i = 0; i < keys.length - 1; i++) {
        	if(keys[i].contains("/")) {
        		keys[i] = keys[i].replace("/", "-");
        	}
        	
            current = (Map<String, Object>) current.computeIfAbsent(keys[i], k -> new HashMap<>());
        }

        String finalKey = keys[keys.length - 1];
        if (name.contains("/")) {
            if (!current.containsKey(finalKey)) {
                current.put(finalKey, value);
            }
            
        } else {
            current.put(finalKey, value);
        }
    }
}
