import {Component, OnInit} from '@angular/core';
import {EventSimple} from '../../dtos/eventsimple';
import {EventService} from '../../services/event.service';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {formatDate} from '@angular/common';

@Component({
  selector: 'app-event-category',
  templateUrl: './event-category.component.html',
  styleUrls: ['./event-category.component.css']
})
export class EventCategoryComponent implements OnInit {
  private simpleEventList: EventSimple[] = [];
  private simpleEventRecent: EventSimple;
  idCategory: number;
  dateList: Date[];
  category: string;

  success: boolean = false;
  successMessage: string = '';

  constructor(private eventService: EventService,
              private route: ActivatedRoute,
              private authService: AuthService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.idCategory = this.eventService.getCategoryId(this.route.snapshot.paramMap.get('category'));
    this.findAllSimpleEventsByCategory(this.idCategory);
    this.category = this.eventService.getCategoryString(this.idCategory);
    const successString  = this.route.snapshot.paramMap.get('createdEvent');
    if (successString != null) {
      this.success = true;
      this.successMessage = 'You have successfully created the event: ' + successString;
    }
  }

  private findAllSimpleEventsByCategory(idCategory: number): void {
    this.eventService.findAllSimpleEventsByCategory(idCategory).subscribe(simpleEventList => {
        this.simpleEventList = simpleEventList;
        this.calculateDates();
    });
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  public calculateDates() {
    this.dateList = [];
    for (let i = 0; i < this.simpleEventList.length; i++) {
      const date = new Date(null, null, null, null, null);
      this.dateList.push(date);
      this.dateList[i].id = this.simpleEventList[i].id;
      this.dateList[i].dateStart = formatDate(this.simpleEventList[i].startDate, 'd. MMMM y', 'en-US');
      this.dateList[i].start = formatDate(this.simpleEventList[i].startDate, 'hh:mm a', 'en-US');
      this.dateList[i].dateEnd = formatDate(this.simpleEventList[i].endDate, 'd. MMMM y', 'en-US');
      this.dateList[i].end = formatDate(this.simpleEventList[i].endDate, 'hh:mm a', 'en-US');
    }
  }

  public  getDateById(idEvent: number): Date {
    return this.dateList.find(elemet => elemet.id === idEvent);
  }

  vanishAlert() {
    this.success = false;
    this.router.navigate(['events-category/' + this.category]);
  }
}

class Date {
  constructor(
    public id: number,
    public dateStart: string,
    public start: string,
    public dateEnd: string,
    public end: string) {
  }
}
