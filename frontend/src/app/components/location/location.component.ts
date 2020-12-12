import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Location} from '../../dtos/location';
import {LocationService} from '../../services/location.service';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from '../../services/auth.service';


@Component({
  selector: 'app-location',
  templateUrl: './location.component.html',
  styleUrls: ['./location.component.css']
})
export class LocationComponent implements OnInit {

  error: boolean = false;
  errorMessage: string = '';
  locationForm: FormGroup;
  page: number = 0;
  pageSize: number = 15;
  submitted: boolean = false;
  searchLocations: Location[];
  cities: String[];
  countries: String[];
  zipCodes: String[];
  public isCollapsed = true;
  pages: Array<number>;
  paginationLimitUp: number = 15;
  paginationLimitDown: number = 0;
  totalElements: number;
  search: boolean = false;
  description: string;
  city: string;
  country: string;
  street: string;
  zipcode: string;


  constructor(private locationService: LocationService, private ngbPaginationConfig: NgbPaginationConfig, private formBuilder: FormBuilder,
              private cd: ChangeDetectorRef, private authService: AuthService) {
    this.locationForm = this.formBuilder.group({
      description: [''],
      street: [''],
      city: [''],
      country: [''],
      zipcode: [''],
    });
  }

  ngOnInit() {
    this.loadLocationPagination();
    this.loadCountries();
    this.loadZipCodes();
    this.loadCities();
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  /**
   * Loads the specified page of message from the backend
   */
  private loadLocations() {
    this.locationService.getLocation().subscribe(
      (artist: Location[]) => {
        this.searchLocations = artist;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }


  /**
   * Loads the specified page of message from the backend
   */
  private loadCities() {
    this.locationService.getCities().subscribe(
      cities => {
        this.cities = cities;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Loads the specified page of message from the backend
   */
  private loadCountries() {
    this.locationService.getCountries().subscribe(
      countries => {
        this.countries = countries;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  /**
   * Loads the specified page of message from the backend
   */
  private loadZipCodes() {
    this.locationService.getZipCode().subscribe(
      zipCodes => {
        this.zipCodes = zipCodes;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }


  /**
   * searches after a set of locations with specified params.
   */
  private searchLocation(description: string, city: string, country: string, street: string, zipcode: string) {
    this.search = true;
    this.description = description;
    this.city = city;
    this.country = country;
    this.street = street;
    this.zipcode = zipcode;

    if (description === '%') {
      description = '//%';
    }
    if (description === '_') {
      description = '//_';
    }
    if (street === '%') {
      street = '//%';
    }
    if (street === '_') {
      street = '//_';
    }
    if (description === '') {
      description =  '%';
    }
    if (country === '') {
      country = '%';
    }
    if (city === '') {
      city = '%';
    }
    if (street === '') {
      street = '%';
    }
    if (zipcode == null) {
      zipcode = '%';
    }
    this.locationService.searchLocation(description, city, country, street, zipcode, this.page).subscribe(data => {
      this.vanishError();
      this.searchLocations = data['content'];
      this.pages = new Array(data['totalPages']);
      this.totalElements = data['totalElements'];

      }, error => {
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

  /**
   * Loads all the locations from the BE
   */
  private loadLocationPagination() {
    this.search = false;
    this.locationService.getLocationPagination(this.page).subscribe(
      data => {
        this.searchLocations = data['content'];
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
      this.loadLocationPagination();
    } else {
      this.searchLocation(this.description, this.city, this.country, this.street, this.zipcode);
    }
  }

  /**
   * clears the from
   */
  private clearForm() {
    this.locationForm.reset();
    this.submitted = false;
  }

}
