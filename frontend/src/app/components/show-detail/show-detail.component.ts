import { Component, OnInit } from '@angular/core';
import * as _ from 'lodash';
import {ActivatedRoute} from '@angular/router';
import {ShowService} from '../../services/show.service';
import {Show} from '../../dtos/show';
import {formatDate} from '@angular/common';
import {LocationService} from '../../services/location.service';
import {EventSimple} from '../../dtos/eventsimple';
import {EventService} from '../../services/event.service';
import {Artist} from '../../dtos/artist';
import {ArtistService} from '../../services/artist.service';
import {flatMap} from 'rxjs/operators';

@Component({
  selector: 'app-show-detail',
  templateUrl: './show-detail.component.html',
  styleUrls: ['./show-detail.component.css']
})
export class ShowDetailComponent implements OnInit {
  error: boolean = false;
  errorMsg: string;
  show: Show;
  startDate: string;
  endDate: string;
  startTime: string;
  endTime: string;
  event: EventSimple;
  artists: Artist[] = [];
  past: boolean = false;

  constructor(private route: ActivatedRoute,
              private showService: ShowService,
              private eventService: EventService) { }

  ngOnInit(): void {
    this.loadShowDetails(Number(this.route.snapshot.paramMap.get('id')));
  }

  /**
   * Loads the specified show details
   * @param id the id of the show which details should be shown
   */
  loadShowDetails(id: number) {
    this.showService.getShowById(id).subscribe(
      (show: Show) => {
        this.show = show;
        this.startDate = formatDate(this.show.startDate, 'd. MMMM y', 'en-US');
        this.endDate = formatDate(this.show.endDate, 'd. MMMM y', 'en-US');
        this.startTime = this.show.startTime.toString().substring(0, 5);
        this.endTime = this.show.endTime.toString().substring(0, 5);
        if (Date.parse(this.show.endDate.toString()) < Date.now()) {
          this.past = true;
        }
        this.eventService.getSimpleEventById(show.eventId).subscribe(
          (event: EventSimple) => {
            this.event = event;
            this.artists = event.artists;
          },
          error => {
            this.defaultServiceErrorHandling(error);
          }
        );
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
      this.errorMsg = error.error.error;
    } else {
      this.errorMsg = error.error;
    }
  }

}
