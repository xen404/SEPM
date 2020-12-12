import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Show} from '../../dtos/show';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from '../../services/auth.service';
import {ShowService} from '../../services/show.service';
import {LocationService} from '../../services/location.service';
import {formatDate} from '@angular/common';

@Component({
  selector: 'app-shows',
  templateUrl: './shows.component.html',
  styleUrls: ['./shows.component.css']
})
export class ShowsComponent implements OnInit {
  shows: Show[];
  page: number = 0;
  paginationLimitUp: number = 15;
  paginationLimitDown: number = 0;
  error: boolean = false;
  errorMessage: string = '';
  showForm: FormGroup;
  pageSize: number = 15;
  submitted: boolean = false;
  public searchShows: Show[] = [];
  pages: Array<number>;
  totalElements: number;
  search: boolean = false;
  title: string;
  public isCollapsed = true;
  private locations: String[];
  location: string;
  price: string;
  date: string;
  time: string;
  private priceValue: any = 100;



  constructor(private showService: ShowService, private  locationService: LocationService, private ngbPaginationConfig: NgbPaginationConfig,
              private formBuilder: FormBuilder, private cd: ChangeDetectorRef, private authService: AuthService) {
    this.showForm = this.formBuilder.group({
      title: ['', [Validators.required]],
      location: ['', [Validators.required]],
      price: ['', [Validators.required]],
      date: ['', [Validators.required]],
      time: ['', [Validators.required]],
    });
  }

  ngOnInit() {
    this.loadShowPagination();
    this.loadLocations();
  }

  /**
   * Loads all the shos from the BE
   */
  private loadShowPagination() {
    this.search = false;
    this.showService.getShowPagination(this.page).subscribe(
      data => {
        this.searchShows = data['content'];
        this.pages = new Array(data['totalPages']);
        this.totalElements = data['totalElements'];
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  getSliderValue(event) {
    this.priceValue = event.target.value;
  }

  /**
   * Loads the specified page of message from the backend
   */
  private loadLocations() {
    this.locationService.getDescription().subscribe(
      locations => {
        this.locations = locations;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * searches after an show with the specified param
   * @param title show title
   * @param page number of page
   */
  private searchShow(title: string, location: string, price: string, date: string, time: string) {
    this.vanishError();
    this.search = true;
    this.title = title;
    this.location = location;
    this.price = price;
    this.date = date;
    this.time = time;
    if ( title === '%') {
      title = '//%';
    }
    if ( location === '%') {
      location = '//%';
    }
    if ( price === '%') {
      price = '//%';
    }
    if ( date === '%') {
      date = '//%';
    }
    if ( time === '%') {
      time = '//%';
    }
    if ( title === '_') {
      title = '//_';
    }
    if ( location === '_') {
      location = '//_';
    }
    if ( price === '_') {
      price = '//_';
    }
    if ( date === '_') {
      date = '//_';
    }
    if ( time === '_') {
      time = '//_';
    }

    if (price === '') {
      price = '-1';
    }
    if (date === '') {
      date = '-1';
    }
    if (time === '') {
      time = '-1';
    }
    this.showService.searchShow(title, location, Number(price), date, time, this.page).subscribe(data => {
        this.searchShows = data['content'];
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
    if (i > this.paginationLimitUp) {
      this.paginationLimitUp += 15;
      this.paginationLimitDown += 15;
    }
    if (i < this.paginationLimitDown) {
      this.paginationLimitUp -= 15;
      this.paginationLimitDown -= 15;
    }
    if (this.search === false) {
      this.loadShowPagination();
    } else {
      this.searchShow(this.title, this.location, this.price, this.date, this.time);
    }
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

  /**
   * clears the from.
   */
  private clearFormFilter() {
    this.showForm.controls['price'].reset();
    this.showForm.controls['location'].reset();
    this.showForm.controls['date'].reset();
    this.showForm.controls['time'].reset();
    this.priceValue = 100;
    this.submitted = false;
  }

  private clearForm() {
    this.showForm.reset();
    this.submitted = false;
  }

  private vanishError() {
    this.error = false;
  }

  private changeDate(date: Date) {
    return formatDate(date, 'd. MMMM y', 'en-US').toString();
  }
}
