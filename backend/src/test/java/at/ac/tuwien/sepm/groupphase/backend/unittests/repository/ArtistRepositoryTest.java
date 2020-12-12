package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class ArtistRepositoryTest implements TestData {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void givenNothing_whenSaveArtist_thenFindListWithOneElementAndFindArtistById() {
        Artist artist = Artist.ArtistBuilder.anArtist()
            .withName(TEST_ARTIST_NAME)
            .build();

        entityManager.persist(artist);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(artistRepository.findAll().size()).isEqualTo(1);
        softly.assertThat(artistRepository.findById(artist.getId())).isNotNull();
        softly.assertAll();
    }

}
