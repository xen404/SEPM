import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {Merchandise} from '../dtos/merchandise';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class MerchandiseService {

  private merchandiseBaseUri: string = this.globals.backendUri + '/merchandise';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Get all merchandise items that are available as bonuses for a specified amount of bonus points.
   *
   * @param bonusPoints specifies the upper limit for the bonus points.
   * @param page specifies the page number index
   */
  getAvailableBonus(bonusPoints: number, page: number): Observable<Merchandise[]> {
    const bonus = bonusPoints.toString();
    console.log('Get available merchandise for: ' + bonus + ' bonus points');
    const params = new HttpParams().set('bonus', bonus);
    return this.httpClient.get<Merchandise[]>(this.merchandiseBaseUri + '/available' + '/pages/' + page , {params});
  }

  /**
   * Get all merchandise items that belong to the specified event.
   *
   * @param eventId specifies the event.
   */
  getMerchandiseForEvent(eventId: number): Observable<Merchandise[]> {
    console.log('Get merchandise for event with id: ' + eventId);
    return this.httpClient.get<Merchandise[]>(this.merchandiseBaseUri + '/event/' + eventId);
  }

  /**
   * Get all merchandise items.
   */
  getAllMerchandise(page: number): Observable<Merchandise[]> {
    console.log('Get all merchandise items');
    return this.httpClient.get<Merchandise[]>(this.merchandiseBaseUri + '/pages/' + page);
  }

  /**
   * Get one specified merchandise item.
   * @param id specifies the merchandise item.
   */
  getMerchandiseItemById(id: number): Observable<Merchandise> {
    console.log('Get merchandise item with id: ' + id);
    return this.httpClient.get<Merchandise>(this.merchandiseBaseUri + '/' + id);
  }

  /**
   * Persists merchandise in the backend
   * @param merchandise to persist
   */
  createMerchandise(merchandise: Merchandise): Observable<Merchandise> {
    console.log('Create merchandise item with the title: ' + merchandise.title);
    return this.httpClient.post<Merchandise>(this.merchandiseBaseUri, merchandise);
  }

  /**
   * save image to a specified merchandise item
   * @param id specifies the merchandise item
   * @param formData image
   */
  public uploadImage(id: number, formData) {
    return this.httpClient.post<any>(this.merchandiseBaseUri + '/' + id + '/upload', formData);
  }

  /**
   * Loads index from the backend where the images should be saved
   */
  getIndexForImage(): Observable<number> {
    return this.httpClient.get<number>(this.merchandiseBaseUri + '/index');
  }

  getImages(id: number) {
    console.log('Load image for merchandise item with id: ' + id);
    return this.httpClient.get(this.merchandiseBaseUri + '/' + id + '/image', {responseType: 'text'});
  }
}
