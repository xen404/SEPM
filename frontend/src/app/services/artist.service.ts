import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Artist} from '../dtos/artist';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {SaveShow} from '../dtos/saveShow';
import {Show} from '../dtos/show';

@Injectable({
  providedIn: 'root'
})
export class ArtistService {

  private artistBaseUri: string = this.globals.backendUri + '/artists';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all artists from BE
   */
  getArtist(): Observable<Artist[]> {
    console.log('Load all artists');
    return this.httpClient.get<Artist[]>(this.artistBaseUri);
  }

  /**
   * Loads all artists from BE withe pages
   */
  getArtistPagination(page: number) {
    console.log('Load all artists');
    const params = new HttpParams().set('page', String(page));
    return this.httpClient.get(this.artistBaseUri + '/pagination', {params});
  }

  /**
   * Loads specific artist from the backend
   * @param id of artist to load
   */
  getArtistById(id: number): Observable<Artist> {
    console.log('Load artist with this id: ' + id);
    return this.httpClient.get<Artist>(this.artistBaseUri + '/' + id);
  }

  /**
   * searching artist for name
   * @param name of artist which is searched
   * @param page page for pagination
   */
  searchArtist(name: string, page: number) {
    console.log('searching artist with this name ' + name + page);
    const params = new HttpParams().set('name', name).set('page', String(page));
    return this.httpClient.get(this.artistBaseUri + '/search', {params});
  }

  createArtist(artist: Artist): Observable<Artist> {
    console.log('Create show with title ' + artist.name);
    return this.httpClient.post<Artist>(this.artistBaseUri, artist);
  }
}
