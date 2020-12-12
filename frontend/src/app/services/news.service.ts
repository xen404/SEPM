import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {News} from '../dtos/news';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  private newsBaseUri: string = this.globals.backendUri + '/news';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all news from the backend
   */
  getNews(): Observable<News[]> {
    return this.httpClient.get<News[]>(this.newsBaseUri);
  }

  /**
   * Loads seen news from the backend
   */
  getLastUnseenNews(): Observable<News[]> {
    return this.httpClient.get<News[]>(this.newsBaseUri + '/homeunseen');
  }

  getUnseenNews(page: number): Observable<News[]> {
    return this.httpClient.get<News[]>(this.newsBaseUri + '/unseen' + '/pages/' + page);
  }

  getSeenNews(page: number): Observable<News[]> {
    return this.httpClient.get<News[]>(this.newsBaseUri + '/seen' + '/pages/' + page);
  }

  /**
   * Loads specific news from the backend while updating the seen View
   * @param id of news to load
   */
  setSeenNewsById(id: number): Observable<News> {
    console.log('Load news details for ' + id);
    return this.httpClient.get<News>(this.newsBaseUri + '/seen' + '/' + id);
  }

  /**
   * Loads last Image from backend
   */
  getLastImage(): Observable<number> {

    return this.httpClient.get<number>(this.newsBaseUri + '/size');
  }


  /**
   * Loads specific news from the backend
   * @param id of news to load
   */
  getNewsById(id: number): Observable<News> {
    console.log('Load news details for ' + id);
    return this.httpClient.get<News>(this.newsBaseUri + '/' + id);
  }

  getImages(id: number) {
    console.log('Load news details for ' + id);
    return this.httpClient.get(this.newsBaseUri + '/' + id + '/image', {responseType: 'text'});
  }


  /**
   * Persists news to the backend
   * @param news to persist
   */
  createNews(news: News): Observable<News> {
    console.log('Create news with title ' + news.title);
    return this.httpClient.post<News>(this.newsBaseUri, news);
  }

  /**
   * save image
   * @param formData image
   */
  public uploadImage(id: number, formData) {
    return this.httpClient.post<any>(this.newsBaseUri + '/' + id + '/upload', formData);
  }
}
