package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.github.javafaker.Faker;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class LocationDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_MESSAGES_TO_GENERATE = 10;

    private final LocationRepository locationRepository;

    public LocationDataGenerator(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @PostConstruct
    private void generateMessage() {
        if (locationRepository.findAll().size() > 0) {
            LOGGER.debug("location already generated");
        } else {
            LOGGER.debug("generating {} location entries", NUMBER_OF_MESSAGES_TO_GENERATE);
            /*for (int i = 0; i < NUMBER_OF_MESSAGES_TO_GENERATE; i++) {
                Faker faker = new Faker();
                String city = faker.address().city();
                String country = faker.address().country();
                String street = faker.address().streetName();
                String zipcode = faker.address().zipCode();
                String description = faker.gameOfThrones().house();

                Location location = Location.LocationBuilder.aLocation()
                    .withCity(city)
                    .withCountry(country)
                    .withdescription(description)
                    .withStreet(street)
                    .withZipCode(zipcode)
                    .build();
                LOGGER.debug("saving location {}", location);
                locationRepository.save(location);
            }*/
            Location location1 = Location.LocationBuilder.aLocation()
                .withCity("Wien")
                .withCountry("Österreich")
                .withdescription("MUMOK")
                .withStreet("Museumsstraße")
                .withZipCode("1060")
                .build();
            LOGGER.debug("saving location {}", location1);
            locationRepository.save(location1);
            Location location2 = Location.LocationBuilder.aLocation()
                .withCity("Wien")
                .withCountry("Österreich")
                .withdescription("WUK")
                .withStreet("Währinger Straße 59")
                .withZipCode("1090")
                .build();
            LOGGER.debug("saving location {}", location2);
            locationRepository.save(location2);
            Location location3 = Location.LocationBuilder.aLocation()
                .withCity("Wien")
                .withCountry("Österreich")
                .withdescription("Arena Wien")
                .withStreet("Baumgasse 80")
                .withZipCode("1030")
                .build();
            LOGGER.debug("saving location {}", location3);
            locationRepository.save(location3);
            Location location4 = Location.LocationBuilder.aLocation()
                .withCity("Wien")
                .withCountry("Österreich")
                .withdescription("Rabenhof Theater")
                .withStreet("Rabengasse 3")
                .withZipCode("1030")
                .build();
            LOGGER.debug("saving location {}", location4);
            locationRepository.save(location4);
            Location location5 = Location.LocationBuilder.aLocation()
                .withCity("Wien")
                .withCountry("Österreich")
                .withdescription("Stadthalle Halle 1")
                .withStreet("Roland-Rainer-Platz 1")
                .withZipCode("1150")
                .build();
            LOGGER.debug("saving location {}", location5);
            locationRepository.save(location5);
            Location location6 = Location.LocationBuilder.aLocation()
                .withCity("Wien")
                .withCountry("Österreich")
                .withdescription("Stadthalle Halle 2")
                .withStreet("Roland-Rainer-Platz 1")
                .withZipCode("1150")
                .build();
            LOGGER.debug("saving location {}", location6);
            locationRepository.save(location6);
            Location location7 = Location.LocationBuilder.aLocation()
                .withCity("Wien")
                .withCountry("Österreich")
                .withdescription("Gasometer")
                .withStreet("Guglgasse 6")
                .withZipCode("1110")
                .build();
            LOGGER.debug("saving location {}", location7);
            locationRepository.save(location7);


            Location location8 = Location.LocationBuilder.aLocation()
                .withCity("Graz")
                .withCountry("Österreich")
                .withdescription("Music-House")
                .withStreet("Mondscheingasse 9")
                .withZipCode("8010")
                .build();
            LOGGER.debug("saving location {}", location8);
            locationRepository.save(location8);
            Location location9 = Location.LocationBuilder.aLocation()
                .withCity("Graz")
                .withCountry("Österreich")
                .withdescription("PPC")
                .withStreet("Neubaugasse")
                .withZipCode("8020")
                .build();
            LOGGER.debug("saving location {}", location9);
            locationRepository.save(location9);
            Location location10 = Location.LocationBuilder.aLocation()
                .withCity("Graz")
                .withCountry("Österreich")
                .withdescription("Stadthalle")
                .withStreet("Messeplatz 1")
                .withZipCode("8010")
                .build();
            LOGGER.debug("saving location {}", location10);
            locationRepository.save(location10);
            Location location11 = Location.LocationBuilder.aLocation()
                .withCity("Graz")
                .withCountry("Österreich")
                .withdescription("Orpheum")
                .withStreet("Orpheumgasse 8")
                .withZipCode("8020")
                .build();
            LOGGER.debug("saving location {}", location11);
            locationRepository.save(location11);
            Location location12 = Location.LocationBuilder.aLocation()
                .withCity("Graz")
                .withCountry("Österreich")
                .withdescription("Dom im Berg")
                .withStreet("Schlossbergplatz")
                .withZipCode("8010")
                .build();
            LOGGER.debug("saving location {}", location12);
            locationRepository.save(location12);

            Location location13 = Location.LocationBuilder.aLocation()
                .withCity("Salzburg")
                .withCountry("Österreich")
                .withdescription("Salzburgarena")
                .withStreet("Am Messezentrum 1")
                .withZipCode("5020")
                .build();
            LOGGER.debug("saving location {}", location13);
            locationRepository.save(location13);
            Location location14 = Location.LocationBuilder.aLocation()
                .withCity("Salzburg")
                .withCountry("Österreich")
                .withdescription("Messezentrum")
                .withStreet("Am Messezentrum 1")
                .withZipCode("8010")
                .build();
            LOGGER.debug("saving location {}", location14);
            locationRepository.save(location14);
            Location location15 = Location.LocationBuilder.aLocation()
                .withCity("Salzburg")
                .withCountry("Österreich")
                .withdescription("Schauspielhaus Salzburg")
                .withStreet("Erzabt-Klotz-Str. 22")
                .withZipCode("5020")
                .build();
            LOGGER.debug("saving location {}", location15);
            locationRepository.save(location15);


            Location location16 = Location.LocationBuilder.aLocation()
                .withCity("Linz")
                .withCountry("Österreich")
                .withdescription("Tabakfabrik Linz")
                .withStreet("Untere Donaulände 74")
                .withZipCode("4020")
                .build();
            LOGGER.debug("saving location {}", location16);
            locationRepository.save(location16);
            Location location17 = Location.LocationBuilder.aLocation()
                .withCity("Linz")
                .withCountry("Österreich")
                .withdescription("Landestheater Linz")
                .withStreet("Promenade 39")
                .withZipCode("4020")
                .build();
            LOGGER.debug("saving location {}", location17);
            locationRepository.save(location17);

            Location location18 = Location.LocationBuilder.aLocation()
                .withCity("St. Pöten")
                .withCountry("Österreich")
                .withdescription("Greenpark St.Pölten")
                .withStreet("Kelsengasse 9")
                .withZipCode("3100")
                .build();
            LOGGER.debug("saving location {}", location18);
            locationRepository.save(location18);
            Location location19 = Location.LocationBuilder.aLocation()
                .withCity("St. Pöten")
                .withCountry("Österreich")
                .withdescription("Cinema Paradiso St. Pölten")
                .withStreet("Rathausplatz 14")
                .withZipCode("3100")
                .build();
            LOGGER.debug("saving location {}", location19);
            locationRepository.save(location19);

            Location location20 = Location.LocationBuilder.aLocation()
                .withCity("Klagenfurt")
                .withCountry("Österreich")
                .withdescription("Konzerthaus Klagenfurt")
                .withStreet("Mießtaler Straße 8")
                .withZipCode("9020")
                .build();
            LOGGER.debug("saving location {}", location20);
            locationRepository.save(location20);
            Location location21 = Location.LocationBuilder.aLocation()
                .withCity("Klagenfurt")
                .withCountry("Österreich")
                .withdescription("Wörthersee Stadion")
                .withStreet("Südring 207")
                .withZipCode("9020")
                .build();
            LOGGER.debug("saving location {}", location21);
            locationRepository.save(location21);

            Location location22 = Location.LocationBuilder.aLocation()
                .withCity("Innsbruck")
                .withCountry("Österreich")
                .withdescription("Olympia Halle")
                .withStreet("Olympiastrasse 10")
                .withZipCode("6010")
                .build();
            LOGGER.debug("saving location {}", location22);
            locationRepository.save(location22);
            Location location23 = Location.LocationBuilder.aLocation()
                .withCity("Innsbruck")
                .withCountry("Österreich")
                .withdescription("Tiroler Landestheater")
                .withStreet("Rennweg 2")
                .withZipCode("6010")
                .build();
            LOGGER.debug("saving location {}", location23);
            locationRepository.save(location23);

            Location location24 = Location.LocationBuilder.aLocation()
                .withCity("Eisenstadt")
                .withCountry("Österreich")
                .withdescription("Schloss Esterhazy")
                .withStreet("Esterhazyplatz 1")
                .withZipCode("7000")
                .build();
            LOGGER.debug("saving location {}", location24);
            locationRepository.save(location24);

            Location location25 = Location.LocationBuilder.aLocation()
                .withCity("Bregenz")
                .withCountry("Österreich")
                .withdescription("Seebühne Bregenz")
                .withStreet("Platz der Wiener Symphoniker 1")
                .withZipCode("6900")
                .build();
            LOGGER.debug("saving location {}", location25);
            locationRepository.save(location25);

            Location location26 = Location.LocationBuilder.aLocation()
                .withCity("Berlin")
                .withCountry("Deutschland")
                .withdescription("Theater unterm Turm")
                .withStreet("Düsseldorfer Str. 2")
                .withZipCode("10719")
                .build();
            LOGGER.debug("saving location {}", location26);
            locationRepository.save(location26);
            Location location27 = Location.LocationBuilder.aLocation()
                .withCity("Berlin")
                .withCountry("Deutschland")
                .withdescription("ufaFabrik")
                .withStreet("Viktoriastraße 10 - 18")
                .withZipCode("12105")
                .build();
            LOGGER.debug("saving location {}", location27);
            locationRepository.save(location27);
            Location location28 = Location.LocationBuilder.aLocation()
                .withCity("Berlin")
                .withCountry("Deutschland")
                .withdescription("Verti Music Hall")
                .withStreet("Mercedes-Platz")
                .withZipCode("10243 ")
                .build();
            LOGGER.debug("saving location {}", location28);
            locationRepository.save(location28);
            Location location29 = Location.LocationBuilder.aLocation()
                .withCity("Berlin")
                .withCountry("Deutschland")
                .withdescription("Stage Theater des Westens")
                .withStreet("Kantstraße 12")
                .withZipCode("10623")
                .build();
            LOGGER.debug("saving location {}", location29);
            locationRepository.save(location29);

            Location location30 = Location.LocationBuilder.aLocation()
                .withCity("München")
                .withCountry("Deutschland")
                .withdescription("Backstage Werk")
                .withStreet("Reitknechtstrasse 6")
                .withZipCode("80639")
                .build();
            LOGGER.debug("saving location {}", location30);
            locationRepository.save(location30);
            Location location31 = Location.LocationBuilder.aLocation()
                .withCity("München")
                .withCountry("Deutschland")
                .withdescription("Lustspielhaus")
                .withStreet("Occamstraße 8")
                .withZipCode("80802")
                .build();
            LOGGER.debug("saving location {}", location31);
            locationRepository.save(location31);
            Location location32 = Location.LocationBuilder.aLocation()
                .withCity("München")
                .withCountry("Deutschland")
                .withdescription("STROM")
                .withStreet("Lindwurmstraße 88")
                .withZipCode("80337")
                .build();
            LOGGER.debug("saving location {}", location32);
            locationRepository.save(location32);
        }
    }

}
