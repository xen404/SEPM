import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Show} from '../../dtos/show';
import {ShowService} from '../../services/show.service';
import {EventService} from '../../services/event.service';
import {EventSimple} from '../../dtos/eventsimple';
import {LocationService} from '../../services/location.service';
import {Location} from '../../dtos/location';
import {Seat} from '../../dtos/seat';
import {Ticket} from '../../dtos/ticket';
import {SaveShow} from '../../dtos/saveShow';
import {ActivatedRoute, Router} from '@angular/router';
import {EventDetail} from '../../dtos/event-detail';

@Component({
  selector: 'app-add-show',
  templateUrl: './add-show.component.html',
  styleUrls: ['./add-show.component.css']
})
export class AddShowComponent implements OnInit {

  @ViewChild('closeModal') closeModal: ElementRef;
  showForm: FormGroup;
  submitted: boolean = false;

  events: EventSimple[];
  locations: Location[];
  eventDetail: EventDetail;
  event: EventSimple;
  duration: number;
  location: Location;
  seats: Seat[];
  detailedSeats: SeatDetailed[] = [];
  containerWidth: number;
  sectors: Sector[];
  sectorRadio = 0;
  currentEventId: number;
  today = new Date();
  detailedSeatsError: boolean = false;
  detailedSeatsErrorMsg: string = '';
  error: boolean = false;
  errorMessage: string = '';
  successCreated: boolean = false;
  successCreatedMessage: string = '';

  constructor(private authService: AuthService,
              private showService: ShowService,
              private eventService: EventService,
              private locationService: LocationService,
              private route: ActivatedRoute,
              private router: Router) {
    this.showForm = new FormGroup({
      title: new FormControl('', Validators.required),
      description: new FormControl('', Validators.required),
      location: new FormControl('', Validators.required),
      start: new FormControl('', Validators.required),
      startDate: new FormControl('',
        Validators.required)
    });
  }

  ngOnInit(): void {
    this.loadAllEvents();
    this.loadAllLocations();
    this.currentEventId = Number(this.route.snapshot.paramMap.get('id'));
    this.getEventById();
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
   * Loads all locations
   */
  loadAllLocations() {
    this.locationService.getLocation().subscribe(
      (locations: Location[]) => {
        this.locations = locations;
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
   * Starts form validation and builds a show dto for sending a creation request if the form is valid.
   * If the procedure was successful, the form will be cleared.
   */
  addShow() {
    this.submitted = true;
    if (this.showForm.valid && this.validateDetailedSeats()) {
      const show: Show = new Show(null,
        this.showForm.controls.title.value,
        this.location,
        this.showForm.controls.description.value,
        this.showForm.controls.start.value,
        '23:55', // real value for end time computed by backend
        this.showForm.controls.startDate.value,
        this.showForm.controls.startDate.value, // real value for start time computed by backend
        this.currentEventId,
        this.eventDetail.duration,
        this.eventDetail.title
      );
      this.createShow(show);
      this.clearForm();
      this.clearValues();
    } else {
      console.log('Invalid input');
    }
  }

  /**
   * Validates detailed seats before show creation
   */
  private validateDetailedSeats() {
    const map = new Map();
    const result = [];
    for (const item of this.sectors) {
      if (!map.has(item.name)) {
        map.set(item.name, true);    // set any value to Map
        result.push(item.name);
      }
    }
    if (result.length !== this.sectors.length) {
      this.detailedSeatsError = true;
      this.detailedSeatsErrorMsg = 'Every sector must have a different name!';
      return false;
    }
    for (const item of this.detailedSeats) {
      if (!this.sectors.some(sector => sector.id === item.sectorId)) {
        this.detailedSeatsError = true;
        this.detailedSeatsErrorMsg = 'Every seat must be assigned to a sector!';
        return false;
      }
    }
    this.detailedSeatsError = false;
    this.detailedSeatsErrorMsg = '';
    return true;
  }

  /**
   * Sends show creation request
   * @param show the show which should be created
   */
  createShow(show: Show) {
    const tickets = [];
    this.detailedSeats.forEach((detailedSeat) => {
      const sector = this.sectors.find((item) => item.id === detailedSeat.sectorId);
      tickets.push(new Ticket(null, detailedSeat.seatId, sector.name, sector.price, null, null, null, null, null, null, null ));
    });

    const saveShow = new SaveShow(show, tickets);

    this.showService.createShow(saveShow).subscribe(
      (msg: Show) => {
        this.closeModal.nativeElement.click();
        this.router.navigate(['/events-detail/' + this.currentEventId, {createdShow: msg.title}]);
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Displays a view to select sectors and prices for the seats of the chosen location
   */
  locationSet(id: number) {
    if (id) {
      this.clearLocationValues();
      this.location = this.locations.find((item) => item.id === Number(this.showForm.controls.location.value));
      this.locationService.getSeats(id).subscribe(
        (seats: Seat[]) => {
          this.seats = seats;
          this.seats.forEach(
            (item) => {
              if (item.realSeat) {
                this.detailedSeats.push(new SeatDetailed(item.id, 0));
              }
            });
          this.containerWidth = (this.seats.length / this.getNumberOfRows(this.seats)) * 1.5;
        },
        error => {
          this.defaultServiceErrorHandling(error);
        }
      );
    }
  }

  /**
   * Calculates the duration of the chosen event
   */
  eventSet(id: number) {
    if (id) {
      this.event = this.events.find((item) => item.id === Number(id));
      this.duration = Number(this.event.duration);
    }
  }

  /**
   * Clears all values
   */
  private clearValues() {
    this.clearLocationValues();
    this.event = null;
  }

  /**
   * Clears location values
   */
  private clearLocationValues() {
    this.seats = [];
    this.detailedSeats = [];
    this.sectors = [];
    this.sectorRadio = 0;
    this.location = null;
  }

  /**
   * Calculates the number of rows of a venue plan (given as list of Seats)
   */
  private getNumberOfRows(seats: Seat[]): number {
    let rowNr = 0;
    seats.forEach(
      (item) => {
        if (item.rowNr > rowNr) {
          rowNr = item.rowNr;
        }
      }
    );

    return rowNr;
  }

  /**
   * Creates a new sector
   */
  addSector() {
    if (!this.sectors || this.sectors.length === 0) {
      this.sectors = [new Sector(1, 'A', 90)];
    } else {
      const last = this.sectors[this.sectors.length - 1];
      const newName = String.fromCharCode(last.name.charCodeAt(0) + 1);
      const newId = last.id + 1;
      this.sectors.push(new Sector(newId, newName, 100));
    }
  }

  /**
   * Removes a sector
   * @param sector to be reomved
   */
  removeSector(sector: Sector) {
    this.sectors = this.sectors.filter((item) => item !== sector);
    this.detailedSeats.filter((item) => item.sectorId === sector.id).forEach(
      (item) => {
        item.sectorId = 0;
      });
    this.sectorRadio = 0;
  }

  /**
   * Adds a seat to the current sector
   * @param seat to add
   */
  selectSeat(seat: Seat) {
    const detailedSeat = this.detailedSeats.find((item) => item.seatId === seat.id);
    detailedSeat.sectorId = this.sectorRadio;
    this.validateDetailedSeats();
  }

  /**
   * Checks whether the seat was already assigned to a sector and returns that sector (or '-' if no sector was assigned)
   * @param seat whose sector to be found
   */
  findSector(seat: Seat): string {
    const detailedSeat = this.detailedSeats.find((item) => item.seatId === seat.id);
    if (detailedSeat) {
      const sector = this.sectors.find((item) => item.id === detailedSeat.sectorId);
      if (sector) {
        return sector.name;
      }
    }
    return '-';
  }

  private clearForm() {
    this.showForm.reset();
    this.submitted = false;
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

  private getEventById() {
    this.eventService.getEventByID(this.currentEventId).subscribe(eventDetail => this.eventDetail = eventDetail);
  }

  vanishAlert() {
    this.successCreated = false;
    this.successCreatedMessage = '';
  }

  createNewDate(date: string): Date {
    const varDate = new Date(date);
    return varDate;
  }

}

class Sector {
  constructor(
    public id: number,
    public name: string,
    public price: number) {
  }
}

class SeatDetailed {
  constructor(
    public seatId: number,
    public sectorId: number) {
  }
}





