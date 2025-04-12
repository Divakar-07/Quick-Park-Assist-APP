package com.qpa.config;

import com.qpa.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class LocationDataInitializer {
    @Autowired
    private LocationService locationService;

    @Bean
    public CommandLineRunner initializeLocationData() {
        return args -> {
            // Your existing stateCityMap from SpotUIController
            Map<String, List<String>> stateCityMap = new HashMap<>() {{
                put("Maharashtra", List.of("Mumbai", "Pune", "Nagpur", "Thane", "Nashik", "Aurangabad", "Solapur", "Bhiwandi",
                        "Amravati", "Kolhapur", "Akola", "Latur", "Nanded", "Ichalkaranji", "Parbhani",
                        "Chandrapur"));
                put("Delhi", List.of("Delhi", "New Delhi"));
                put("Karnataka",
                        List.of("Bangalore", "Mysore", "Hubli–Dharwad", "Davangere", "Bellary", "Shimoga", "Tumkur"));
                put("Telangana", List.of("Hyderabad", "Warangal", "Nizamabad", "Kakinada"));
                put("Tamil Nadu", List.of("Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Tiruppur", "Salem",
                        "Erode", "Tirunelveli"));
                put("West Bengal", List.of("Kolkata", "Bardhaman", "Kharagpur"));
                put("Rajasthan", List.of("Jaipur", "Jodhpur", "Kota", "Bikaner", "Ajmer", "Udaipur", "Alwar"));
                put("Gujarat", List.of("Ahmedabad", "Vadodara", "Rajkot", "Bhavnagar", "Junagadh"));
                put("Uttar Pradesh",
                        List.of("Lucknow", "Kanpur", "Ghaziabad", "Agra", "Meerut", "Varanasi", "Allahabad", "Bareilly",
                                "Moradabad", "Aligarh", "Saharanpur", "Gorakhpur", "Firozabad", "Mathura", "Shahjahanpur",
                                "Rampur", "Hapur", "Noida"));
                put("Madhya Pradesh", List.of("Indore", "Bhopal", "Jabalpur", "Gwalior", "Ratlam", "Rewa"));
                put("Andhra Pradesh", List.of("Visakhapatnam", "Vijayawada", "Guntur", "Nellore", "Anantapur", "Kurnool"));
                put("Bihar", List.of("Patna", "Gaya", "Bhagalpur"));
                put("Haryana", List.of("Faridabad", "Hisar", "Panipat"));
                put("Punjab", List.of("Ludhiana", "Amritsar", "Jalandhar", "Bathinda"));
                put("Jammu and Kashmir", List.of("Srinagar", "Jammu"));
                put("Jharkhand", List.of("Dhanbad", "Ranchi", "Jamshedpur"));
                put("Chhattisgarh", List.of("Raipur", "Bhilai", "Bilaspur"));
                put("Assam", List.of("Guwahati"));
                put("Chandigarh", List.of("Chandigarh"));
                put("Uttarakhand", List.of("Dehradun"));
                put("Odisha", List.of("Bhubaneswar", "Cuttack", "Rourkela"));
                put("Kerala", List.of("Kochi", "Kollam", "Thrissur"));
            }};

            // Your existing cityPincodeMap from SpotUIController
            Map<String, String> cityPincodeMap = new HashMap<>() {{
                put("Mumbai", "400001");
                put("Delhi", "110001");
                put("Noida", "201304");
                put("Bangalore", "560001");
                put("Hyderabad", "500001");
                put("Chennai", "600001");
                put("Kolkata", "700001");
                put("Pune", "411001");
                put("Jaipur", "302001");
                put("Ahmedabad", "380001");
                put("Lucknow", "226001");
                put("Kanpur", "208001");
                put("Nagpur", "440001");
                put("Indore", "452001");
                put("Thane", "400601");
                put("Bhopal", "462001");
                put("Visakhapatnam", "530001");
                put("Patna", "800001");
                put("Vadodara", "390001");
                put("Ghaziabad", "201001");
                put("Ludhiana", "141001");
                put("Agra", "282001");
                put("Nashik", "422001");
                put("Faridabad", "121001");
                put("Meerut", "250001");
                put("Rajkot", "360001");
                put("Varanasi", "221001");
                put("Srinagar", "190001");
                put("Aurangabad", "431001");
                put("Dhanbad", "826001");
                put("Amritsar", "143001");
                put("Allahabad", "211001");
                put("Ranchi", "834001");
                put("Coimbatore", "641001");
                put("Jabalpur", "482001");
                put("Gwalior", "474001");
                put("Vijayawada", "520001");
                put("Jodhpur", "342001");
                put("Madurai", "625001");
                put("Raipur", "492001");
                put("Kota", "324001");
                put("Guwahati", "781001");
                put("Chandigarh", "160001");
                put("Solapur", "413001");
                put("Hubli–Dharwad", "580001");
                put("Bareilly", "243001");
                put("Moradabad", "244001");
                put("Mysore", "570001");
                put("Tiruchirappalli", "620001");
                put("Tiruppur", "641601");
                put("Dehradun", "248001");
                put("Jalandhar", "144001");
                put("Aligarh", "202001");
                put("Bhubaneswar", "751001");
                put("Salem", "636001");
                put("Warangal", "506001");
                put("Guntur", "522001");
                put("Bhiwandi", "421308");
                put("Saharanpur", "247001");
                put("Gorakhpur", "273001");
                put("Bikaner", "334001");
                put("Amravati", "444601");
                put("Noida", "201301");
                put("Jamshedpur", "831001");
                put("Bhilai", "490001");
                put("Cuttack", "753001");
                put("Firozabad", "283203");
                put("Kochi", "682001");
                put("Nellore", "524001");
                put("Bhavnagar", "364001");
                put("Jammu", "180001");
                put("Udaipur", "313001");
                put("Davangere", "577001");
                put("Bellary", "583101");
                put("Kurnool", "518001");
                put("Malegaon", "423203");
                put("Kolhapur", "416001");
                put("Ajmer", "305001");
                put("Anantapur", "515001");
                put("Erode", "638001");
                put("Rourkela", "769001");
                put("Tirunelveli", "627001");
                put("Akola", "444001");
                put("Latur", "413512");
                put("Panipat", "132103");
                put("Mathura", "281001");
                put("Kollam", "691001");
                put("Bilaspur", "495001");
                put("Shimoga", "577201");
                put("Chandrapur", "442401");
                put("Junagadh", "362001");
                put("Thrissur", "680001");
                put("Alwar", "301001");
                put("Bardhaman", "713101");
                put("Kakinada", "533001");
                put("Nizamabad", "503001");
                put("Parbhani", "431401");
                put("Tumkur", "572101");
                put("Hisar", "125001");
                put("Kharagpur", "721301");
                put("Nanded", "431601");
                put("Ichalkaranji", "416115");
                put("Bathinda", "151001");
                put("Shahjahanpur", "242001");
                put("Rampur", "244901");
                put("Ratlam", "457001");
                put("Hapur", "245101");
                put("Rewa", "486001");
                put("New Delhi", "110001");
                put("Gaya", "823001");
                put("Bhagalpur", "812001");
            }};

            // Initialize locations in the database
            locationService.initializeLocations(stateCityMap, cityPincodeMap);
        };
    }
}