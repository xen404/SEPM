package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ArtistDto {


    private Long id;

    private String name;

    private List<Long> eventIds;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistDto artistDto = (ArtistDto) o;
        return id.equals(artistDto.id) &&
            name.equals(artistDto.name) &&
            eventIds.equals(artistDto.eventIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, eventIds);
    }

    @Override
    public String toString() {
        return "ArtistDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", events=" + eventIds +
            '}';
    }

    public static final class ArtistDtoBuilder {
        private Long id;
        private String name;
        private List<Long> eventIds;

        private ArtistDtoBuilder() {
        }

        public static ArtistDtoBuilder anArtistDto() {
            return new ArtistDtoBuilder();
        }

        public ArtistDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ArtistDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ArtistDtoBuilder withEvents(List<Long> eventIds) {
            this.eventIds = eventIds;
            return this;
        }

        public ArtistDto build() {
            ArtistDto artist = new ArtistDto();
            artist.setId(id);
            artist.setName(name);
            artist.setEventIds(eventIds);
            return artist;
        }
    }
}