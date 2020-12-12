import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TopEventService {

  private eventBaseUri: string = this.globals.backendUri + '/events';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * GET: load all valid event categories from the backend
   * @return List<String> containing all the valid event categories
   */
  getCategories(): Observable<string[]> {
    return  this.httpClient.get<string[]>(this.eventBaseUri + '/categories');
  }

  /**
   * GET: load top ten events according to ticket sales of the last month for each category
   * @return Hashmap with key CATEGORY and value the top events in descending order
   */
  getTopEventsPerCategory(): Observable<any> {
    const currentDate = new Date();
    const month = currentDate.getMonth() - 1;
    const params = new HttpParams().set('month', month.toString());
    return this.httpClient.get<any>(this.eventBaseUri + '/top', {params});
  }
}
