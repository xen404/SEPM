import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Artist} from '../../dtos/artist';
import {ArtistService} from '../../services/artist.service';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from '../../services/auth.service';
import {ActivatedRoute} from '@angular/router';
import {EventService} from '../../services/event.service';
import {EventSimple} from '../../dtos/eventsimple';

@Component({
  selector: 'app-artist-detail',
  templateUrl: './artist-detail.component.html',
  styleUrls: ['./artist-detail.component.css']
})
export class ArtistDetailComponent implements OnInit {

  error: boolean = false;
  errorArtist: string = '';
  artistForm: FormGroup;
  submitted: boolean = false;
  private artists: Artist[];
  events: EventSimple[];
  page: number = 0;
  pageSize: number = 15;
  pages: Array<number>;
  totalElements: number;
  artist: Artist;
  private id: number;
  sub;


  constructor(private artistService: ArtistService, private ngbPaginationConfig: NgbPaginationConfig, private formBuilder: FormBuilder,
              private cd: ChangeDetectorRef, private authService: AuthService,  private route: ActivatedRoute,
              private eventService: EventService) {
    this.artistForm = this.formBuilder.group({
      name: ['', [Validators.required]],
    });
  }

  ngOnInit() {
    this.loadArtist();
    this.sub = this.route.params.subscribe(params => {
      this.id = params['id'];
    });
    this.loadArtistById(this.id);
    this.artistService.getArtistById(this.id).subscribe(data => {
      this.artist = data;
    });
   this.getEvents();
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Loads artist specified id
   * @param id of the artist
   */
  private loadArtistById(id: number) {
    this.artistService.getArtistById(id).subscribe(
      (artist: Artist) => {
        this.artist = artist;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }


  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  /**
   * Loads all artists from BE
   */
  private loadArtist() {
    this.artistService.getArtist().subscribe(
      (artist: Artist[]) => {
        this.artists = artist;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  private getEvents() {
    this.eventService.getEventsByArtist(this.id, this.page).subscribe(data => {
      this.events = data['content'];
      this.pages = new Array(data['totalPages']);
      this.totalElements = data['totalElements'];
      }, error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  setPage(i, event: any) {
    event.preventDefault();
    this.page = i;
    this.getEvents();
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorArtist = error.error.error;
    } else {
      this.errorArtist = error.error;
    }
  }


}
