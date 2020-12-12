package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table (name = "artist")
public class  Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    @JoinTable(
        name = "Event_Artist_Jtable",
        joinColumns = @JoinColumn(name = "artist_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id"))
    List<Event> events;

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

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Artist artist = (Artist) o;
        return Objects.equals(id, artist.id)
            && Objects.equals(name, artist.name)
            && Objects.equals(events, artist.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, events);
    }

    @Override
    public String toString() {
        return "Artist{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", events=" + events
            + '}';
    }

    public static final class ArtistBuilder {
        private Long id;
        private String name;
        private List<Event> events;

        private ArtistBuilder() {
        }

        public static ArtistBuilder anArtist() {
            return new ArtistBuilder();
        }

        public ArtistBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ArtistBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ArtistBuilder withEvents(List<Event> events) {
            this.events = events;
            return this;
        }

        public Artist build() {
            Artist artist = new Artist();
            artist.setId(id);
            artist.setName(name);
            artist.setEvents(events);
            return artist;
        }
    }
}