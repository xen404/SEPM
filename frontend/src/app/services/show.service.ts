import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {Show} from '../dtos/show';
import {ShowSeat} from '../dtos/show-seat';
import {Price} from '../dtos/price';
import {SaveShow} from '../dtos/saveShow';
import {OrderTickets} from '../dtos/orderTickets';
import {Ticket} from '../dtos/ticket';

@Injectable({
  providedIn: 'root'
})
export class ShowService {

  private showBaseUri: string = this.globals.backendUri + '/shows';


  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads specific show from the backend
   * @param id of show to load
   */
  getShowById(id: number): Observable<Show> {
    console.log('Load show details for ' + id);
    return this.httpClient.get<Show>(this.showBaseUri + '/' + id);
  }

  /**
   * Loads a list of seats with their status for a specific show from the backend
   * @param id of show
   */
  getSeatsOfShow(id: number): Observable<ShowSeat[]> {
    console.log('Load seats of show for ' + id);
    return this.httpClient.get<ShowSeat[]>(this.showBaseUri + '/' + id + '/seats');
  }

  /**
   * Loads a list of prices for a specific show from the backend
   * @param id of the show
   */
  getPricesOfShow(id: number): Observable<Price[]> {
    console.log('Load prices of show for ' + id);
    return this.httpClient.get<Price[]>(this.showBaseUri + '/' + id + '/prices');
  }

  /**
   * Persists a show (and its tickets) to the backend
   * @param saveShow to persist
   */
  createShow(saveShow: SaveShow): Observable<Show> {
    console.log('Create show with title ' + saveShow.show.title);
    return this.httpClient.post<Show>(this.showBaseUri, saveShow);
  }

  /**
   * Loads all shows as page
   * @param page to be shown
   */
  getAllShows(page: number): Observable<Show[]> {
    console.log('Get page nr. ' + page + ' of shows');
    return this.httpClient.get<Show[]>(this.showBaseUri + '/pages/' + page);
  }

  /**
   * Order tickets for a show
   */
  orderTickets(saveTickets: OrderTickets, showId: number): Observable<number[]> {
    console.log('Order tickets for show ' + showId + ' [' + saveTickets.mode + ']');
    return this.httpClient.post<number[]>(this.showBaseUri + '/' + showId + '/order', saveTickets);
  }

  /**
   * Purchase previously reserved tickets
   */
  purchaseReservedTickets(ticketIds: number[]): Observable<number> {
    console.log('Purchase previously reserved tickets ' + ticketIds);
    return this.httpClient.post<number>(this.showBaseUri + '/purchase-reserved', ticketIds);
  }

  exportPurchasedTicketPdf(orderId: number): Observable<Blob> {
    return this.httpClient.get(this.showBaseUri + '/generatepdf/purchase/' + orderId, {responseType: 'blob'});

  }

  exportCancelTicketPdf(orderId: number, ticketIds:number[], bonusPointsInEuro:number): Observable<Blob> {
      return this.httpClient.post(this.showBaseUri + '/generatepdf/cancel/' + orderId + '/' + bonusPointsInEuro , ticketIds , {responseType: 'blob'});

    }


  cancelTicket(ticketIds: number[]): Observable<number>  {
      console.log('Cancel tickets with order Ids: ', ticketIds);
      return this.httpClient.post<number>(this.showBaseUri + '/cancel-tickets' , ticketIds);
  }

  /**
   * Loads all ticket if specified user
   * @param userId id of specified user
   */
  getAllTicketsByUserId() {
    console.log('Sending get request for all ticket');
    return this.httpClient.get<Ticket[]>(this.showBaseUri + '/my-orders');
  }

  getTicketPagination(page: number) {
    console.log('Load all tickets page: ' + page);
    const params = new HttpParams().set('page', String(page));
    return this.httpClient.get(this.showBaseUri + '/tickets-pagination', {params});
  }

  /**
   * Loads all shows from BE withe pages
   */
  getShowPagination(page: number) {
    console.log('Load all show');
    const params = new HttpParams().set('page', String(page));
    return this.httpClient.get(this.showBaseUri + '/pagination', {params});
  }

  /**
   * searching shows for title
   * @param name of show which is searched
   * @param page page for pagination
   */
  searchShow(title: string, location: string, price: number, date: string, time:string, page: number) {
    console.log('searching shows with this title ' + title + page + location + price);
    const params = new HttpParams().set('title', title).set('location', location).set('price', String(price)).set('date', date).set('time', time).set('page', String(page));
    return this.httpClient.get(this.showBaseUri + '/search', {params});
  }

  /**
   * get all the events from an artist
   * @param id of artist
   */
  getShowsByLocation(id: number, page: number): Observable<Show[]> {
    console.log('get shows for location with this id:' + id);
    const params = new HttpParams().set('id', String(id)).set('page', String(page));
    return this.httpClient.get<Show[]>(this.showBaseUri + '/location', {params});
  }

}
