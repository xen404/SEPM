import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {EventSimple} from '../dtos/eventsimple';
import {News} from '../dtos/news';
import {EventDetail} from '../dtos/event-detail';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private eventsBaseUri: string = this.globals.backendUri + '/events';

  private categoryId: number;
  private categoryName: string;

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * Load all event categories
   */
  getEventCategories() {
    console.log('Sending get request for all events');
    return this.httpClient.get<String[]>(this.eventsBaseUri + '/categories');
  }

  /**
   * Loads all simple events of specified category
   * @param idCategory id of the category
   */
  findAllSimpleEventsByCategory(idCategory: number) {
    console.log('Sending get request for all events with category id = ', idCategory);
    return this.httpClient.get<EventSimple[]>(this.eventsBaseUri + '/category/' + idCategory);
  }

  /**
   * Loads event with specifeid id
   * @param id id of the event
   */
  getEventByID(id: number) {
    console.log('Load Event details with id = ' + id);
    return this.httpClient.get<EventDetail>(this.eventsBaseUri + '/' + id);
  }

  /**
   * Loads specific simple event from the backend
   * @param id of simple event to load
   */
  getSimpleEventById(id: number) {
    console.log('Load simple event with id = ' + id);
    return this.httpClient.get<EventSimple>(this.eventsBaseUri + '/' + id + '/simple');
  }

  /**
   * Loads all (simple) events from the backend
   */
  getAllSimpleEvents() {
    console.log('Load all (simple) events');
    return this.httpClient.get<EventSimple[]>(this.eventsBaseUri + '/simple');
  }

  /**
   * Saves event
   * @param event new Event
   */
  createEvent(event: EventDetail): Observable<EventDetail> {
    console.log('Create event with title = ' + event.title);
    return this.httpClient.post<EventDetail>(this.eventsBaseUri, event);
  }

  /**
   * Loads last Image from backend
   */
  getLastImage(): Observable<number> {
    console.log('Get id of the last image');
    return this.httpClient.get<number>(this.eventsBaseUri + '/size');
  }

  /**
   * save image
   * @param formData image
   */
  public uploadImage(id: number, formData) {
    console.log('Upload image to event ');
    return this.httpClient.post<any>(this.eventsBaseUri + '/' + id + '/upload', formData);
  }

  getImages(id: number) {
    console.log('Get image with id = ' + id);
    return this.httpClient.get(this.eventsBaseUri + '/' + id + '/image', {responseType: 'text'});
  }

  /**
   * Returns id of category
   * @param categoryName category
   */
  getCategoryId(categoryName: string): number {
    switch (categoryName) {
      case '':
        this.categoryId = -1;
        break;
      case 'cabaret':
        this.categoryId = 0;
        break;
      case 'cinema':
        this.categoryId = 1;
        break;
      case 'circus':
        this.categoryId = 2;
        break;
      case 'concert':
        this.categoryId = 3;
        break;
      case 'musical':
        this.categoryId = 4;
        break;
      case 'opera':
        this.categoryId = 5;
        break;
      case  'theatre':
        this.categoryId = 6;
        break;
    }
    return this.categoryId;
  }

  /**
   * Returns name of category
   * @param categoryId category id
   */
  getCategoryString(categoryId: number): string {
    switch (categoryId) {
      case 0:
        this.categoryName = 'cabaret';
        break;
      case 1:
        this.categoryName = 'cinema';
        break;
      case 2:
        this.categoryName = 'circus';
        break;
      case 3:
        this.categoryName = 'concert';
        break;
      case 4:
        this.categoryName = 'musical';
        break;
      case 5:
        this.categoryName = 'opera';
        break;
      case  6:
        this.categoryName = 'theatre';
        break;
    }
    return this.categoryName;
  }

  /**
   * Loads all shows from BE withe pages
   */
  getEventPagination(page: number) {
    console.log('Load all events page: ' + page);
    const params = new HttpParams().set('page', String(page));
    return this.httpClient.get(this.eventsBaseUri + '/pagination', {params});
  }

  /**
   * searching event with param combination
   * @param title of event
   * @param category of event
   * @param duration of event
   * @param page for pagination
   */
  searchEvent(title: string, category: number, duration: number, page: number) {
    console.log('Searching for events with: ', 'title: ' + title, 'category: ' +  category, 'duration: ' +  duration, 'page: ' +  page);
    // tslint:disable-next-line:max-line-length
    const params = new HttpParams().set('title', title).set('duration', String(duration)).set('categoryId', String(category)).set('page', String(page));
    return this.httpClient.get(this.eventsBaseUri + '/search', {params});

  }

  /**
   * get all the events from an artist
   * @param id of artist
   */
  getEventsByArtist(id: number, page: number): Observable<Event[]> {
    console.log('get shows for location with this id:' + id);
    const params = new HttpParams().set('id', String(id)).set('page', String(page));
    return this.httpClient.get<Event[]>(this.eventsBaseUri + '/artist', {params});
  }

}
