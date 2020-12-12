package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Show;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * Find all location entries.
     *
     * @return list of all location entries
     */
    List<Location> findAll();

    /**
     * Find all location entries.
     *
     * @param pageable for pagination
     * @return list of all location entries
     */
    Page<Location> findAll(Pageable pageable);

    /**
     * Fin all distinct cities.
     *
     * @return list of cities
     */
    @Query(value = "SELECT DISTINCT city from Location order by city ASC ")
    List<String> getCitiesOrdered();

    /**
     * Find all distinct countries.
     *
     * @return list of countries
     */
    @Query(value = "SELECT DISTINCT country from Location order by country ASC ")
    List<String> getCountriesOrdered();

    /**
     * Find all sitinct zipcodes.
     *
     * @return list of zipcodes.
     */
    @Query(value = "SELECT distinct zipCode from Location order by zipCode ASC")
    List<String> getZipCodeOrdered();

    /**
     * Find all location with the specified params.
     *
     * @param description searched description
     * @param city searched city
     * @param country searched country
     * @param street searched street
     * @param zipcode searched zipcode
     * @param pageable for pagination
     * @return a list of locations wich fits the params
     */
    @Query(value = "SELECT * FROM location l WHERE (l.city ILIKE :city) AND ( l.street ILIKE CONCAT('%',:street,'%')) AND (l.country ILIKE :country) AND (l.description ILIKE CONCAT('%',:description,'%')) AND (l.zip_code ILIKE :zipcode)", nativeQuery = true)
    Page<Location> findByParam(@Param("description") String description, @Param("city") String city, @Param("country") String country, @Param("street") String street, @Param("zipcode") String zipcode, Pageable pageable);

    @Query(value = "SELECT distinct description from Location order by description ASC")
    List<String> getDescriptionOrdered();

}
