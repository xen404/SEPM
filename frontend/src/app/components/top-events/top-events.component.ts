import { Component, OnInit } from '@angular/core';
import {TopEventService} from '../../services/top-event.service';
import {TopEvent} from '../../dtos/top-event';
import {Router} from '@angular/router';

@Component({
  selector: 'app-top-events',
  templateUrl: './top-events.component.html',
  styleUrls: ['./top-events.component.css']
})
export class TopEventsComponent implements OnInit {

  categories: string[];

  currentDate = new Date();
  months: string[] = ['january', 'february' , 'march' , 'april', 'may' , 'june' , 'july' , 'august', 'september' ,  'october' , 'november', 'december'];
  month: string = this.months[this.currentDate.getMonth() - 1];

  topEventData: {[key: string]: TopEvent[]};

  activeData: TopEvent[];

  maxValue: number;

  error: boolean = false;
  errorMessage: string = '';

  constructor(private topEventService: TopEventService, private router: Router) { }

  /**
   * Loads the event categories and the top ten events for each category.
   */
  ngOnInit(): void {
    this.topEventService.getCategories().subscribe(data => {
      this.categories = data;
    }, error => {
        this.defaultServiceErrorHandling(error);
    });

    this.topEventService.getTopEventsPerCategory().subscribe(data => {
      this.topEventData = data;
      this.activeData = this.topEventData[this.categories[0]];
      this.maxValue = this.activeData[0].accumulatedSoldTickets;
    });
  }

  /**
   * Changes the displayed data according to the selected category.
   * @param category specifies the category of interest.
   */
  onClick(category: string) {
    this.activeData = this.topEventData[category];
    this.maxValue = this.activeData[0].accumulatedSoldTickets;
  }

  /**
   * Navigates to the specified event detail page.
   * @param id specifies the event.
   */
  clickBox(id: number) {
    this.router.navigate(['/events-detail/' + id]);
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
}
