import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {LocationService} from '../../services/location.service';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Location} from '../../dtos/location';
import {Show} from '../../dtos/show';
import {ShowService} from '../../services/show.service';

@Component({
  selector: 'app-location-details',
  templateUrl: './location-details.component.html',
  styleUrls: ['./location-details.component.css']
})
export class LocationDetailsComponent implements OnInit {
  private locations: Location[];
  location: Location;
  private id: number;
  shows: Show[];
  page: number = 0;
  pageSize: number = 15;
  pages: Array<number>;
  paginationLimitUp: number = 15;
  paginationLimitDown: number = 0;
  totalElements: number;
  sub;
  error: boolean = false;
  errorMessage: string = '';

  constructor(private locationService: LocationService, private ngbPaginationConfig: NgbPaginationConfig, private formBuilder: FormBuilder,
              private cd: ChangeDetectorRef, private authService: AuthService, private route: ActivatedRoute,
              private showService: ShowService) {
  }

  ngOnInit(): void {
    this.loadLocation();
    this.sub = this.route.params.subscribe(params => {
      this.id = params['id'];
    },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
    this.loadLocationById(this.id);
    this.locationService.getLocationById(this.id).subscribe(data => {
      this.location = data;
      console.log(data);
    },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
    this.getShows();
  }

  /**
   * Loads all Location from the BE
   */
  private loadLocation() {
    this.locationService.getLocation().subscribe(
      (locations: Location[]) => {
        this.locations = locations;
      },
    );
  }

  /**
   * Loads location for the specified id
   * @param id of the location
   */
  private loadLocationById(id: number) {
    this.locationService.getLocationById(id).subscribe(
      (location: Location) => {
        this.location = location;
      },
    );
  }

  private getShows() {
    this.showService.getShowsByLocation(this.id, this.page).subscribe(data => {
      this.shows = data['content'];
      this.pages = new Array(data['totalPages']);
      this.totalElements = data['totalElements'];
    },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  setPage(i, event: any) {
    event.preventDefault();
    if (i > this.paginationLimitUp) {
      this.paginationLimitUp += 15;
      this.paginationLimitDown += 15;
    }
    if (i < this.paginationLimitDown) {
      this.paginationLimitUp -= 15;
      this.paginationLimitDown -= 15;
    }
    this.page = i;
    this.getShows();
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
