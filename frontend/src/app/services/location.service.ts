import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Location} from '../dtos/location';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {Seat} from '../dtos/seat';
import {Show} from '../dtos/show';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  private locationBaseUri: string = this.globals.backendUri + '/venues';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all locations from BE
   */
  getLocation(): Observable<Location[]> {
    return this.httpClient.get<Location[]>(this.locationBaseUri);
  }

  /**
   * Loads all locations from BE withe pages
   */
  getLocationPagination(page: number) {
    console.log('Load all locations');
    const params = new HttpParams().set('page', String(page));
    return this.httpClient.get(this.locationBaseUri + '/pagination', {params});
  }

  /**
   * Loads specific location from the backend
   * @param id of location to load
   */
  getLocationById(id: number): Observable<Location> {
    console.log('Load location with this id: ' + id);
    return this.httpClient.get<Location>(this.locationBaseUri + '/' + id);
  }

  /**
   * Load all cities from the BE
   */
  getCities(): Observable<String[]> {
    console.log('Load all cities');
    return this.httpClient.get<String[]>(this.locationBaseUri + '/cities');
  }

  /**
   * Load all countries from the BE
   */
  getCountries(): Observable<String[]> {
    console.log('Load all countries');
    return this.httpClient.get<String[]>(this.locationBaseUri + '/countries');
  }

  /**
   * Load all zipcodes from the BE
   */
  getZipCode(): Observable<String[]> {
    console.log('Load all zipcodes');
    return this.httpClient.get<String[]>(this.locationBaseUri + '/zipCodes');
  }

  getDescription(): Observable<String[]> {
    console.log('Load all descriptions');
    return this.httpClient.get<String[]>(this.locationBaseUri + '/description');
  }

  /**
   * searches after a set of location given the params.
   */
  searchLocation(description: string, city: string, country: string, street: string, zipcode: string, page: number) {
    console.log('Searching for locations');
    const params = new HttpParams().set('description', description).set('city', city).set('country', country).set('street', street).set('zipcode', zipcode).set('page', String(page));
    return this.httpClient.get(this.locationBaseUri + '/search', {params});

  }

  /**
   * Loads all seats of a specific location
   * @param id of the location
   */
  getSeats(id: number): Observable<Seat[]> {
    console.log('Load all seats for location with id: ' + id);
    return this.httpClient.get<Seat[]>(this.locationBaseUri + '/' + id + '/seats');
  }

}
