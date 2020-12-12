import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {EventSimple} from '../../dtos/eventsimple';
import {Location} from '../../dtos/location';
import {Seat} from '../../dtos/seat';
import {AuthService} from '../../services/auth.service';
import {ShowService} from '../../services/show.service';
import {EventService} from '../../services/event.service';
import {LocationService} from '../../services/location.service';
import {Show} from '../../dtos/show';
import {Ticket} from '../../dtos/ticket';
import {SaveShow} from '../../dtos/saveShow';
import {Artist} from '../../dtos/artist';
import {EventDetail} from '../../dtos/event-detail';
import {ArtistService} from '../../services/artist.service';

@Component({
  selector: 'app-add-artist',
  templateUrl: './add-artist.component.html',
  styleUrls: ['./add-artist.component.css']
})
export class AddArtistComponent implements OnInit {

  artistForm: FormGroup;
  submitted: boolean = false;
  events: EventSimple[];
  event: EventSimple;


  error: boolean = false;
  errorMessage: string = '';

  constructor(private authService: AuthService,
              private artistService: ArtistService,
              private eventService: EventService) {
    this.artistForm = new FormGroup({
      name: new FormControl('', Validators.required),
      event: new FormControl('', Validators.required)
    });
  }

  ngOnInit(): void {
    this.loadAllEvents();
  }

  /**
   * Loads all (simple) events
   */
  loadAllEvents() {
    this.eventService.getAllSimpleEvents().subscribe(
      (events: EventSimple[]) => {
        this.events = events;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Sends show creation request
   * @param show the show which should be created
   */
  createArtist(artist: Artist) {
    this.artistService.createArtist(artist).subscribe(
      (msg: Artist) => {
        console.log(msg);
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }


  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  private clearForm() {
    this.artistForm.reset();
    this.submitted = false;
  }

}

