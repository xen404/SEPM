import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {EventService} from '../../services/event.service';
import {Show} from '../../dtos/show';
import {EventDetail} from '../../dtos/event-detail';
import {DomSanitizer} from '@angular/platform-browser';
import {Merchandise} from '../../dtos/merchandise';
import {MerchandiseService} from '../../services/merchandise.service';
import {formatDate} from '@angular/common';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.css']
})
export class EventDetailsComponent implements OnInit {
  event: EventDetail;
  errorMsg: string;
  error: boolean = false;
  shows: Show[];
  show: Show;
  image: string;
  eventId: number;
  dateStart: string;
  dateEnd: string;
  startTime: string;
  endTime: string;

  merchandiseItems: Merchandise[] = [];

  successCreated: boolean = false;
  successCreatedMessage: string = '';


  constructor(private route: ActivatedRoute,
              private eventService: EventService,
              private sanitizer: DomSanitizer,
              private merchandiseService: MerchandiseService,
              private authService: AuthService,
              private router: Router) {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
  }

  ngOnInit(): void {
    this.eventId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadEventDetails(Number(this.route.snapshot.paramMap.get('id')));
    this.loadMerchandiseItems();
    const successString  = this.route.snapshot.paramMap.get('createdShow');
    if (successString != null) {
      this.successCreated = true;
      this.successCreatedMessage = 'You have successfully added the show: ' + successString;
    }
  }

  private loadEventDetails(id: number) {
    this.eventService.getEventByID(id).subscribe(
      (event: EventDetail) => {
        this.event = event;
        this.dateStart = formatDate(this.event.startDate, 'd. MMMM y', 'en-US');
        this.dateEnd = formatDate(this.event.endDate, 'd. MMMM y', 'en-US');
        this.startTime = this.event.startTime.toString().substring(0, 5);
        this.endTime = this.event.endTime.toString().substring(0, 5);
        this.getImage(this.event.id);
      },
      error => {
        this.defaultServiceErrorHandling(error);
      });
  }

  private loadMerchandiseItems() {
    this.merchandiseService.getMerchandiseForEvent(this.eventId).subscribe(
      data => {
        this.merchandiseItems = data;
        this.getMerchandiseImages();
      }
    );
  }

  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMsg = error.error.error;
    } else {
      this.errorMsg = error.error;
    }
  }

  getImage(id: number) {

    this.eventService.getImages(id).subscribe(
      (res) => {
        this.image = res;
      }
    );
  }

  getSanitizer() {
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.image);
  }

  /**
   * Load images to all displayed merchandise items
   */
  getMerchandiseImages() {
    for (const m of this.merchandiseItems) {
      this.merchandiseService.getImages(m.id).subscribe(
        (res) => {
          this.merchandiseItems.find(x => x.id === m.id).imagePresent = true;
          this.merchandiseItems.find(x => x.id === m.id).image = res;
        }
      );
    }
  }

  /**
   * returns true if the user is a admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  vanishAlert() {
    this.successCreated = false;
    this.router.navigate(['/events-detail/' + this.eventId]);
  }

  private changeDate(date: Date) {
    return formatDate(date, 'd. MMMM y', 'en-US').toString();
  }
}

