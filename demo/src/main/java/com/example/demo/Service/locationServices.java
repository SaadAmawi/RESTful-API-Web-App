package com.example.demo.Service;

import com.example.demo.ServiceImpl.UserServiceImpl;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

@Service
public class locationServices {
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    File database = new File("C:/Users/saada/Downloads/demo (1)/demo/src/main/java/com/example/demo/Service/GeoLite2-City.mmdb");
    String location = "";
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("RE_POSSIBLE_UNINTENDED_PATTERN")
public String getLocation() {
    try (DatabaseReader dbReader = new DatabaseReader.Builder(database).build()) {
        InetAddress ipAddress = null;
        try {
            //SOME RANDOM PLACE IN AFRICA OR SOMETHING (JUST FOR TESTING PURPOSES) - Saad
//            ipAddress = InetAddress.getByName("102.177.124.0");
            ipAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }


        String address = (ipAddress.toString().split("/")[1]);
        boolean containsPrivateIP = address.contains("192");
        String isPrivateNetwork = new String("ANSWER IS "+ containsPrivateIP);
       logger.info(isPrivateNetwork);
        if(address.contains("192.")||address.contains("172.")||address.contains("10.")){
        location="Private Network";
        return location;
        }

        CityResponse response = null;
        try {
            response = dbReader.city(ipAddress);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (GeoIp2Exception ex) {
            location = "Location Unknown";
            throw new RuntimeException(ex);
        }

        String countryName = response.getCountry().getName();
        String cityName = response.getCity().getName();
        double latitude = response.getLocation().getLatitude();
        double longitude = response.getLocation().getLongitude();
        location = countryName+" "+cityName+" "+"lat:"+latitude+" "+"long: "+longitude;
        System.out.println("Country: " + countryName);
        System.out.println("City: " + cityName);
        System.out.println("Latitude: " + latitude);
        System.out.println("Longitude: " + longitude);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return location;
}

}

