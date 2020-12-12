import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PhotosService {

  private photosUri: string = 'https://api.unsplash.com/photos/';
  private photosSearchUri: string = 'https://api.unsplash.com/search/photos/';
  private client_id: string = 'jHZl5qHAcXjKJhF4NLyfR5xlWB7C9vhfji936picDqA';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  getRandomImage(): Observable<any> {
    const params = new HttpParams().set('client_id', this.client_id);
    return this.httpClient.get<any>(this.photosUri + 'random', {params});
  }

  getMatchingImage(searchTerm: string): Observable<any> {
    const params = new HttpParams().set('query', searchTerm)
      .set('client_id', this.client_id);
    return this.httpClient.get<any>(this.photosUri + 'random', {params});
  }
}
