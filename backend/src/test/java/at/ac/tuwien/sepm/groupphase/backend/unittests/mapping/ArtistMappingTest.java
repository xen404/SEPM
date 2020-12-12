package at.ac.tuwien.sepm.groupphase.backend.unittests.mapping;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapperImpl;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapperImpl;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ArtistMapperImpl.class)
@ActiveProfiles("test")
public class ArtistMappingTest implements TestData {

    private final Artist artist = TestData.getTestArtist();
    @Autowired
    private ArtistMapper artistmapper;

    @Test
    public void givenNothing_whenMapArtistDtoToEntity_thenEntityHasAllProperties() {
        //Act
        ArtistDto artistDto = artistmapper.artistToArtistDto(artist);

        //Assert
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(artistDto.getId()).isEqualTo(ID);
        softly.assertThat(artistDto.getName()).isEqualTo(TEST_ARTIST_NAME);
        softly.assertAll();
    }

    @Test
    public void givenNothing_whenMapListWithTwoArtistEntitiesToSimpleDto_thenGetListWithSizeTwoAndAllProperties() {
        //Assert
        List<Artist> artists = new ArrayList<>();
        artists.add(this.artist);
        artists.add(this.artist);

        //Act & Assert
        List<ArtistDto> artistDtos = artistmapper.artistToArtistDto(artists);
        assertThat(artistDtos.size()).isEqualTo(2);
        ArtistDto artistDto = artistDtos.get(0);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(artistDto.getId()).isEqualTo(ID);
        softly.assertThat(artistDto.getName()).isEqualTo(TEST_ARTIST_NAME);
        softly.assertAll();
    }
}
